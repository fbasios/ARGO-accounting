package org.grnet.creditmanagement;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
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
import org.grnet.creditmanagement.dtos.CreditAllocationRequestDto;
import org.grnet.creditmanagement.dtos.CreditAllocationResponseDto;
import org.grnet.creditmanagement.exceptions.RatingPolicyConflictExceptionMapper;
import org.grnet.creditmanagement.services.CreditAllocationService;

@Path("/projects")
public class CreditAllocationEndpoint {

    @Inject
    CreditAllocationService creditAllocationService;

    @Tag(name = "Credit Allocation")
    @Operation(
            summary = "Allocate a total number of credits to a group under a project for a specific period.",
            description = "Creates a new Credit Allocation for the given project_id and group_id, granting " +
                    "total_credits to be consumed across all Installations under that project, equally, " +
                    "during [valid_from, valid_to). Unlike Rating Policy rates, an allocation represents a " +
                    "closed budget for a bounded period, so both a start and an end date are required. " +
                    "A stored allocation is never modified; changing an allocation always means creating a " +
                    "new entry. For a given project_id/group_id pair, allocation periods must never overlap " +
                    "with each other.")
    @APIResponse(
            responseCode = "200",
            description = "The Credit Allocation has been successfully created.",
            content = @Content(schema = @Schema(implementation = CreditAllocationResponseDto.class)))
    @APIResponse(
            responseCode = "400",
            description = "valid_from is not strictly earlier than valid_to, total_credits is not greater " +
                    "than zero, or the requested period overlaps with an existing allocation for the same " +
                    "project_id and group_id.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @APIResponse(
            responseCode = "404",
            description = "The Project does not exist.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Error.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @SecurityRequirement(name = "Authentication")
    @POST
    @Path("/{project_id}/groups/{group_id}/allocations")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAllocation(

            @Parameter(name = "project_id", in = ParameterIn.PATH, description = "The project id.", required = true,
                    schema = @Schema(type = SchemaType.STRING, implementation = String.class, example = "707f1f77bcf86cd799439011"))
            @PathParam("project_id") String projectId,

            @Parameter(name = "group_id", in = ParameterIn.PATH, description = "The group id.", required = true,
                    schema = @Schema(type = SchemaType.STRING, implementation = String.class, example = "group-42"))
            @PathParam("group_id") String groupId,

            @RequestBody(description = "The allocation period and total credits.",
                    content = @Content(schema = @Schema(implementation = CreditAllocationRequestDto.class)))
            @Valid @NotNull(message = "The request body is empty.") CreditAllocationRequestDto request) {

        var response = creditAllocationService.createAllocation(projectId, groupId, request);

        return Response.ok(response).build();
    }
}
