package com.evo.middleware.tx.domain.event;

import com.evo.middleware.tx.domain.TransactionData;
import com.evo.middleware.tx.domain.TransactionRunnable;
import org.springframework.context.ApplicationEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

public abstract class TransactionEvent extends ApplicationEvent {

    // 需要执行的数据
    protected TransactionData data;
    protected Supplier<ExecutorService> poolOptional;

    public TransactionEvent(Runnable runnable) {
        this(d -> runnable.run());
    }

    public TransactionEvent(TransactionRunnable runnable) {
        this(null, runnable);
    }

    public TransactionEvent(TransactionData data, TransactionRunnable runnable) {
        super(runnable);
        this.data = data;
    }

    public void pool(ExecutorService pool) {
        this.poolOptional = () -> pool;
    }

    public void run() {
        if (poolOptional != null) {
            CompletableFuture.runAsync(() -> ((TransactionRunnable) getSource()).run(data), poolOptional.get());
        } else {
            ((TransactionRunnable) getSource()).run(data);
        }
    }
}
