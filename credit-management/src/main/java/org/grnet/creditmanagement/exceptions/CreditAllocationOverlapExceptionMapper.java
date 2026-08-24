package org.grnet.creditmanagement.exceptions;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CreditAllocationOverlapExceptionMapper implements ExceptionMapper<CreditAllocationOverlapException> {

    @Override
    public Response toResponse(CreditAllocationOverlapException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new RatingPolicyConflictExceptionMapper.ErrorResponse(400, exception.getMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
