package com.evo.middleware.tx.domain.event;

import com.evo.middleware.tx.domain.TransactionData;
import com.evo.middleware.tx.domain.TransactionRunnable;

public class TransactionRollbackEvent extends TransactionCompleteEvent {
    public TransactionRollbackEvent(Runnable runnable) {
        super(runnable);
    }

    public TransactionRollbackEvent(TransactionRunnable runnable) {
        super(runnable);
    }

    public TransactionRollbackEvent(TransactionData data, TransactionRunnable runnable) {
        super(data, runnable);
    }

}
