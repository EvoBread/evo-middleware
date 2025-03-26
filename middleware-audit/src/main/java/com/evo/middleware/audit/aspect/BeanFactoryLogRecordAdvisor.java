package com.evo.middleware.audit.aspect;

import com.evo.middleware.audit.context.LogRecordRuntimeContextSource;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

/**
 * 动态切入点
 */
public class BeanFactoryLogRecordAdvisor extends AbstractBeanFactoryPointcutAdvisor{
	private final LogRecordPointcut pointcut = new LogRecordPointcut();

	public void setClassFilter(ClassFilter classFilter) {
		this.pointcut.setClassFilter(classFilter);
	}

	@Override
	public Pointcut getPointcut() {
		return pointcut;
	}

	public void setLogRecordRuntimeContextParser(LogRecordRuntimeContextSource source) {
		pointcut.setRuntimeContextParser(source);
	}
}
