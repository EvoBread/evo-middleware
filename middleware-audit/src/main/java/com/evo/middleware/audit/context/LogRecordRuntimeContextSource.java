package com.evo.middleware.audit.context;

import com.evo.middleware.audit.annotation.AuditRecord;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 日志运行时上下文解析器
 */
public class LogRecordRuntimeContextSource {
	public Collection<LogRecordRuntimeContext> compute(Method method, Class<?> targetClass) {
		if (!Modifier.isPublic(method.getModifiers())) {
			return null;
		}
		Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
		specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
		return parseLogRecordAnnotations(specificMethod);
	}

	private Collection<LogRecordRuntimeContext> parseLogRecordAnnotations(AnnotatedElement ae) {
		Collection<AuditRecord> annotations = AnnotatedElementUtils.getAllMergedAnnotations(ae, AuditRecord.class);
		Collection<LogRecordRuntimeContext> ret = null;
		if (!annotations.isEmpty()) {
			ret = new ArrayList<>(1);
			for (AuditRecord annotation : annotations) {
				ret.add(parseAnnotation(ae, annotation));
			}
		}
		if (!CollectionUtils.isEmpty(ret)) {
			ret = ret.stream().filter(context ->
					StringUtils.hasText(context.getSuccess()) || StringUtils.hasText(context.getFailure())
			).collect(Collectors.toList());
			if (ret.isEmpty()) {
				throw new IllegalStateException("Invalid logRecord annotation configuration on '" +
						ae.toString() + "'. 'one of successTemplate and failLogTemplate' attribute must be set.");
			}
		}

		return ret;
	}

	private LogRecordRuntimeContext parseAnnotation(AnnotatedElement ae, AuditRecord annotation) {
		LogRecordRuntimeContext context = new LogRecordRuntimeContext()
				.success(annotation.success())
				.failure(annotation.fail())
				.category(!StringUtils.hasText(annotation.category()) ? annotation.prefix() : annotation.category())
				.operator(annotation.operator())
				.detail(annotation.detail())
				.bizNo(annotation.bizNo())
				.operation(annotation.operation())
				.type(annotation.type())
				.condition(annotation.condition());
		String bizNo;
		if (StringUtils.hasText(annotation.prefix())) {
			bizNo = annotation.prefix().concat("_") + annotation.bizNo();
		} else {
			bizNo = annotation.bizNo();
		}
		context.setBizKey(bizNo);
		return context;
	}
}
