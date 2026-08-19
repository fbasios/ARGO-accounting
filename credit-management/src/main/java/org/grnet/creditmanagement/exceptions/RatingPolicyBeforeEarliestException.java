package org.grnet.creditmanagement.exceptions;

public class RatingPolicyBeforeEarliestException extends RuntimeException {
    public RatingPolicyBeforeEarliestException(String message) {
        super(message);
    }
}