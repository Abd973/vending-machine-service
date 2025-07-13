package org.example.vendingmachine.api.v1.exception;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class InsufficientBalanceException extends RuntimeException {
    private static final String  MESSAGE = "You don't have enough credits to buy this product, Available balance: %s";
    public InsufficientBalanceException(int id) {
        super(MESSAGE.formatted(id));
    }
}
