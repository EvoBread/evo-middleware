package com.evo.middleware.ratelimiter.annotation;

import com.evo.middleware.ratelimiter.starter.EvoRateLimiterConfigureSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EvoRateLimiterConfigureSelector.class)
public @interface EnableEvoRateLimiter {
}
