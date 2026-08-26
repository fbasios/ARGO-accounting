package org.grnet.creditmanagement.security;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.grnet.creditmanagement.entities.RatingPolicyEntity;
import org.grnet.creditmanagement.repositories.ExternalEntityLookupRepository;
import org.grnet.creditmanagement.repositories.RatingPolicyRepository;

/**
 * Enforces that Credit Management is enabled for the project a request
 * targets. Applies only to resources/methods annotated with
 * @CreditManagementSecured.
 *
 * project_id is resolved, in order:
 * 1. Directly from the {project_id} path parameter, if present.
 * 2. From {installation_id}, via the HierarchicalRelation lookup.
 * 3. From {policy_id}, by first looking up the Rating Policy entry to
 *    get its installation_id, then resolving that as in (2).
 */
@Provider
@CreditManagementSecured
@Priority(Priorities.AUTHORIZATION)
public class CreditManagementAccessFilter implements ContainerRequestFilter {

    @Inject
    ExternalEntityLookupRepository externalEntityLookupRepository;

    @Inject
    RatingPolicyRepository ratingPolicyRepository;

    @Override
    public void filter(ContainerRequestContext requestContext) {

        var projectId = resolveProjectId(requestContext);

        if (projectId == null) {
            abortWithForbidden(requestContext, "Unable to determine the project for this request.");
            return;
        }

        if (!externalEntityLookupRepository.projectExists(projectId)) {
            abortWithNotFound(requestContext, "Project not found: " + projectId);
            return;
        }

        if (!externalEntityLookupRepository.isCreditManagementEnabled(projectId)) {
            abortWithForbidden(requestContext,
                    "Credit Management is not enabled for project " + projectId + ".");
        }
    }

    private String resolveProjectId(ContainerRequestContext requestContext) {

        var pathParameters = requestContext.getUriInfo().getPathParameters();

        var projectId = pathParameters.getFirst("project_id");

        if (projectId != null) {
            return projectId;
        }

        var installationId = pathParameters.getFirst("installation_id");

        if (installationId != null) {
            return externalEntityLookupRepository
                    .resolveProjectIdForInstallation(installationId)
                    .orElse(null);
        }

        var policyId = pathParameters.getFirst("policy_id");

        if (policyId != null) {

            var installationIdFromPolicy = ratingPolicyRepository
                    .findByIdOptional(policyId)
                    .map(RatingPolicyEntity::getInstallationId)
                    .orElse(null);

            if (installationIdFromPolicy != null) {
                return externalEntityLookupRepository
                        .resolveProjectIdForInstallation(installationIdFromPolicy)
                        .orElse(null);
            }
        }

        return null;
    }

    private void abortWithForbidden(ContainerRequestContext requestContext, String message) {

        requestContext.abortWith(
                Response.status(Response.Status.FORBIDDEN)
                        .entity(new ErrorResponse(403, message))
                        .type(MediaType.APPLICATION_JSON)
                        .build());
    }

    public static class ErrorResponse {
        public int code;
        public String message;

        public ErrorResponse(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    private void abortWithNotFound(ContainerRequestContext requestContext, String message) {

        requestContext.abortWith(
                Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse(404, message))
                        .type(MediaType.APPLICATION_JSON)
                        .build());
    }
}