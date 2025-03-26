package com.evo.middleware.audit.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 审计记录注解
 * <p>1. 支持动态解析模板，success、fail、bizNo、detail、type、operation、condition、operator 均为可解析模板。 </p>
 * <p>2. 目前支持三种可解析模板，变量参数（用#来标识），常量参数（用$来标识），函数参数（用@来标识）</p>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AuditRecord {
    String success();                    // 成功模板

    String fail() default "";            // 失败模板

    String operator() default "";        // 操作人模板

    String prefix() default "";            // 前缀

    String bizNo() default "";            // 业务编号

    String category() default "";        // 业务分类

    String detail() default "";            // 详情

    String type() default "";            // 审计类型

    String operation() default "";        // 操作类型

    /**
     * 解析条件
     * SPEL 表达式
     * <P>表达式执行结果为 true 则在进入方法前解析 审计日志</P>
     * <P>表达式执行结果为 false 则在方法执行完成后解析 审计日志</P>
     * <p>默认为false</p>
     */
    String condition() default "false";        // 条件
}
