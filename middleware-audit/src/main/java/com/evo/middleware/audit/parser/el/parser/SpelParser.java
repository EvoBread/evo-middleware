package com.evo.middleware.audit.parser.el.parser;

import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;

/**
 * SPEL解析器
 */
public interface SpelParser{
	/**
	 * 是否可以前置解析
	 */
	boolean canPreParse(String expression);


	/**
	 * 将表达式转为SPEL
	 */
	String convertedToSPEL(String expression);


	/**
	 * 解析spel表达式
	 */
	String doParser(String spel, AnnotatedElementKey annotatedElementKey, EvaluationContext evaluationContext);
}
