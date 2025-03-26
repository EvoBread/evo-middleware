package com.evo.middleware.audit.parser.el.constant;

import com.evo.middleware.audit.parser.el.ExpressionConverter;

/**
 * 常量表达式转化器
 */
public class ConstantExpressionConverter implements ExpressionConverter {
	@Override
	public String typeFlag() {
		return "$";
	}

	@Override
	public boolean support(String expression) {
		return expression.startsWith(typeFlag());
	}

	@Override
	public String convert(String expression) {
		if (expression.contains(typeFlag())) {
			expression = expression.replaceAll("\\$", "#\\$");
		}
		if (expression.contains(".")) {
			expression = expression.replaceAll("\\.", "_");
		}
		return expression;
	}

	@Override
	public boolean preParse(String expression) {
		return true;
	}
}
