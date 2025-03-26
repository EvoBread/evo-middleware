package com.evo.middleware.audit.parser.el.parser;

import com.evo.middleware.audit.parser.el.ExpressionConverter;
import com.evo.middleware.audit.parser.template.TemplateExpressionEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 抽象 SPEL 处理器
 */
public abstract class AbstractSpELParser implements SpelParser, SpELEvaluationContextCreator {
	private static final Logger logger = LoggerFactory.getLogger(AbstractSpELParser.class);

	private List<ExpressionConverter> converters;

	public void setExpressionConverters(List<ExpressionConverter> expressionConverters) {
		this.converters = expressionConverters;
	}

	protected static final Pattern pattern = Pattern.compile("\\{(.*?)}");

	protected final TemplateExpressionEvaluator expressionEvaluator = new TemplateExpressionEvaluator();

	protected abstract String getVariableKey();
	@Override
	public boolean canPreParse(String expression) {
		for (ExpressionConverter converter : converters) {
			if (converter.support(expression)) {
				return converter.preParse(expression);
			}
		}
		return false;
	}

	@Override
	public String convertedToSPEL(String expression) {
		for (ExpressionConverter converter : converters) {
			if (converter.support(expression)) {
				return converter.convert(expression);
			}
		}
		return expression;
	}

	@Override
	public String doParser(String spel, AnnotatedElementKey annotatedElementKey, EvaluationContext evaluationContext) {
		logger.debug("SPEL : {}", spel);
		String value = expressionEvaluator.parseExpression(spel, annotatedElementKey, evaluationContext);
		return value == null ? "" : value;
	}


	@Override
	public EvaluationContext createEvaluationContext(Method method, Object[] args, Class<?> targetClass, Object result, String errorMsg, BeanFactory beanFactory, Map<String, Object> constants) {
		return expressionEvaluator.createEvaluationContext(getVariableKey(),method, args, targetClass, result, errorMsg, beanFactory, constants);
	}
}
