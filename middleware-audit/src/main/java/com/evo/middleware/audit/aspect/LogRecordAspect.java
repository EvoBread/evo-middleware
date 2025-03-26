package com.evo.middleware.audit.aspect;

import com.evo.middleware.audit.context.LogRecordContext;
import com.evo.middleware.audit.context.LogRecordRuntimeContext;
import com.evo.middleware.audit.domain.MethodExecuteResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * 基于AspectJ实现日志处理切面
 */
@Aspect
public class LogRecordAspect extends BaseLogRecordAspect{

	@Pointcut("@annotation(com.evo.middleware.audit.annotation.AuditRecord)")
	public void pointcut() {
	}

	@Around(value = "pointcut()")
	public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
		long currTime = System.currentTimeMillis();
		Object execute = execute(joinPoint);
		logger.debug("method {} execution time is {}", joinPoint.getSignature().getName(), System.currentTimeMillis() - currTime);
		return execute;
	}

	private Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
		Class<?> targetClass = getTargetClass(joinPoint.getTarget());
		Method method = getMethod(joinPoint);
		Object[] args = joinPoint.getArgs();
		Object ret = null;
		try {
			//0. 获取上下文
			Collection<LogRecordRuntimeContext> contexts = getContexts(targetClass, method);

			// 1. 在方法执行前执行
			Map<String, String> expressionValues = before(contexts, targetClass, method, args);

			// 2. 执行方法并获取结果
			MethodExecuteResult result = new MethodExecuteResult(true, null, "");
			try {
				ret = joinPoint.proceed();
			} catch (Throwable e) {
				result = new MethodExecuteResult(false, e, e.getMessage());
			}

			// 3. 在切点执行后执行
			after(contexts, targetClass, method, args, ret, result, expressionValues);
		} finally {
			new LogRecordContext().clear();
		}
		return ret;
	}

	private Method getMethod(ProceedingJoinPoint point) {
		Class<?> target = getTargetClass(point.getTarget());
		//拦截的方法名称
		String methodName = point.getSignature().getName();
		//拦截的方法参数
		Object[] args = point.getArgs();
		//拦截的放参数类型
		Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();
		Method m = null;
		try {
			//通过反射获得拦截的method
			m = target.getMethod(methodName, parameterTypes);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return m;
	}
}
