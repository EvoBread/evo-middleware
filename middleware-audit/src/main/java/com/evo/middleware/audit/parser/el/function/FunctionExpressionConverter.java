package com.evo.middleware.audit.parser.el.function;

import com.evo.middleware.audit.parser.el.ExpressionConverter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import java.lang.reflect.Method;

/**
 * 函数表达式转换
 */
public class FunctionExpressionConverter implements ExpressionConverter, BeanFactoryAware{
	private BeanFactory beanFactory;
	private FunctionService functionService;

	@Override
	public String typeFlag() {
		return "@";
	}

	@Override
	public boolean support(String expression) {
		return expression.startsWith(typeFlag());
	}


	@Override
	public String convert(String expression) {
		String funcName = getFunctionName(expression);
		Method paresMethod = functionService.getMethod(funcName);
		return typeFlag() + getBeanName(paresMethod) + "." + expression.substring(1);
	}

	@Override
	public boolean preParse(String expression) {
		if (expression.contains(typeFlag())) {
			String functionName = getFunctionName(expression);
			return functionService.beforeFunction(functionName);
		}
		return false;
	}

	/**
	 * 获取执行函数名
	 *
	 * @param expression 表达式
	 * @return
	 */
	private String getFunctionName(String expression) {
		return expression.substring(expression.indexOf(typeFlag()) + 1, expression.indexOf("("));
	}

	/**
	 * 获取BeanName
	 *
	 * @param method 方式
	 */
	private String getBeanName(Method method) {
		Class<?> clazz = method.getDeclaringClass();
		String[] aliases = beanFactory.getAliases(clazz.getName());
		String name;
		if (aliases.length > 0) {
			name = aliases[0];
		} else {
			name = clazz.getSimpleName();
		}
		name = name.substring(0, 1).toLowerCase() + name.substring(1);
		return name;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	public void setFunctionService(FunctionService functionService) {
		this.functionService = functionService;
	}
}
