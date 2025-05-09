package com.evo.middleware.audit.parser.template;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模板表达式计算器
 */
public class TemplateExpressionEvaluator extends CachedExpressionEvaluator{

	private final Map<ExpressionKey, Expression> expressionCache = new ConcurrentHashMap<>(64);

	private final Map<AnnotatedElementKey, Method> targetMethodCache = new ConcurrentHashMap<>(64);

	public String parseExpression(String conditionExpression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
		Object value = getExpression(this.expressionCache, methodKey, conditionExpression).getValue(evalContext, Object.class);
		return value == null ? "" : value.toString();
	}


	public EvaluationContext createEvaluationContext(String variableKey,Method method, Object[] args, Class<?> targetClass,
													 Object result, String errorMsg, BeanFactory beanFactory,
													 Map<String,Object> constants) {
		Method targetMethod = getTargetMethod(targetClass, method);
		TemplateEvaluationContext evaluationContext = new TemplateEvaluationContext(variableKey,
				null, targetMethod, args, getParameterNameDiscoverer(), result, errorMsg);
		evaluationContext.setConstants(constants);
		if (beanFactory != null) {
			evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
		}
		return evaluationContext;
	}

	private Method getTargetMethod(Class<?> targetClass, Method method) {
		AnnotatedElementKey methodKey = new AnnotatedElementKey(method, targetClass);
		Method targetMethod = this.targetMethodCache.get(methodKey);
		if (targetMethod == null) {
			targetMethod = AopUtils.getMostSpecificMethod(method, targetClass);
			this.targetMethodCache.put(methodKey, targetMethod);
		}
		return targetMethod;
	}
}
