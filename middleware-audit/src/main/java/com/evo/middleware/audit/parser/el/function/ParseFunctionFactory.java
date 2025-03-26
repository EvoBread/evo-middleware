package com.evo.middleware.audit.parser.el.function;

import com.evo.middleware.audit.domain.ParseFunction;
import com.evo.middleware.audit.annotation.CustomFunction;
import com.evo.middleware.audit.extend.CustomParseFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 自定义函数工厂
 */
public class ParseFunctionFactory{
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private Map<String, ParseFunction> allFunctionMap;

	public ParseFunctionFactory(List<CustomParseFunction> parseFunctions){
		if (CollectionUtils.isEmpty(parseFunctions)) {
			return;
		}
		allFunctionMap = new HashMap<>();
		for (CustomParseFunction parseFunction : parseFunctions) {
			Class<?> clazz = parseFunction.getClass();
			Method[] methods = clazz.getMethods();
			for (Method method : methods){
				Method specificMethod = ClassUtils.getMostSpecificMethod(method, clazz);
				CustomFunction annotation = specificMethod.getAnnotation(CustomFunction.class);
				if(annotation == null){
					continue;
				}
				String functionName = !StringUtils.hasLength(annotation.name())
						? specificMethod.getName() : annotation.name();
				if (allFunctionMap.containsKey(functionName)) {
					throw new IllegalArgumentException(String.format("custom function '%s' already existed, class:'%s'",
							functionName,
							specificMethod.getDeclaringClass().getName()));
				}
				Class<?> returnType = specificMethod.getReturnType();
				if (returnType != String.class && !returnTypeIsVoid(returnType)) {
					throw new IllegalArgumentException(String
							.format("custom function '%s' return type Illegal", functionName));
				}
				if (returnTypeIsVoid(returnType) && !annotation.executeBefore()) {
					throw new IllegalArgumentException(String
							.format("custom function '%s' return type is void but executeBefore is false", functionName));
				}
				ParseFunction function = new ParseFunction();
				function.setFunctionName(functionName);
				function.setExecuteBefore(annotation.executeBefore());
				function.setMethod(specificMethod);
				allFunctionMap.put(function.functionName(), function);
			}
		}
	}

	private boolean returnTypeIsVoid(Class<?> returnType){
		return returnType == Void.class || returnType.getName().equals("void");
	}

	public ParseFunction getFunction(String functionName) {
		return allFunctionMap.get(functionName);
	}

	public boolean isBeforeFunction(String functionName) {
		return allFunctionMap.get(functionName) != null && allFunctionMap.get(functionName).executeBefore();
	}
}
