package org.grnet.creditmanagement;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.grnet.creditmanagement.dtos.CreditManagementToggleRequestDto;
import org.grnet.creditmanagement.dtos.CreditManagementToggleResponseDto;
import org.grnet.creditmanagement.security.AdminCreditManagementService;

@Path("/admin")
public class AdminCreditManagementEndpoint {

    @Inject
    AdminCreditManagementService adminCreditManagementService;

    @Tag(name = "System Administrator")
    @Operation(
            summary = "Enable or disable Credit Management for a project.",
            description = "Sets the credit_management_enabled flag on the given Project. When disabled " +
                    "(the default for all projects), requests to any Credit Management endpoint for this " +
                    "project are rejected with 403 Forbidden. This endpoint itself is not gated by that " +
                    "flag — it is how the flag gets turned on in the first place.")
    @APIResponse(
            responseCode = "200",
            description = "The flag has been updated.",
            content = @Content(schema = @Schema(implementation = CreditManagementToggleResponseDto.class)))
    @APIResponse(
            responseCode = "400",
            description = "The request body is invalid (enabled is missing).")
    @APIResponse(
            responseCode = "404",
            description = "The Project does not exist.")
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Error.")
    @SecurityRequirement(name = "Authentication")
    @PATCH
    @Path("/projects/{project_id}/credit-management")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setCreditManagementEnabled(

            @Parameter(name = "project_id", in = ParameterIn.PATH, description = "The project id.", required = true,
                    schema = @Schema(type = SchemaType.STRING, implementation = String.class, example = "707f1f77bcf86cd799439011"))
            @PathParam("project_id") String projectId,

            @RequestBody(description = "Whether to enable or disable Credit Management for this project.",
                    content = @Content(schema = @Schema(implementation = CreditManagementToggleRequestDto.class)))
            @Valid @NotNull(message = "The request body is empty.") CreditManagementToggleRequestDto request) {

        var response = adminCreditManagementService.setEnabled(projectId, request);

        return Response.ok(response).build();
    }
}