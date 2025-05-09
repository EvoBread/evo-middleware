package com.evo.middleware.db.router.dynamic;

import com.evo.middleware.db.router.DBContextHolder;
import com.evo.middleware.db.router.annotation.DBRouterStrategy;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Intercepts({@Signature(
    type = StatementHandler.class,
    method = "prepare",
    args = {Connection.class, Integer.class}
)})
public class DynamicMybatisPlugin implements Interceptor {
    private final Pattern pattern = Pattern.compile("(from|into|update)[\\s]{1,}(\\w{1,})", 2);

    public DynamicMybatisPlugin() {
    }

    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler)invocation.getTarget();
        MetaObject metaObject = MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
        MappedStatement mappedStatement = (MappedStatement)metaObject.getValue("delegate.mappedStatement");

        String id = mappedStatement.getId();
        String className = id.substring(0, id.lastIndexOf("."));
        Class<?> clazz = Class.forName(className);

        DBRouterStrategy dbRouterStrategy = clazz.getAnnotation(DBRouterStrategy.class);
        if (null != dbRouterStrategy && dbRouterStrategy.splitTable()) {
            BoundSql boundSql = statementHandler.getBoundSql();
            String sql = boundSql.getSql();
            Matcher matcher = this.pattern.matcher(sql);
            String tableName = null;
            if (matcher.find()) {
                tableName = matcher.group().trim();
            }

            assert null != tableName;

            String replaceSql = matcher.replaceAll(tableName + "_" + DBContextHolder.getTBKey());
            Field field = boundSql.getClass().getDeclaredField("sql");
            field.setAccessible(true);
            field.set(boundSql, replaceSql);
            field.setAccessible(false);
            return invocation.proceed();
        } else {
            return invocation.proceed();
        }
    }
}