package com.evo.middleware.audit.context;

import com.evo.middleware.audit.context.variable.VariableContext;

/**
 * 日志上下文
 */
public class LogRecordContext extends VariableContext {
	public static final String KEY = "AUDIT";

	@Override
	protected String getKey() {
		return KEY;
	}
}
