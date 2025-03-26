package com.evo.middleware.audit.parser.el;

/**
 * 表达式转化器
 */
public interface ExpressionConverter{
	/**
	 * 表达式类型标识
	 */
	String typeFlag();

	/**
	 * 是否支持
	 */
	boolean support(String expression);

	boolean preParse(String expression);

	/**
	 * 转换
	 *
	 * @param expression 表达式
	 * @return SPEL表达式
	 */
	String convert(String expression);
}
