package com.evo.middleware.tx.coordinator.listener;

import com.evo.middleware.tx.domain.event.TransactionAfterCommitEvent;
import com.evo.middleware.tx.domain.event.TransactionCompleteEvent;
import com.evo.middleware.tx.domain.event.TransactionRollbackEvent;
import com.evo.middleware.tx.coordinator.TransactionCoordinator;
import com.evo.middleware.tx.domain.event.TransactionBeforeCommitEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

public class TransactionListener {

    private final static Logger logger = LoggerFactory.getLogger(TransactionListener.class);

    private final TransactionCoordinator transactionCoordinator;

    public TransactionListener(TransactionCoordinator transactionCoordinator) {
        this.transactionCoordinator = transactionCoordinator;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void beforeCommit(TransactionBeforeCommitEvent event){
        logger.info("Listener Transaction Before Commit");
        transactionCoordinator.beforeCommit(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(TransactionAfterCommitEvent event){
        logger.info("Listener Transaction After Commit");
        transactionCoordinator.afterCommit(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void afterRollBack(TransactionRollbackEvent event){
        logger.info("Listener Transaction After Rollback");
        transactionCoordinator.afterRollBack(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    public void afterCompletion(TransactionCompleteEvent event){
        logger.info("Listener Transaction After Completion");
        transactionCoordinator.afterCompletion(event);
    }
}
