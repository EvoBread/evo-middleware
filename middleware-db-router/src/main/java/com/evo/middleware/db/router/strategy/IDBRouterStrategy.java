package com.evo.middleware.db.router.strategy;

public interface IDBRouterStrategy {
    void doRouter(String var1);

    void setDBKey(int var1);

    void setTBKey(int var1);

    int dbCount();

    int tbCount();

    void clear();
}