package com.evo.middleware.audit.starter;

import com.evo.middleware.audit.context.LogRecordRuntimeContextSource;
import com.evo.middleware.audit.extend.LogRecordCreator;
import com.evo.middleware.audit.extend.OperatorGetter;
import com.evo.middleware.audit.extend.impl.DefaultLogRecordServiceImpl;
import com.evo.middleware.audit.extend.impl.DefaultOperatorGetServiceImpl;
import com.evo.middleware.audit.parser.template.LogRecordTemplateCollector;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@Configuration
public class LogRecordAutoConfiguration{

	// 业务上的
	@Bean
	@ConditionalOnMissingBean
	@Role(BeanDefinition.ROLE_APPLICATION)
	public OperatorGetter operatorGetService() {
		return new DefaultOperatorGetServiceImpl();
	}

	@Bean
	@ConditionalOnMissingBean
	@Role(BeanDefinition.ROLE_APPLICATION)
	public LogRecordCreator recordService() {
		return new DefaultLogRecordServiceImpl();
	}

	// 日志框架内的
	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public LogRecordRuntimeContextSource logRecordRuntimeContextSource() {
		return new LogRecordRuntimeContextSource();
	}


	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public LogRecordTemplateCollector logRecordTemplateCollector() {
		return new LogRecordTemplateCollector();
	}
}
