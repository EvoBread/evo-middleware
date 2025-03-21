package com.evo.middleware.tx.domain.event;

import com.evo.middleware.tx.domain.TransactionData;
import com.evo.middleware.tx.domain.TransactionRunnable;

public class TransactionCompleteEvent extends TransactionEvent {
    public TransactionCompleteEvent(Runnable runnable) {
        super(runnable);
    }

    public TransactionCompleteEvent(TransactionRunnable runnable) {
        super(runnable);
    }

    public TransactionCompleteEvent(TransactionData data, TransactionRunnable runnable) {
        super(data, runnable);
    }

}
