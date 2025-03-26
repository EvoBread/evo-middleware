package com.evo.middleware.db.router.dynamic;

import com.evo.middleware.db.router.DBContextHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {

    @Value("${mini-db-router.jdbc.datasource.default}")
    private String defaultDataSource;

    public DynamicDataSource() {
    }

    protected Object determineCurrentLookupKey() {
        return null == DBContextHolder.getDBKey() ? this.defaultDataSource : "db" + DBContextHolder.getDBKey();
    }

}