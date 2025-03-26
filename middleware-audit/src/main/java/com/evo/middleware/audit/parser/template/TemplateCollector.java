package com.evo.middleware.audit.parser.template;

import com.evo.middleware.audit.context.variable.ExpressionContext;

import java.util.Collection;
import java.util.List;

/**
 * 模板收集器
 */
public interface TemplateCollector<T extends ExpressionContext>{
	/**
	 * 获取解析模板
	 */
	List<String> getParseTemplates(T context, String action);

	/**
	 * 获取条件模板
	 */
	List<String> getConditionTemplates(T context);

	/**
	 * 获取前置执行的SPEL模板
	 */
	List<String> getBeforeExecuteTemplate(Collection<T> contexts);
}
