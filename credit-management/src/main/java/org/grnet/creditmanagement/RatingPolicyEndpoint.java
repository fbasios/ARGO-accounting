package org.grnet.creditmanagement;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.grnet.creditmanagement.dtos.CurrentRatingPolicyEntryDto;
import org.grnet.creditmanagement.dtos.RatingPolicyRequestDto;
import org.grnet.creditmanagement.dtos.RatingPolicyResponseDto;
import org.grnet.creditmanagement.dtos.RatingPolicyUpdateRequestDto;
import org.grnet.creditmanagement.exceptions.RatingPolicyConflictExceptionMapper;
import org.grnet.creditmanagement.pagination.PageResource;
import org.grnet.creditmanagement.services.RatingPolicyService;

import java.util.List;

import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.PATH;

@Path("/installations")
@Authenticated
@SecurityScheme(securitySchemeName = "Authentication",
        description = "JWT token",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)
public class RatingPolicyEndpoint {

    @Inject
    RatingPolicyService ratingPolicyService;

    @Tag(name = "Credit Management")
    @Operation(
            summary = "Submit a rate for a metric on an Installation.",
            description = "Creates a new Rating Policy entry for the given Installation and Metric Definition, " +
                    "effective from the given valid_from date. No end date is accepted or stored — the entry's " +
                    "effective end is implicitly determined by whichever entry (if any) has the next later " +
                    "valid_from for the same Installation and Metric Definition. Submitting a rate for a metric " +
                    "that has never been rated on this Installation before starts rating it from valid_from " +
                    "onward. The given valid_from is automatically rounded down to the start of the day (00:00:00 UTC) " +
                    "before being stored. Inserting an entry with a valid_from between two existing entries effectively " +
                    "shortens the preceding entry's derived effective period; inserting one later than the most " +
                    "recent existing entry effectively closes that entry's open-ended period as of the new " +
                    "valid_from. A valid_from earlier than the earliest already recorded entry for the same " +
                    "Installation and Metric Definition is rejected, to prevent retroactively altering the " +
                    "start of a rating history that may have already been reported on.")
    @APIResponse(
            responseCode = "200",
            description = "The Rating Policy has been successfully created.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyResponseDto.class)))
    @APIResponse(
            responseCode = "400",
            description = "The request is invalid: valid_from is not at the start of a day (00:00:00 UTC), " +
                    "an entry with the exact same valid_from already exists for this Installation and Metric " +
                    "Definition, or the given valid_from is earlier than the earliest already recorded " +
                    "valid_from for this Installation and Metric Definition.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @APIResponse(
            responseCode = "404",
            description = "Installation, or Metric Definition does not exist.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Error.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @SecurityRequirement(name = "Authentication")
    @POST
    @Path("/{installation_id}/metrics/{metric_definition_id}/rate-policy")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response submitRatingPolicy(@Parameter(
            name = "installation_id",
            in = PATH,
            description = "The installation id.",
            required = true,
            schema = @Schema(type = SchemaType.STRING, implementation = String.class, example = "707f1f77bcf86cd799439013"))
            @PathParam("installation_id") String installationId,
                                       @Parameter(
                                               name = "metric_definition_id",
                                               in = PATH,
                                               description = "The metric definition id.",
                                               required = true,
                                               schema = @Schema(type = SchemaType.STRING, implementation = String.class, example = "707f1f77bcf86cd799439014"))
            @PathParam("metric_definition_id") String metricDefinitionId,
            @RequestBody(description = "The valid_from date and rate to apply.",
                    content = @Content(schema = @Schema(implementation = RatingPolicyRequestDto.class)))
            @Valid @NotNull(message = "The request body is empty.") RatingPolicyRequestDto request) {

        var response = ratingPolicyService.createRatingPolicy(installationId, metricDefinitionId, request);

        return Response.ok(response).build();
    }

    @Tag(name = "Credit Management")
    @Operation(
            summary = "Retrieve the currently effective rates for an Installation.",
            description = "Returns, for the given Installation, one Rating Policy entry per metric_definition_id: " +
                    "for each metric_definition_id that has ever had an entry, the entry with the latest " +
                    "valid_from that is less than or equal to the current time. If a metric_definition_id has " +
                    "no entry with valid_from less than or equal to the current time, it is simply absent from " +
                    "the response rather than causing an error, reflecting that it is currently unrated.")
    @APIResponse(
            responseCode = "200",
            description = "The list of currently effective Rating Policy entries for the Installation. " +
                    "May be empty if no metric has ever been rated on this Installation.",
            content = @Content(schema = @Schema(type = SchemaType.ARRAY, implementation = CurrentRatingPolicyEntryDto.class)))
    @APIResponse(
            responseCode = "404",
            description = "Installation does not exist.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Error.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @SecurityRequirement(name = "Authentication")
    @GET
    @Path("/{installation_id}/rate-policies/current")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentRatingPolicies(
            @Parameter(name = "installation_id", in = PATH, description = "The installation id.", required = true,
                    schema = @Schema(type = SchemaType.STRING, implementation = String.class, example = "707f1f77bcf86cd799439013"))
            @PathParam("installation_id") String installationId) {

        var response = ratingPolicyService.getCurrentRatingPolicies(installationId);

        return Response.ok(response).build();
    }

    @Tag(name = "Credit Management")
    @Operation(
            summary = "List all Rate Policies for an Installation.",
            description = "Returns a paginated list of all Rating Policy entries recorded for the given " +
                    "Installation, across all metric definitions, ordered by metric_definition_id and then " +
                    "valid_from. If the Installation exists but has no Rating Policy entries at all, an empty " +
                    "paginated result is returned rather than an error.")
    @APIResponse(
            responseCode = "200",
            description = "A paginated list of Rating Policy entries for the Installation.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = PageableRatingPolicyResponseDto.class)))
    @APIResponse(
            responseCode = "404",
            description = "Installation does not exist.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Error.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @SecurityRequirement(name = "Authentication")
    @GET
    @Path("/installations/{installation_id}/rate-policies")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listRatingPolicies(
            @Parameter(name = "installation_id", in = PATH, description = "The installation id.", required = true,
                    schema = @Schema(type = SchemaType.STRING, implementation = String.class, example = "707f1f77bcf86cd799439013"))
            @PathParam("installation_id") String installationId,

            @Parameter(name = "page", description = "The page number. Must be >= 1.")
            @Min(value = 1, message = "Page size must be between 1 and 100.")
            @DefaultValue("1") @QueryParam("page") int page,

            @Parameter(name = "size", description = "The page size.")
            @Max(value = 100, message = "Page size must be between 1 and 100.")
            @DefaultValue("10") @QueryParam("size") int size,
            @Context UriInfo uriInfo) {

        var response = ratingPolicyService.getAllRatingPolicies(installationId, page, size, uriInfo);

        return Response.ok(response).build();
    }

    @Tag(name = "Credit Management")
    @Operation(
            summary = "Update the rate of an existing Rating Policy entry.",
            description = "Updates the rate of the Rating Policy entry identified by policy_id, for the given " +
                    "Installation and Metric Definition. Only the rate can be updated — the valid_from date of " +
                    "the entry cannot be changed through this endpoint.")
    @APIResponse(
            responseCode = "200",
            description = "The Rating Policy entry has been successfully updated.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyResponseDto.class)))
    @APIResponse(
            responseCode = "400",
            description = "The request body is invalid.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @APIResponse(
            responseCode = "404",
            description = "The Installation, Metric Definition, or Rating Policy entry does not exist, or the " +
                    "Rating Policy entry does not belong to the given Installation and Metric Definition.",
            content = @Content(schema = @Schema(type = SchemaType.OBJECT, implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Error.")
    @SecurityRequirement(name = "Authentication")
    @PATCH
    @Path("/rate-policies/{policy_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRatingPolicy(
            @Parameter(name = "policy_id", in = PATH, description = "The Rating Policy entry id.", required = true,
                    schema = @Schema(type = SchemaType.STRING, implementation = String.class, example = "64f1a2b3c4d5e6f7a8b9c0d1"))
            @PathParam("policy_id") String policyId,

            @RequestBody(description = "The new rate to apply.", required = true,
                    content = @Content(schema = @Schema(implementation = RatingPolicyUpdateRequestDto.class)))
            @Valid @NotNull(message = "The request body is empty.") RatingPolicyUpdateRequestDto request) {

        var response = ratingPolicyService.updateRatingPolicy(policyId, request);

        return Response.ok(response).build();
    }

    public static class PageableRatingPolicyResponseDto extends PageResource<RatingPolicyResponseDto> {

        private List<RatingPolicyResponseDto> content;

        @Override
        public List<RatingPolicyResponseDto> getContent() {
            return content;
        }

        @Override
        public void setContent(List<RatingPolicyResponseDto> content) {
            this.content = content;
        }
    }
}