package com.evo.middleware.tx.coordinator.publisher;

import com.evo.middleware.tx.coordinator.TransactionCoordinator;
import com.evo.middleware.tx.coordinator.synchronizor.TransactionEventSynchronization;
import com.evo.middleware.tx.domain.PublisherMode;
import com.evo.middleware.tx.domain.event.TransactionEvent;
import com.evo.middleware.tx.exception.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.ExecutorService;

public class TransactionCoordinatorPublisher implements ApplicationEventPublisherAware {

    private static final Logger log = LoggerFactory.getLogger(TransactionCoordinatorPublisher.class);

    private ApplicationEventPublisher applicationEventPublisher;
    private final TransactionCoordinator transactionCoordinator;

    public TransactionCoordinatorPublisher(TransactionCoordinator transactionCoordinator) {
        this.transactionCoordinator = transactionCoordinator;
    }

    public void publishEvent(TransactionEvent event) {
        publishEvent(event, PublisherMode.TX_SYNCHRONIZATION);
    }

    public void publishAsyncEvent(TransactionEvent event, ExecutorService pool) {
        publishAsyncEvent(event, pool, PublisherMode.TX_SYNCHRONIZATION);
    }

    public void publishAsyncEvent(TransactionEvent event, ExecutorService pool, PublisherMode mode) {
        event.pool(pool);
        publishEvent(event, mode);
    }

    public void publishEvent(TransactionEvent event, PublisherMode mode) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            log.warn("not in transaction, run event now");
            event.run();
            return;
        }

        try {
            if (PublisherMode.TX_LISTENER == mode) {
                applicationEventPublisher.publishEvent(event);
            } else {
                TransactionSynchronizationManager.registerSynchronization(new TransactionEventSynchronization(event, transactionCoordinator));
            }
        } catch (Exception e) {
            throw new TransactionException("publish transaction error", e);
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
