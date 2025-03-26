package com.evo.middleware.audit.parser.el.function;

import com.evo.middleware.audit.domain.ParseFunction;

import java.lang.reflect.Method;

public class DefaultFunctionServiceImpl implements FunctionService{
	private final ParseFunctionFactory parseFunctionFactory;

	public DefaultFunctionServiceImpl(ParseFunctionFactory parseFunctionFactory) {
		this.parseFunctionFactory = parseFunctionFactory;
	}

	@Override
	public boolean beforeFunction(String functionName) {
		return parseFunctionFactory.isBeforeFunction(functionName);
	}

	@Override
	public Method getMethod(String functionName) {
		ParseFunction function = parseFunctionFactory.getFunction(functionName);
		if (function == null) {
			throw new IllegalArgumentException("no custom function name is " + functionName);
		}
		return function.getMethod();
	}

}
