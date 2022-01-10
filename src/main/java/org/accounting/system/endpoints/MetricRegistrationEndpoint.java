package org.accounting.system.endpoints;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.MetricRegistrationDtoRequest;
import org.accounting.system.dtos.MetricRegistrationDtoResponse;
import org.accounting.system.services.MetricRegistrationService;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Objects;
import java.util.Optional;

@Path("/metric-registration")
public class MetricRegistrationEndpoint {

    @Inject
    private MetricRegistrationService metricRegistrationService;


    public MetricRegistrationEndpoint(MetricRegistrationService metricRegistrationService) {
        this.metricRegistrationService = metricRegistrationService;
    }


    @Tag(name = "Submit Metric Registration.")
    @Operation(
            summary = "Records a new Metric Registration.",
            description = "Retrieves and inserts a Metric Registration into the database. Typically, " +
                    "a Metric Registration is the metadata describing a Virtual Access Metric.")
    @APIResponse(
            responseCode = "201",
            description = "Metric Registration has been created successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = MetricRegistrationDtoResponse.class)))
    @APIResponse(
            responseCode = "400",
            description = "Bad Request.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "401",
            description = "User has not been authenticated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "415",
            description = "Cannot consume content type.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Errors.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = InformativeResponse.class)))

    @POST
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response save(MetricRegistrationDtoRequest metricRegistrationDtoRequest, @Context UriInfo uriInfo) {

        if(Objects.isNull(metricRegistrationDtoRequest)){

            throw new BadRequestException("The request body is empty.");
        }

        MetricRegistrationDtoResponse response = metricRegistrationService.save(metricRegistrationDtoRequest);

        return Response.created(uriInfo.getAbsolutePathBuilder().path(response.id).build()).entity(response).build();
    }

    @Tag(name = "Search a Metric Registration.")
    @Operation(
            summary = "Returns an existing Metric Registration.",
            description = "This operation accepts the id of a Metric Registration and fetches from the database the corresponding record.")
    @APIResponse(
            responseCode = "200",
            description = "The corresponding Metric Registration.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = MetricRegistrationDtoResponse.class)))
    @APIResponse(
            responseCode = "401",
            description = "User has not been authenticated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "404",
            description = "Metric Registration has not been found.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Errors.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = InformativeResponse.class)))

    @GET
    @Path("/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response get(
            @Parameter(
                    description = "The assigned id to the Metric Registration.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") String id){

        Optional<MetricRegistrationDtoResponse> response = metricRegistrationService.storedMetricRegistrationToResponseDto(id);

        return response
                .map(dto->Response.ok().entity(response.get()).build())
                .orElseThrow(()-> new NotFoundException("The Metric Registration has not been found."));


    }

    @Tag(name = "Edit Metric Registration.")
    @Operation(
            summary = "Updates an existing Metric Registration.",
            description = "This operation lets you update only a part of a Metric Registration by updating the existing attributes.")
    @APIResponse(
            responseCode = "200",
            description = "Metric Registration was updated successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = MetricRegistrationDtoResponse.class)))
    @APIResponse(
            responseCode = "401",
            description = "User has not been authenticated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "404",
            description = "Metric Registration has not been found.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "415",
            description = "Cannot consume content type.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Errors.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = InformativeResponse.class)))

    @PATCH
    @Path("/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response update(
            @Parameter(
                    description = "The assigned id to the Metric Registration.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("id") String id, MetricRegistrationDtoRequest metricRegistrationDtoRequest){


        MetricRegistrationDtoResponse response = metricRegistrationService.update(id, metricRegistrationDtoRequest);

        return Response.ok().entity(response).build();

    }


}
