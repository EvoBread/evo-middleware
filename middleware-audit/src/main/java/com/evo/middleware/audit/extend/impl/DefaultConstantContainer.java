package com.evo.middleware.audit.extend.impl;

import com.evo.middleware.audit.extend.ConstantContainer;

import java.util.HashMap;
import java.util.Map;

public class DefaultConstantContainer implements ConstantContainer {
	@Override
	public Map<String, Object> constants() {
		Map<String, Object> constants = new HashMap<>();
		constants.put("author", "bread");
		return constants;
	}
}
