package com.evo.middleware.tx.coordinator.synchronizor;

import com.evo.middleware.tx.coordinator.TransactionCoordinator;
import com.evo.middleware.tx.domain.event.TransactionAfterCommitEvent;
import com.evo.middleware.tx.domain.event.TransactionBeforeCommitEvent;
import com.evo.middleware.tx.domain.event.TransactionCompleteEvent;
import com.evo.middleware.tx.domain.event.TransactionEvent;
import org.springframework.transaction.support.TransactionSynchronization;

public class TransactionEventSynchronization implements TransactionSynchronization {

    private final TransactionEvent event;
    private final TransactionCoordinator coordinator;

    public TransactionEventSynchronization(TransactionEvent event, TransactionCoordinator coordinator) {
        this.event = event;
        this.coordinator = coordinator;
    }

    @Override
    public void beforeCommit(boolean readOnly) {
        if (!(event instanceof TransactionBeforeCommitEvent)) return;
        coordinator.beforeCommit(event);
    }

    @Override
    public void afterCommit() {
        if (!(event instanceof TransactionAfterCommitEvent)) return;
        coordinator.afterCommit(event);
    }

    @Override
    public void afterCompletion(int status) {
        if (!(event instanceof TransactionCompleteEvent)) return;
        if (status == STATUS_ROLLED_BACK) coordinator.afterRollBack(event);
        else coordinator.afterCompletion(event);
    }
}
