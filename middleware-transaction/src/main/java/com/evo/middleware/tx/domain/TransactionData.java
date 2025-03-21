package com.evo.middleware.tx.domain;

public abstract class TransactionData {

    protected Object data;

    public TransactionData(Object data) {
        this.data = data;
    }
}
