package com.evo.middleware.tx.coordinator;

import com.evo.middleware.tx.domain.event.TransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public abstract class AbstractTransactionCoordinatorAdapter implements TransactionCoordinator {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected ExecutorService pool;

    public AbstractTransactionCoordinatorAdapter() {
    }

    public AbstractTransactionCoordinatorAdapter(ExecutorService pool) {
        this.pool = pool;
    }

    @Override
    public void beforeCommit(TransactionEvent event) {
        executeEvent(event);
    }

    @Override
    public void afterCommit(TransactionEvent event) {
        executeEvent(event);
    }

    @Override
    public void afterRollBack(TransactionEvent event) {
        executeEvent(event);
    }

    @Override
    public void afterCompletion(TransactionEvent event) {
        executeEvent(event);
    }

    protected void executeEvent(TransactionEvent event) {
        event.run();
        logger.debug("Transaction event is executed synchronously");
    }
}
