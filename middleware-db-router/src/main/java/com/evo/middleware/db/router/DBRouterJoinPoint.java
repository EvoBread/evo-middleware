package com.evo.middleware.db.router;

import com.evo.middleware.db.router.annotation.DBRouter;
import com.evo.middleware.db.router.config.DBRouterConfig;
import com.evo.middleware.db.router.strategy.IDBRouterStrategy;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Aspect
public class DBRouterJoinPoint {
    private final Logger logger = LoggerFactory.getLogger(DBRouterJoinPoint.class);
    private final DBRouterConfig dbRouterConfig;
    private final IDBRouterStrategy dbRouterStrategy;

    public DBRouterJoinPoint(DBRouterConfig dbRouterConfig, IDBRouterStrategy dbRouterStrategy) {
        this.dbRouterConfig = dbRouterConfig;
        this.dbRouterStrategy = dbRouterStrategy;
    }

    @Pointcut("@annotation(com.evo.middleware.db.router.annotation.DBRouter)")
    public void aopPoint() {
    }

    @Around("aopPoint() && @annotation(dbRouter)")
    public Object doRouter(ProceedingJoinPoint jp, DBRouter dbRouter) throws Throwable {
        String dbKey = dbRouter.key();
        if (StringUtils.isBlank(dbKey) && StringUtils.isBlank(this.dbRouterConfig.getRouterKey())) {
            throw new RuntimeException("annotation DBRouter key is null！");
        } else {
            dbKey = StringUtils.isNotBlank(dbKey) ? dbKey : this.dbRouterConfig.getRouterKey();
            String dbKeyAttr = this.getAttrValue(dbKey, jp.getArgs());
            this.dbRouterStrategy.doRouter(dbKeyAttr);

            Object res;
            try {
                res = jp.proceed();
            } finally {
                this.dbRouterStrategy.clear();
            }
            return res;
        }
    }

    private Method getMethod(JoinPoint jp) throws NoSuchMethodException {
        Signature sig = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) sig;
        return jp.getTarget().getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
    }

    public String getAttrValue(String attr, Object[] args) {
        if (1 == args.length) {
            Object arg = args[0];
            if (arg instanceof String) {
                return arg.toString();
            }
        }

        String filedValue = null;

        for (Object arg : args) {
            try {
                if (StringUtils.isNotBlank(filedValue)) {
                    break;
                }
                filedValue = String.valueOf(this.getValueByName(arg, attr));
            } catch (Exception e) {
                this.logger.error("获取路由属性值失败 attr：{}", attr, e);
            }
        }

        return filedValue;
    }

    private Object getValueByName(Object item, String name) {
        try {
            Field field = this.getFieldByName(item, name);
            if (field == null) {
                return null;
            } else {
                field.setAccessible(true);
                Object o = field.get(item);
                field.setAccessible(false);
                return o;
            }
        } catch (IllegalAccessException var5) {
            return null;
        }
    }

    private Field getFieldByName(Object item, String name) {
        try {
            Field field;
            try {
                field = item.getClass().getDeclaredField(name);
            } catch (NoSuchFieldException var5) {
                field = item.getClass().getSuperclass().getDeclaredField(name);
            }

            return field;
        } catch (NoSuchFieldException var6) {
            return null;
        }
    }
}

