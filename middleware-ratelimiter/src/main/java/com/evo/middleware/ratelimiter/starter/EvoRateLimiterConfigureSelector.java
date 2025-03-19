package com.evo.middleware.ratelimiter.starter;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class EvoRateLimiterConfigureSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{RateLimiterAutoConfiguration.class.getName()};
    }
}
