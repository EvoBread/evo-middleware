package com.evo.middleware.audit.starter;

import com.evo.middleware.audit.annotation.EnableAuditRecord;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

public class LogRecordConfigureSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        AnnotationAttributes attributes =
                AnnotationAttributes.fromMap(
                        importingClassMetadata.getAnnotationAttributes(EnableAuditRecord.class.getName(), false)
                );
        return attributes.getBoolean("aspectJ") ?
                new String[]{LogRecordAutoConfiguration.class.getName(),
                        SpELParserConfiguration.class.getName(),
                        LogRecordAspectAutoConfiguration.class.getName()
                } :
                new String[]{LogRecordAutoConfiguration.class.getName(),
                        SpELParserConfiguration.class.getName(),
                        LogRecordInterceptorAutoConfiguration.class.getName()
                };
    }
}
