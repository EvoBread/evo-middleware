package com.evo.middleware.audit.parser.template;

import com.evo.middleware.audit.context.LogRecordRuntimeContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LogRecordTemplateCollector implements TemplateCollector<LogRecordRuntimeContext>{
	@Override
	public List<String> getParseTemplates(LogRecordRuntimeContext context, String action) {

		List<String> spElTemplates = new ArrayList<>();
		if (StringUtils.hasLength(context.getBizKey())) {
			spElTemplates.add(context.getBizKey());
		}
		if (StringUtils.hasLength(context.getBizNo())) {
			spElTemplates.add(context.getBizNo());
		}
		if (StringUtils.hasLength(action)) {
			spElTemplates.add(action);
		}
		if (StringUtils.hasLength(context.getDetail())) {
			spElTemplates.add(context.getDetail());
		}
		if (StringUtils.hasLength(context.getType())) {
			spElTemplates.add(context.getType());
		}
		if (StringUtils.hasLength(context.getOperation())) {
			spElTemplates.add(context.getOperation());
		}
		if (StringUtils.hasLength(context.getOperator())) {
			spElTemplates.add(context.getOperator());
		}
		return spElTemplates;
	}

	@Override
	public List<String> getConditionTemplates(LogRecordRuntimeContext context) {
		List<String> spElTemplates = new ArrayList<>();
		if (StringUtils.hasLength(context.getCondition())) {
			spElTemplates.add(context.getCondition());
		}
		return spElTemplates;
	}

	@Override
	public List<String> getBeforeExecuteTemplate(Collection<LogRecordRuntimeContext> contexts) {
		List<String> spElTemplates = new ArrayList<>();
		for (LogRecordRuntimeContext context : contexts) {
			//执行之前的函数，失败模版不解析
			List<String> templates = getParseTemplates(context, context.getSuccess());
			if (!CollectionUtils.isEmpty(templates)) {
				spElTemplates.addAll(templates);
			}
		}
		return spElTemplates;
	}
}
