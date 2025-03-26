package com.evo.middleware.audit.starter;

import com.evo.middleware.audit.extend.ConstantContainer;
import com.evo.middleware.audit.extend.CustomParseFunction;
import com.evo.middleware.audit.parser.el.function.DefaultFunctionServiceImpl;
import com.evo.middleware.audit.parser.el.function.FunctionService;
import com.evo.middleware.audit.parser.el.function.ParseFunctionFactory;
import com.evo.middleware.audit.extend.impl.DefaultParseFunction;
import com.evo.middleware.audit.extend.impl.DefaultConstantContainer;
import com.evo.middleware.audit.parser.el.constant.ConstantExpressionConverter;
import com.evo.middleware.audit.parser.el.function.FunctionExpressionConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Role;

import java.util.List;

/**
 * SpEL解析配置
 */
@Configuration
public class SpELParserConfiguration {
	@Bean
	@ConditionalOnMissingBean
	@Role(BeanDefinition.ROLE_APPLICATION)
	public CustomParseFunction parseFunction() {
		return new DefaultParseFunction();
	}

	@Bean
	@ConditionalOnMissingBean
	@Role(BeanDefinition.ROLE_APPLICATION)
	public ConstantContainer constantContainer() {
		return new DefaultConstantContainer();
	}

	@Lazy
	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public ParseFunctionFactory parseFunctionFactory(@Autowired List<CustomParseFunction> parseFunctions) {
		return new ParseFunctionFactory(parseFunctions);
	}

	@Bean
	@ConditionalOnMissingBean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public FunctionService functionService(ParseFunctionFactory parseFunctionFactory) {
		return new DefaultFunctionServiceImpl(parseFunctionFactory);
	}

	@Lazy
	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public FunctionExpressionConverter functionExpressionConverter(@Lazy FunctionService functionService) {
		FunctionExpressionConverter functionExpressionConverter = new FunctionExpressionConverter();
		functionExpressionConverter.setFunctionService(functionService);
		return functionExpressionConverter;
	}

	@Lazy
	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public ConstantExpressionConverter constantExpressionConverter() {
		return new ConstantExpressionConverter();
	}

}
