package com.evo.middleware.db.router.strategy.impl;

import com.evo.middleware.db.router.DBContextHolder;
import com.evo.middleware.db.router.config.DBRouterConfig;
import com.evo.middleware.db.router.strategy.IDBRouterStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBRouterStrategyHashCode implements IDBRouterStrategy {
    private final Logger logger = LoggerFactory.getLogger(DBRouterStrategyHashCode.class);
    private final DBRouterConfig dbRouterConfig;

    public DBRouterStrategyHashCode(DBRouterConfig dbRouterConfig) {
        this.dbRouterConfig = dbRouterConfig;
    }

    public void doRouter(String dbKeyAttr) {
        int size = this.dbRouterConfig.getDbCount() * this.dbRouterConfig.getTbCount();
        int idx = size - 1 & (dbKeyAttr.hashCode() ^ dbKeyAttr.hashCode() >>> 16);
        int dbIdx = idx / this.dbRouterConfig.getTbCount() + 1;
        int tbIdx = idx - this.dbRouterConfig.getTbCount() * (dbIdx - 1);
        DBContextHolder.setDBKey(String.format("%02d", dbIdx));
        DBContextHolder.setTBKey(String.format("%03d", tbIdx));
        this.logger.debug("数据库路由 dbIdx：{} tbIdx：{}", dbIdx, tbIdx);
    }

    public void setDBKey(int dbIdx) {
        DBContextHolder.setDBKey(String.format("%02d", dbIdx));
    }

    public void setTBKey(int tbIdx) {
        DBContextHolder.setTBKey(String.format("%03d", tbIdx));
    }

    public int dbCount() {
        return this.dbRouterConfig.getDbCount();
    }

    public int tbCount() {
        return this.dbRouterConfig.getTbCount();
    }

    public void clear() {
        DBContextHolder.clearDBKey();
        DBContextHolder.clearTBKey();
    }
}
