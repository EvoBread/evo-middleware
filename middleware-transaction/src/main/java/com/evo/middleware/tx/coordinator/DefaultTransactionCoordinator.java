package com.evo.middleware.tx.coordinator;

import com.evo.middleware.tx.domain.event.TransactionEvent;

import java.util.concurrent.ExecutorService;

public class DefaultTransactionCoordinator extends AbstractTransactionCoordinatorAdapter {

    public DefaultTransactionCoordinator() {
    }

    public DefaultTransactionCoordinator(ExecutorService pool) {
        super(pool);
    }

    @Override
    public void beforeCommit(TransactionEvent event) {
        super.beforeCommit(event);
    }

    @Override
    public void afterCommit(TransactionEvent event) {
        super.afterCommit(event);
    }

    @Override
    public void afterRollBack(TransactionEvent event) {
        super.afterRollBack(event);
    }

    @Override
    public void afterCompletion(TransactionEvent event) {
        super.afterCompletion(event);
    }

}
