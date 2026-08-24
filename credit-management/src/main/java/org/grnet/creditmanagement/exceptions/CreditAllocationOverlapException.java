package org.grnet.creditmanagement.exceptions;

public class CreditAllocationOverlapException extends RuntimeException {
    public CreditAllocationOverlapException(String message) {
        super(message);
    }
}