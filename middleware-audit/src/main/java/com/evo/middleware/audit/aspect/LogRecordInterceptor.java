package com.evo.middleware.audit.aspect;

import com.evo.middleware.audit.context.LogRecordContext;
import com.evo.middleware.audit.context.LogRecordRuntimeContext;
import com.evo.middleware.audit.domain.MethodExecuteResult;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * 日志拦截器
 */
public class LogRecordInterceptor extends BaseLogRecordAspect implements MethodInterceptor, Serializable{
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		long currTime = System.currentTimeMillis();
		Method method = invocation.getMethod();
		Object execute = execute(invocation, invocation.getThis(), method, invocation.getArguments());
		logger.debug("method {} execution time is {}", method.getName(), System.currentTimeMillis() - currTime);
		return execute;
	}

	private Object execute(MethodInvocation invoker, Object target, Method method, Object[] args) throws Throwable {
		Class<?> targetClass = getTargetClass(target);
		Object ret = null;
		MethodExecuteResult result = new MethodExecuteResult(true, null, "");
		try {
			// 0. 获取上下文
			Collection<LogRecordRuntimeContext> contexts = getContexts(targetClass, method);

			// 1. 在方法执行前执行
			Map<String, String> expressionValues = before(contexts, targetClass, method, args);

			// 2. 执行方法并获取结果
			try {
				ret = invoker.proceed();
			} catch (Exception e) {
				result = new MethodExecuteResult(false, e, e.getMessage());
			}
			
			// 3. 在切点执行后执行
			after(contexts, targetClass, method, args, ret, result, expressionValues);
		} finally {
			new LogRecordContext().clear();
		}
		return ret;
	}
}
