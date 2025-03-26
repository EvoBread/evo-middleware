package com.evo.middleware.audit.aspect;

import com.evo.middleware.audit.context.LogRecordRuntimeContextSource;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Method;

public class LogRecordPointcut extends StaticMethodMatcherPointcut implements Serializable{
	private LogRecordRuntimeContextSource recordRuntimeContextSource;

	void setRuntimeContextParser(LogRecordRuntimeContextSource source){
		this.recordRuntimeContextSource = source;
	}

	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		return !CollectionUtils.isEmpty(recordRuntimeContextSource.compute(method,targetClass));
	}

}
