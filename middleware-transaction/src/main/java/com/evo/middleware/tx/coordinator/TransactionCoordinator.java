package com.evo.middleware.tx.coordinator;

import com.evo.middleware.tx.domain.event.TransactionEvent;

/***
 * 事务协调器
 * TransactionSynchronization 可以根据各个状态进行扩展
     public interface TransactionSynchronization {
         default void suspend() {} // 事务挂起时调用
         default void resume() {}  // 事务恢复时调用
         void beforeCommit(boolean readOnly); // 提交前调用
         void beforeCompletion();  // 提交或回滚前调用
         void afterCommit();       // 提交后调用
         void afterCompletion(int status); // 提交或回滚完成后调用
 * }
 */
public interface TransactionCoordinator {

    /**
     * 监听提交前操作
     */
    void beforeCommit(TransactionEvent event);

    /**
     * 监听提交后操作
     */
    void afterCommit(TransactionEvent event);

    /**
     * 监听回滚操作
     */
    void afterRollBack(TransactionEvent event);

    /**
     * 监听事务完成或者回滚完毕操作
     */
    void afterCompletion(TransactionEvent event);
}
