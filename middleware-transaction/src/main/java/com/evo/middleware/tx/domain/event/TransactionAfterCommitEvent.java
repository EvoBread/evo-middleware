package com.evo.middleware.tx.domain.event;

import com.evo.middleware.tx.domain.TransactionData;
import com.evo.middleware.tx.domain.TransactionRunnable;

public class TransactionAfterCommitEvent extends TransactionEvent {

    public TransactionAfterCommitEvent(TransactionData data, TransactionRunnable runnable) {
        super(data, runnable);
    }

    public TransactionAfterCommitEvent(TransactionRunnable runnable) {
        super(runnable);
    }

    public TransactionAfterCommitEvent(Runnable runnable) {
        super(runnable);
    }
}
