package com.evo.middleware.audit.aspect;

import com.evo.middleware.audit.context.LogRecordContext;
import com.evo.middleware.audit.context.LogRecordRuntimeContext;
import com.evo.middleware.audit.context.LogRecordRuntimeContextSource;
import com.evo.middleware.audit.domain.AuditBean;
import com.evo.middleware.audit.domain.MethodExecuteResult;
import com.evo.middleware.audit.extend.LogRecordCreator;
import com.evo.middleware.audit.parser.template.LogRecordTemplateCollector;
import com.evo.middleware.audit.parser.annotation.LogRecordParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class BaseLogRecordAspect extends LogRecordParser {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private LogRecordRuntimeContextSource logRecordRuntimeContextSource;
	private LogRecordParser parser;
	private LogRecordCreator creator;
	private LogRecordTemplateCollector collector;

	public void setRuntimeContextParser(LogRecordRuntimeContextSource logRecordRuntimeContextSource) {
		this.logRecordRuntimeContextSource = logRecordRuntimeContextSource;
	}

	public void setLogRecordParser(LogRecordParser parser) {
		this.parser = parser;
	}

	public void setLogRecordCreator(LogRecordCreator creator) {
		this.creator = creator;
	}

	public void setLogRecordTemplateCollector(LogRecordTemplateCollector collector) {
		this.collector = collector;
	}

	protected Collection<LogRecordRuntimeContext> getContexts(Class<?> targetClass, Method method) {
		new LogRecordContext().putEmptySpan();
		Collection<LogRecordRuntimeContext> contexts = new ArrayList<>();

		// 获取上下文
		try {
			contexts = logRecordRuntimeContextSource.compute(method, targetClass);
		} catch (Exception e) {
			logger.error("log record context compute exception", e);
		}
		return contexts;
	}

	protected Map<String, String> before(
			Collection<LogRecordRuntimeContext> contexts,
			Class<?> targetClass,
			Method method,
			Object[] args
	) {
		Map<String, String> expressionValues = new HashMap<>();
		try {
			List<String> spElTemplates = collector.getBeforeExecuteTemplate(contexts);
			expressionValues = parser.processBeforeExecute(spElTemplates, targetClass, method, args);
		} catch (Exception e) {
			logger.error("log record parse before function exception", e);
		}
		return expressionValues;
	}

	protected void after(
			Collection<LogRecordRuntimeContext> contexts,
			Class<?> targetClass,
			Method method,
			Object[] args,
			Object ret,
			MethodExecuteResult result,
			Map<String, String> expressionValues
	) throws Throwable {
		try {
			conditionExecute(contexts, method, args, targetClass);
			if (!CollectionUtils.isEmpty(contexts)) {
				recordExecute(ret, method, args, contexts, targetClass,
						result.isSuccess(), result.getErrorMsg(), expressionValues);
			}
		} catch (Exception e) {
			logger.error("log record parse exception", e);
		} finally {
			new LogRecordContext().clear();
		}
		if (result.getThrowable() != null) {
			throw result.getThrowable();
		}
	}

	private void recordExecute(Object ret, Method method, Object[] args, Collection<LogRecordRuntimeContext> contexts,
							   Class<?> targetClass, boolean success, String errorMsg, Map<String, String> lastExpressionValues) {
		for (LogRecordRuntimeContext context : contexts) {
			try {
				String action = context.getAction(success);
				if (StringUtils.isEmpty(action)) {
					//没有日志内容则忽略
					continue;
				}
				//获取需要解析的表达式
				List<String> spElTemplates = collector.getParseTemplates(context, action);
				Map<String, String> expressionValues = super.process(spElTemplates, ret, targetClass, method, args, errorMsg, lastExpressionValues);

				// 记录日志
				AuditBean record = convert2Log(expressionValues, action, context);
				creator.record(record, new LogRecordContext().getVariables());
			} catch (Exception t) {
				logger.error("log record execute exception", t);
			}
		}
	}

	private void conditionExecute(Collection<LogRecordRuntimeContext> contexts, Method method, Object[] args, Class<?> targetClass) {
		try {
			Iterator<LogRecordRuntimeContext> iterator = contexts.iterator();
			while (iterator.hasNext()) {
				LogRecordRuntimeContext context = iterator.next();
				List<String> conditionTemplates = collector.getConditionTemplates(context);
				Map<String, String> conditionValues = super.processCondition(conditionTemplates, targetClass, method, args);
				if (!CollectionUtils.isEmpty(conditionTemplates)) {
					for (Map.Entry<String, String> entry : conditionValues.entrySet()) {
						if (!StringUtils.hasText(entry.getValue())) {
							continue;
						}
						if (!"true".equalsIgnoreCase(entry.getValue())) {
							iterator.remove();
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("log record parse condition exception", e);
		}
	}

}
