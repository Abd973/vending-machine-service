package org.example.vendingmachine.api.v1.exception;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class NotFoundException extends RuntimeException {
    private static final String MESSAGE = "%s with ID = %s not found";
    public NotFoundException(String type, int id) {
        super(MESSAGE.formatted(type, id));
    }
}
