package com.evo.middleware.tx.config;

import com.evo.middleware.tx.coordinator.DefaultTransactionCoordinator;
import com.evo.middleware.tx.coordinator.listener.TransactionListener;
import com.evo.middleware.tx.coordinator.TransactionCoordinator;
import com.evo.middleware.tx.coordinator.publisher.TransactionCoordinatorPublisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransactionCoordinatorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TransactionCoordinator transactionCoordinator() {
        return new DefaultTransactionCoordinator();
    }

    @Bean
    public TransactionListener transactionListener(TransactionCoordinator transactionCoordinator) {
        return new TransactionListener(transactionCoordinator);
    }

    @Bean
    public TransactionCoordinatorPublisher transactionCoordinatorPublisher(TransactionCoordinator transactionCoordinator) {
        return new TransactionCoordinatorPublisher(transactionCoordinator);
    }
}
