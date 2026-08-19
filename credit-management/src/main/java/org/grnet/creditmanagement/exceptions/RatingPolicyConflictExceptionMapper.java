package org.grnet.creditmanagement.exceptions;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RatingPolicyConflictExceptionMapper implements ExceptionMapper<RatingPolicyConflictException> {

    @Override
    public Response toResponse(RatingPolicyConflictException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(400, exception.getMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    public static class ErrorResponse {
        public int code;
        public String message;

        public ErrorResponse(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
