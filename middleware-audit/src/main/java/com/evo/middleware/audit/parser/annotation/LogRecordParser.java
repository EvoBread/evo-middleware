package com.evo.middleware.audit.parser.annotation;

import com.evo.middleware.audit.context.LogRecordContext;
import com.evo.middleware.audit.context.LogRecordRuntimeContext;
import com.evo.middleware.audit.domain.AuditBean;
import com.evo.middleware.audit.extend.OperatorGetter;
import com.evo.middleware.audit.parser.el.parser.DefaultTemplateValueParser;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 日志解析器
 */
public class LogRecordParser extends DefaultTemplateValueParser{
	private String serviceId;
	private OperatorGetter operatorGetter;

	public void setOperatorGetService(OperatorGetter operatorGetter) {
		this.operatorGetter = operatorGetter;
	}

	public AuditBean convert2Log(Map<String, String> expressionValues, String action, LogRecordRuntimeContext context) {
		String operatorId = getOperatorIdFromService(context);

		AuditBean logBean = new AuditBean();
		logBean.setService(serviceId);
		logBean.setAction(expressionValues.get(action));
		logBean.setBizKey(expressionValues.get(context.getBizKey()));
		logBean.setBizNo(expressionValues.get(context.getBizNo()));
		logBean.setDetail(expressionValues.get(context.getDetail()));
		logBean.setOperator(getRealOperatorId(context, operatorId, expressionValues));
		logBean.setCategory(context.getCategory());
		logBean.setOperation(expressionValues.get(context.getOperation()));
		logBean.setType(expressionValues.get(context.getType()));

		//如果 action 为空，不记录日志
		if (!StringUtils.hasLength(logBean.getAction())) {
			return null;
		}
		return logBean;
	}

	private String getOperatorIdFromService(LogRecordRuntimeContext context) {
		String operator = "";
		if (!StringUtils.hasLength(context.getOperator())) {
			operator = operatorGetter.getOperatorId();
			if (!StringUtils.hasLength(operator)) {
				throw new IllegalArgumentException("[LogRecord] operator is null");
			}
		}
		return operator;
	}

	private String getRealOperatorId(LogRecordRuntimeContext context, String operatorIdFromService, Map<String, String> expressionValues) {
		return StringUtils.hasLength(operatorIdFromService) ? operatorIdFromService : expressionValues.get(context.getOperator());
	}

	@Override
	protected String getVariableKey() {
		return LogRecordContext.KEY;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public Object getServiceId() {
		return serviceId;
	}
}
