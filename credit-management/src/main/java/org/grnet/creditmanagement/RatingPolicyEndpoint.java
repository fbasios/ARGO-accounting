package org.grnet.creditmanagement;

import io.quarkus.security.Authenticated;
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
import org.grnet.creditmanagement.dtos.RatingPolicyRequestDto;
import org.grnet.creditmanagement.dtos.RatingPolicyResponseDto;
import org.grnet.creditmanagement.exceptions.RatingPolicyConflictExceptionMapper;
import org.grnet.creditmanagement.services.RatingPolicyService;

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
                    "onward. Inserting an entry with a valid_from between two existing entries effectively " +
                    "shortens the preceding entry's derived effective period; inserting one later than the most " +
                    "recent existing entry effectively closes that entry's open-ended period as of the new " +
                    "valid_from. A valid_from earlier than the earliest already recorded entry for the same " +
                    "Installation and Metric Definition is rejected, to prevent retroactively altering the " +
                    "start of a rating history that may have already been reported on.")
    @APIResponse(
            responseCode = "200",
            description = "The Rating Policy has been successfully created.",
            content = @Content(schema = @Schema(implementation = RatingPolicyResponseDto.class)))
    @APIResponse(
            responseCode = "400",
            description = "A Rating Policy with the exact same valid_from already exists for this Installation " +
                    "and Metric Definition, or the given valid_from is earlier than the earliest already " +
                    "recorded valid_from for this Installation and Metric Definition.",
            content = @Content(schema = @Schema(implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @APIResponse(
            responseCode = "404",
            description = "Installation, or Metric Definition does not exist.",
            content = @Content(schema = @Schema(implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Error.",
            content = @Content(schema = @Schema(implementation = RatingPolicyConflictExceptionMapper.ErrorResponse.class)))
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
            @Valid @NotNull RatingPolicyRequestDto request) {

        var response = ratingPolicyService.createRatingPolicy(installationId, metricDefinitionId, request);

        return Response.ok(response).build();
    }
}