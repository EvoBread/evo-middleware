package com.evo.middleware.audit.annotation;

import com.evo.middleware.audit.starter.LogRecordConfigureSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用审计
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LogRecordConfigureSelector.class)
public @interface EnableAuditRecord {

	/**
	 * 服务ID
	 */
	String serviceId() default "";

	boolean aspectJ() default true;
}
