package com.evo.middleware.tx.annotation;

import com.evo.middleware.tx.config.TxConfigurationSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(TxConfigurationSelector.class)
public @interface EnableTxCoordinator {
}
