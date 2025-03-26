package com.evo.middleware.audit.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义函数注解
 *
 * <p>注解所标记的函数为自定义函数</p>
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CustomFunction{
	/**
	 * 是否在业务执行前执行自定义函数
	 */
	boolean executeBefore() default false;

	/**
	 * 自定义函数名称（默认为方法名称）
	 * <p>自定义函数的名称在系统中具有唯一性，不支持方法重载</p>
	 */
	String name() default "";
}
