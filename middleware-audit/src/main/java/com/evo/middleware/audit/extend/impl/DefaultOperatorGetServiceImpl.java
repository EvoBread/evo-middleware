package com.evo.middleware.audit.extend.impl;

import com.evo.middleware.audit.extend.OperatorGetter;

public class DefaultOperatorGetServiceImpl implements OperatorGetter {
	@Override
	public String getOperatorId() {
		return "systemId";
	}
}
