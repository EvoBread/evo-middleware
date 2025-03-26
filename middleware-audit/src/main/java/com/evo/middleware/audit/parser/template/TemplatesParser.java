package com.evo.middleware.audit.parser.template;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * 模板解析器
 */
public interface TemplatesParser{

	/**
	 * 执行条件解析
	 *
	 * @param templates   模板集合
	 * @param targetClass 当前对象
	 * @param method      方法
	 * @param args        参数
	 * @return 解析值
	 */
	Map<String, String> processCondition(Collection<String> templates,
										 Class<?> targetClass,
										 Method method,
										 Object[] args);

	/**
	 * 执行解析模板
	 *
	 * @param templates    模板集合
	 * @param ret          返回值
	 * @param targetClass  当前对象
	 * @param method       方法
	 * @param args         参数
	 * @param errorMsg     错误信息
	 * @param lastParseMap 上次的解析的结果
	 * @return 解析值
	 */
	Map<String, String> process(Collection<String> templates,
								Object ret,
								Class<?> targetClass,
								Method method,
								Object[] args,
								String errorMsg,
								Map<String, String> lastParseMap);


	/**
	 * 在切点方法执行前执行解析模板
	 *
	 * @param templates   模板集合
	 * @param targetClass 当前对象
	 * @param method      方法
	 * @param args        参数
	 * @return 解析值
	 */
	Map<String, String> processBeforeExecute(Collection<String> templates,
											 Class<?> targetClass,
											 Method method,
											 Object[] args);

}
