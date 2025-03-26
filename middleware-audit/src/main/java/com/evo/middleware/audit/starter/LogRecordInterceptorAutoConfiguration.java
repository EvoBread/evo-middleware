package com.evo.middleware.audit.starter;

import com.evo.middleware.audit.annotation.EnableAuditRecord;
import com.evo.middleware.audit.aspect.BeanFactoryLogRecordAdvisor;
import com.evo.middleware.audit.aspect.LogRecordAspect;
import com.evo.middleware.audit.aspect.LogRecordInterceptor;
import com.evo.middleware.audit.context.LogRecordRuntimeContextSource;
import com.evo.middleware.audit.extend.ConstantContainer;
import com.evo.middleware.audit.extend.LogRecordCreator;
import com.evo.middleware.audit.extend.OperatorGetter;
import com.evo.middleware.audit.parser.annotation.LogRecordParser;
import com.evo.middleware.audit.parser.el.ExpressionConverter;
import com.evo.middleware.audit.parser.template.LogRecordTemplateCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;

@Configuration
public class LogRecordInterceptorAutoConfiguration implements ImportAware{
	private static final Logger logger = LoggerFactory.getLogger(LogRecordInterceptorAutoConfiguration.class);
	public AnnotationAttributes enableLogRecord;

	@Bean
	@Primary
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public LogRecordInterceptor logRecordInterceptor(
			@Lazy List<ExpressionConverter> expressionConverters,
			@Lazy OperatorGetter operatorGetter,
			@Lazy LogRecordCreator logRecordCreator,
			@Lazy ConstantContainer constantContainer,
			@Lazy LogRecordParser logRecordParser,
			@Lazy LogRecordTemplateCollector templateCollector,
			@Lazy LogRecordRuntimeContextSource logRecordRuntimeContextSource
	) {
		String serviceId = "DEFAULT";
		if (enableLogRecord != null) {
			serviceId = enableLogRecord.getString("serviceId");
		}
		LogRecordInterceptor interceptor = new LogRecordInterceptor();
		interceptor.setRuntimeContextParser(logRecordRuntimeContextSource);
		interceptor.setLogRecordTemplateCollector(templateCollector);
		interceptor.setLogRecordCreator(logRecordCreator);
		interceptor.setLogRecordParser(logRecordParser);
		interceptor.setServiceId(serviceId);
		interceptor.setExpressionConverters(expressionConverters);
		interceptor.setOperatorGetService(operatorGetter);
		interceptor.setConstants(constantContainer.constants());
		return interceptor;
	}

	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public BeanFactoryLogRecordAdvisor logRecordAdvisor(LogRecordInterceptor logRecordInterceptor, LogRecordRuntimeContextSource logRecordRuntimeContextSource) {
		logger.info("{} server is use {}", logRecordInterceptor.getServiceId(), "Spring Aop");
		BeanFactoryLogRecordAdvisor advisor = new BeanFactoryLogRecordAdvisor();
		advisor.setLogRecordRuntimeContextParser(logRecordRuntimeContextSource);
		advisor.setAdvice(logRecordInterceptor);
		advisor.setOrder(20);
		return advisor;
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
