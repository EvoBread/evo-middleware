package com.evo.middleware.audit.context;

import com.evo.middleware.audit.context.variable.ExpressionContext;

/**
 * 日志运行时上下文
 */
public class LogRecordRuntimeContext implements ExpressionContext {
	private String successTemplate;    // 成功模板
	private String failureTemplate;    // 失败模板
	private String operator;           // 操作人
	private String detail;             // 详情
	private String bizKey;
	private String bizNo;
	private String category;
	private String condition;
	private String type;
	private String operation;


	public void setSuccess(String template) {
		this.successTemplate = template;
	}

	public String getSuccess() {
		return this.successTemplate;
	}

	public void setFailure(String template) {
		this.failureTemplate = template;
	}

	public String getFailure() {
		return this.failureTemplate;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getBizKey() {
		return bizKey;
	}

	public void setBizKey(String bizKey) {
		this.bizKey = bizKey;
	}

	public String getBizNo() {
		return bizNo;
	}

	public void setBizNo(String bizNo) {
		this.bizNo = bizNo;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public LogRecordRuntimeContext success(String template) {
		this.successTemplate = template;
		return this;
	}

	public LogRecordRuntimeContext failure(String template) {
		this.failureTemplate = template;
		return this;
	}

	public LogRecordRuntimeContext operator(String operator) {
		this.operator = operator;
		return this;
	}

	public LogRecordRuntimeContext detail(String detail) {
		this.detail = detail;
		return this;
	}

	public LogRecordRuntimeContext bizKey(String bizKey) {
		this.bizKey = bizKey;
		return this;
	}

	public LogRecordRuntimeContext bizNo(String bizNo) {
		this.bizNo = bizNo;
		return this;
	}

	public LogRecordRuntimeContext category(String category) {
		this.category = category;
		return this;
	}

	public LogRecordRuntimeContext condition(String condition) {
		this.condition = condition;
		return this;
	}

	public LogRecordRuntimeContext type(String type) {
		this.type = type;
		return this;
	}

	public LogRecordRuntimeContext operation(String operation) {
		this.operation = operation;
		return this;
	}

	public String getAction(boolean success) {
		String action = "";
		if (success) {
			action = this.getSuccess();
		} else {
			action = this.getFailure();
		}
		return action;
	}
}
