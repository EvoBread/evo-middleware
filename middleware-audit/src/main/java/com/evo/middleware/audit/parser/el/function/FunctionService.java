package com.evo.middleware.audit.parser.el.function;

import java.lang.reflect.Method;

public interface FunctionService{

	/**
	 * 函数是否在业务执行前解析
	 * @param functionName 函数名
	 */
	boolean beforeFunction(String functionName);

	/**
	 * 获取自定义函数方法
	 * @param functionName 函数名
	 */
	Method getMethod(String functionName);
}
