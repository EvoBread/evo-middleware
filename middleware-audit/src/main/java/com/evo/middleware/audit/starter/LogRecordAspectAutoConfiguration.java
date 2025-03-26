package com.evo.middleware.audit.starter;

import com.evo.middleware.audit.annotation.EnableAuditRecord;
import com.evo.middleware.audit.aspect.LogRecordAspect;
import com.evo.middleware.audit.context.LogRecordRuntimeContextSource;
import com.evo.middleware.audit.extend.ConstantContainer;
import com.evo.middleware.audit.extend.LogRecordCreator;
import com.evo.middleware.audit.extend.OperatorGetter;
import com.evo.middleware.audit.parser.template.LogRecordTemplateCollector;
import com.evo.middleware.audit.parser.el.ExpressionConverter;
import com.evo.middleware.audit.parser.annotation.LogRecordParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;

@Configuration
public class LogRecordAspectAutoConfiguration implements ImportAware{
	private static final Logger logger = LoggerFactory.getLogger(LogRecordAspectAutoConfiguration.class);
	public AnnotationAttributes enableLogRecord;

	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public LogRecordAspect logRecordAspect(
			@Lazy List<ExpressionConverter> expressionConverters,
			@Lazy OperatorGetter operatorGetter,
			@Lazy LogRecordCreator logRecordCreator,
			@Lazy LogRecordParser logRecordParser,
			@Lazy ConstantContainer constantContainer,
			@Lazy LogRecordTemplateCollector templateCollector,
			@Lazy LogRecordRuntimeContextSource logRecordRuntimeContextSource
	) {
		String serviceId = "DEFAULT";
		if (enableLogRecord != null) {
			serviceId = enableLogRecord.getString("serviceId");
		}
		logger.info("{} server is use {}", serviceId, "AspectJ");
		LogRecordAspect aspect = new LogRecordAspect();
		aspect.setRuntimeContextParser(logRecordRuntimeContextSource);
		aspect.setLogRecordTemplateCollector(templateCollector);
		aspect.setLogRecordCreator(logRecordCreator);
		aspect.setLogRecordParser(logRecordParser);
		aspect.setServiceId(serviceId);
		aspect.setExpressionConverters(expressionConverters);
		aspect.setOperatorGetService(operatorGetter);
		aspect.setConstants(constantContainer.constants());
		return aspect;
	}

	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {
		this.enableLogRecord = AnnotationAttributes.fromMap(
				importMetadata.getAnnotationAttributes(EnableAuditRecord.class.getName(), false));
		if (this.enableLogRecord == null) {
			logger.info("@EnableCaching is not present on importing class");
		}
	}
}
