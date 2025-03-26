package com.evo.middleware.audit.extend;

public interface OperatorGetter{
	/**
	 * 获取当前操作用户
	 *
	 * @return 用户Id
	 */
	String getOperatorId();

}
