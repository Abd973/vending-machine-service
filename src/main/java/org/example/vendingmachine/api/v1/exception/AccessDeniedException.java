package org.example.vendingmachine.api.v1.exception;

public class AccessDeniedException extends RuntimeException {
    private static final String Message = "Access denied, you are not allowed to access this %s.";
    public AccessDeniedException(String type) {
        super(Message.formatted( type));
    }

}
