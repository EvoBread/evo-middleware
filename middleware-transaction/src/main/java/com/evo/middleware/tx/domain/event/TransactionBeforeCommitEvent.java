package com.evo.middleware.tx.domain.event;

import com.evo.middleware.tx.domain.TransactionData;
import com.evo.middleware.tx.domain.TransactionRunnable;

public class TransactionBeforeCommitEvent extends TransactionEvent {
    public TransactionBeforeCommitEvent(Runnable runnable) {
        super(runnable);
    }

    public TransactionBeforeCommitEvent(TransactionRunnable runnable) {
        super(runnable);
    }

    public TransactionBeforeCommitEvent(TransactionData data, TransactionRunnable runnable) {
        super(data, runnable);
    }

}
