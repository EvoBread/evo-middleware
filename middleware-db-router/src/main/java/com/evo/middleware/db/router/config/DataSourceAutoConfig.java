package com.evo.middleware.db.router.config;

import com.evo.middleware.db.router.DBRouterJoinPoint;
import com.evo.middleware.db.router.dynamic.DynamicDataSource;
import com.evo.middleware.db.router.dynamic.DynamicMybatisPlugin;
import com.evo.middleware.db.router.strategy.IDBRouterStrategy;
import com.evo.middleware.db.router.strategy.impl.DBRouterStrategyHashCode;
import com.evo.middleware.db.router.util.PropertyUtil;
import com.evo.middleware.db.router.util.StringUtils;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
public class DataSourceAutoConfig implements EnvironmentAware {

    private static final String TAG_GLOBAL = "global";
    private static final String TAG_POOL = "pool";
    private final Map<String, Map<String, Object>> dataSourceMap = new HashMap<>();
    private Map<String, Object> defaultDataSourceConfig;
    private int dbCount;
    private int tbCount;
    private String routerKey;

    public DataSourceAutoConfig() {
    }

    @Bean(name = {"db-router-point"})
    @ConditionalOnMissingBean
    public DBRouterJoinPoint point(DBRouterConfig dbRouterConfig, IDBRouterStrategy dbRouterStrategy) {
        return new DBRouterJoinPoint(dbRouterConfig, dbRouterStrategy);
    }

    @Bean
    public DBRouterConfig dbRouterConfig() {
        return new DBRouterConfig(this.dbCount, this.tbCount, this.routerKey);
    }

    @Bean
    public Interceptor plugin() {
        return new DynamicMybatisPlugin();
    }

    private DataSource createDataSource(Map<String, Object> attributes) {
        try {
            DataSourceProperties dataSourceProperties = new DataSourceProperties();
            dataSourceProperties.setUrl(attributes.get("url").toString());
            dataSourceProperties.setUsername(attributes.get("username").toString());
            dataSourceProperties.setPassword(attributes.get("password").toString());
            String driverClassName = attributes.get("driver-class-name") == null ? "com.zaxxer.hikari.HikariDataSource" : attributes.get("driver-class-name").toString();
            dataSourceProperties.setDriverClassName(driverClassName);
            String typeClassName = attributes.get("type-class-name") == null ? "com.zaxxer.hikari.HikariDataSource" : attributes.get("type-class-name").toString();
            DataSource ds = dataSourceProperties.initializeDataSourceBuilder().type((Class<? extends DataSource>) Class.forName(typeClassName)).build();
            MetaObject dsMeta = SystemMetaObject.forObject(ds);
            Map<String, Object> poolProps = (Map<String, Object>) attributes.getOrDefault(TAG_POOL, Collections.EMPTY_MAP);

            for (Map.Entry<String, Object> entry : poolProps.entrySet()) {
                String key = StringUtils.middleScoreToCamelCase(entry.getKey());
                if (dsMeta.hasSetter(key)) {
                    dsMeta.setValue(key, entry.getValue());
                }
            }

            return ds;
        } catch (ClassNotFoundException var11) {
            throw new IllegalArgumentException("can not find datasource type class by class name", var11);
        }
    }

    @Bean
    public DataSource createDataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>();

        for (String dbInfo : this.dataSourceMap.keySet()) {
            Map<String, Object> objMap = this.dataSourceMap.get(dbInfo);
            DataSource ds = this.createDataSource(objMap);
            targetDataSources.put(dbInfo, ds);
        }

        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDefaultTargetDataSource(this.createDataSource(this.defaultDataSourceConfig));
        return dynamicDataSource;
    }

    @Bean
    public IDBRouterStrategy dbRouterStrategy(DBRouterConfig dbRouterConfig) {
        return new DBRouterStrategyHashCode(dbRouterConfig);
    }

    @Bean
    public TransactionTemplate transactionTemplate(DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);
        TransactionTemplate transactionTemplate = new TransactionTemplate();
        transactionTemplate.setTransactionManager(dataSourceTransactionManager);
        transactionTemplate.setPropagationBehaviorName("PROPAGATION_REQUIRED");
        return transactionTemplate;
    }

    public void setEnvironment(Environment environment) {
        String prefix = "mini-db-router.jdbc.datasource.";
        this.dbCount = Integer.parseInt(Objects.requireNonNull(environment.getProperty(prefix + "dbCount")));
        this.tbCount = Integer.parseInt(Objects.requireNonNull(environment.getProperty(prefix + "tbCount")));
        this.routerKey = environment.getProperty(prefix + "routerKey");
        String dataSources = environment.getProperty(prefix + "list");
        Map<String, Object> globalInfo = this.getGlobalProps(environment, prefix + TAG_GLOBAL);

        for (String dbInfo : dataSources.split(",")) {
            String dbPrefix = prefix + dbInfo;
            Map<String, Object> dataSourceProps = PropertyUtil.handle(environment, dbPrefix, Map.class);
            this.injectGlobal(dataSourceProps, globalInfo);
            this.dataSourceMap.put(dbInfo, dataSourceProps);
        }

        String defaultData = environment.getProperty(prefix + "default");
        this.defaultDataSourceConfig = PropertyUtil.handle(environment, prefix + defaultData, Map.class);
        this.injectGlobal(this.defaultDataSourceConfig, globalInfo);
    }

    private Map<String, Object> getGlobalProps(Environment environment, String key) {
        try {
            return PropertyUtil.handle(environment, key, Map.class);
        } catch (Exception var4) {
            return Collections.EMPTY_MAP;
        }
    }

    private void injectGlobal(Map<String, Object> origin, Map<String, Object> global) {
        for (String key : global.keySet()) {
            if (!origin.containsKey(key)) {
                origin.put(key, global.get(key));
            } else if (origin.get(key) instanceof Map) {
                this.injectGlobal((Map) origin.get(key), (Map) global.get(key));
            }
        }

    }

}
