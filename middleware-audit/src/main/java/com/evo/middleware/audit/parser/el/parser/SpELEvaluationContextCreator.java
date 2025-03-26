package com.evo.middleware.audit.parser.el.parser;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.expression.EvaluationContext;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * SpEL 计算上下文创建器
 */
public interface SpELEvaluationContextCreator{

	/**
	 * 创建计算上下文
	 */
	EvaluationContext createEvaluationContext(
			Method method,
			Object[] args,
			Class<?> targetClass,
			Object result,
			String errorMsg,
			BeanFactory beanFactory,
			Map<String,Object> constants
	);
}
