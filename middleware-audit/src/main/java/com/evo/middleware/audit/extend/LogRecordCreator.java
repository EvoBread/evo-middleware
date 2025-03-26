package com.evo.middleware.audit.extend;

import com.evo.middleware.audit.domain.AuditBean;

import java.util.Map;

/**
 * 日志业务接口
 * <p>目前只定义了保存日志的方法</p>
 * <p>注意在实现该方法的时候要新开事务!!!</p>
 */
public interface LogRecordCreator{
	/**
	 * 保存log
	 * @param record 	日志实体
	 * @param variables	业务变量
	 */
	void record(AuditBean record, Map<String, Object> variables);
}
