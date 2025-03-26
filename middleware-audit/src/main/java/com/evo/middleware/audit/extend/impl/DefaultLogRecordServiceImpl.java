package com.evo.middleware.audit.extend.impl;

import com.evo.middleware.audit.domain.AuditBean;
import com.evo.middleware.audit.extend.LogRecordCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 默认的日志记录器
 * <p>直接把日志打印</p>
 */
public class DefaultLogRecordServiceImpl implements LogRecordCreator {
	private static final Logger logger = LoggerFactory.getLogger(DefaultLogRecordServiceImpl.class);
	private static final String STANDARD_PATTERN = "yyyy-MM-dd HH:mm:ss";

	@Override
	public void record(AuditBean logRecord, Map<String, Object> variables) {
		logger.info("【{}】{}", new SimpleDateFormat(STANDARD_PATTERN).format(new Date()), logRecord.toString());
	}
}
