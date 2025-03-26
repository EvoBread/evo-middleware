package com.evo.middleware.audit.parser.el.parser;

import com.evo.middleware.audit.parser.template.TemplatesParser;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;

/**
 * 默认的模板解析器
 */
public abstract class DefaultTemplateValueParser extends AbstractSpELParser implements TemplatesParser, BeanFactoryAware{

	private Map<String, Object> constants;
	private BeanFactory beanFactory;

	public void setConstants(Map<String, Object> constants) {
		this.constants = constants;
	}

	protected Class<?> getTargetClass(Object target) {
		return AopProxyUtils.ultimateTargetClass(target);
	}

	@Override
	public Map<String, String> processCondition(Collection<String> templates,
												Class<?> targetClass, Method method, Object[] args) {
		// 缓存解析式值
		Map<String, String> conditionValues = new TreeMap<>();

		if (CollectionUtils.isEmpty(templates)) {
			return conditionValues;
		}

		// 创建上下文
		EvaluationContext evaluationContext =
				createEvaluationContext(method, args, targetClass, null, null, beanFactory, constants);

		for (String expressionTemplate : templates) {
			if (expressionTemplate.contains("{")) {
				Matcher matcher = pattern.matcher(expressionTemplate);
				while (matcher.find()) {
					String expression = matcher.group(1);
					// 替换El表达式
					expression = convertedToSPEL(expression);
					AnnotatedElementKey annotatedElementKey = new AnnotatedElementKey(method, targetClass);
					// 执行
					String value = doParser(expression, annotatedElementKey, evaluationContext);
					conditionValues.put(expression, value);
				}
			}
		}
		return conditionValues;
	}

	/**
	 * 执行解析模板
	 *
	 * @param templates            模板集合
	 * @param ret                  返回值
	 * @param targetClass          当前对象
	 * @param method               方法
	 * @param args                 参数
	 * @param errorMsg             错误信息
	 * @param lastExpressionValues 上次的解析的结果
	 * @return
	 */
	@Override
	public Map<String, String> process(Collection<String> templates, Object ret, Class<?> targetClass, Method method,
									   Object[] args, String errorMsg, Map<String, String> lastExpressionValues) {
		// 缓存解析式值
		Map<String, String> expressionValues = new HashMap<>();
		// 创建上下文
		EvaluationContext evaluationContext =
				createEvaluationContext(method, args, targetClass, ret, errorMsg, beanFactory, constants);
		// 解析所有模板
		for (String expressionTemplate : templates) {
			// 如果表达式中存在"{"，则表示有解析函数
			if (expressionTemplate.contains("{")) {
				Matcher matcher = pattern.matcher(expressionTemplate);
				StringBuilder parsedStr = new StringBuilder();
				while (matcher.find()) {
					String expression = matcher.group(1);
					// 替换El表达式
					expression = convertedToSPEL(expression);

					AnnotatedElementKey annotatedElementKey = new AnnotatedElementKey(method, targetClass);
					// 执行
					// 如果在前置解析执行的时候已经执行过了，则用前置执行的值
					String value;
					if (StringUtils.hasLength(lastExpressionValues.get(expression))) {
						value = lastExpressionValues.get(expression);
					} else {
						value = doParser(expression, annotatedElementKey, evaluationContext);
					}
					matcher.appendReplacement(parsedStr, value);
				}
				matcher.appendTail(parsedStr);
				expressionValues.put(expressionTemplate, parsedStr.toString());
			} else {
				expressionValues.put(expressionTemplate, expressionTemplate);
			}
		}

		return expressionValues;
	}

	@Override
	public Map<String, String> processBeforeExecute(Collection<String> templates,
													Class<?> targetClass, Method method, Object[] args) {
		// 缓存解析式值
		Map<String, String> expressionValues = new HashMap<>();
		EvaluationContext evaluationContext =
				createEvaluationContext(method, args, targetClass, null, null, beanFactory, constants);

		for (String expressionTemplate : templates) {
			if (expressionTemplate.contains("{")) {
				Matcher matcher = pattern.matcher(expressionTemplate);
				while (matcher.find()) {
					String expression = matcher.group(1);
					boolean canParse = canPreParse(expression);
					if (!canParse) {
						continue;
					}
					// 替换El表达式
					expression = convertedToSPEL(expression);
					AnnotatedElementKey annotatedElementKey = new AnnotatedElementKey(method, targetClass);
					// 执行
					String value = doParser(expression, annotatedElementKey, evaluationContext);
					expressionValues.put(expression, value);
				}
			}
		}
		return expressionValues;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
}
