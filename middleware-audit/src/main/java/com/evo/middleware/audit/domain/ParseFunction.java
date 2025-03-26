package com.evo.middleware.audit.domain;

import java.lang.reflect.Method;

/**
 * 解析函数
 */
public class ParseFunction{
	private boolean executeBefore;
	private String functionName;
	private Method method;

	public boolean executeBefore() {
		return executeBefore;
	}

	public void setExecuteBefore(boolean executeBefore) {
		this.executeBefore = executeBefore;
	}

	public String functionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}
}
