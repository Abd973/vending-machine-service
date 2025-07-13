package org.example.vendingmachine.api.v1.exception;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class InsufficientProductQuantityException extends RuntimeException {
    private static final String MESSAGE = "This product quantity = %s which is not enough for the requested quantity = %s";
    public InsufficientProductQuantityException(int productQuantity, int requestedQuantity) {
        super(MESSAGE.formatted(productQuantity, requestedQuantity));
    }
}
