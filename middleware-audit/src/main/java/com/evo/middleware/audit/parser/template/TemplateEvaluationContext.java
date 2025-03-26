package com.evo.middleware.audit.parser.template;

import com.evo.middleware.audit.context.variable.VariableContext;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.util.Map;

public class TemplateEvaluationContext extends MethodBasedEvaluationContext {

	public TemplateEvaluationContext(String variableKey,Object rootObject, Method method, Object[] arguments,
									  ParameterNameDiscoverer parameterNameDiscoverer,
									  Object ret, String errorMsg) {
		super(rootObject, method, arguments, parameterNameDiscoverer);
		Map<String, Object> variables = new VariableContext(){
			@Override
			protected String getKey() {
				return variableKey;
			}
		}.getVariables();
		if (variables != null && !variables.isEmpty()) {
			for (Map.Entry<String, Object> entry : variables.entrySet()) {
				setVariable(entry.getKey(), entry.getValue());
			}
		}
		setVariable("_ret", ret);
		setVariable("_errorMsg", errorMsg);
	}

	public void setConstants(Map<String, Object> constants) {
		if (constants != null && !constants.isEmpty()) {
			for (Map.Entry<String, Object> entry : constants.entrySet()) {
				String key = entry.getKey();
				if (key.contains(".")) {
					key = key.replaceAll("\\.", "_");
				}
				setVariable("$" + key, entry.getValue());
			}
		}
	}
}
