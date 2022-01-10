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
import org.accounting.system.dtos.MetricRequestDto;
import org.accounting.system.dtos.MetricResponseDto;
import org.accounting.system.dtos.MetricsPaginationDto;
import org.accounting.system.services.MetricService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Objects;
import java.util.Optional;

@Path("/metric-registration/{metric_registration_id}/metrics")
public class MetricEndpoint {

    @Inject
    private MetricService metricService;


    public MetricEndpoint(MetricService metricService) {
        this.metricService = metricService;
    }


    @Tag(name = "Submit Virtual Access Metric.")
    @Operation(
            summary = "Registers a new Virtual Access Metric.",
            description = "Retrieves and inserts a VA Metric into the database. " +
                    "The VA Metric is assigned to the Metric Registration with id metric_registration_id.")
    @APIResponse(
            responseCode = "201",
            description = "Virtual Access Metric has been created successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = MetricResponseDto.class)))
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

    @POST
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response save(@Parameter(
            description = "The assigned id to the Metric Registration.",
            required = true,
            example = "507f1f77bcf86cd799439011",
            schema = @Schema(type = SchemaType.STRING))
                         @PathParam("metric_registration_id") String metricRegistrationId, @Valid MetricRequestDto metric, @Context UriInfo uriInfo) {

        if (Objects.isNull(metric)) {

            throw new BadRequestException("The request body is empty.");
        }

        MetricResponseDto response = metricService.save(metric, metricRegistrationId);

        return Response.created(uriInfo.getAbsolutePathBuilder().path(response.id).build()).entity(response).build();
    }

    @Tag(name = "Search a Virtual Access Metric.")
    @Operation(
            summary = "Returns an existing Virtual Access Metric.",
            description = "This operation accepts the id of a Virtual Access Metric and fetches from the database the corresponding record.")
    @APIResponse(
            responseCode = "200",
            description = "The corresponding Virtual Access Metric.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = MetricResponseDto.class)))
    @APIResponse(
            responseCode = "401",
            description = "User has not been authenticated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "404",
            description = "Virtual Access Metric has not been found.",
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
    @Path("/{metric_id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response get(
            @Parameter(
                    description = "The assigned id to the Metric Registration.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("metric_registration_id") String metricRegistrationId,
            @Parameter(
                    description = "The assigned id to the Virtual Access Metric.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("metric_id") String metricId){

        Optional<MetricResponseDto> response = metricService.storedMetricToResponseDto(metricId, metricRegistrationId);

        return response
                .map(dto->Response.ok().entity(response.get()).build())
                .orElseThrow(()-> new NotFoundException("The Virtual Access Metric has not been found."));

    }

    @Tag(name = "Edit Virtual Access Metric.")
    @Operation(
            summary = "Updates an existing Virtual Access Metric.",
            description = "This operation lets you update only a part of a Virtual Access Metric by updating the existing attributes.")
    @APIResponse(
            responseCode = "200",
            description = "Virtual Access Metric was updated successfully.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = MetricResponseDto.class)))
    @APIResponse(
            responseCode = "401",
            description = "User has not been authenticated.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = InformativeResponse.class)))
    @APIResponse(
            responseCode = "404",
            description = "Metric Registration/Virtual Access Metric has not been found.",
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
    @Path("/{metric_id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response update(
            @Parameter(
                    description = "The assigned id to the Metric Registration.",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("metric_registration_id") String metricRegistrationId,
            @Parameter(
                    description = "The assigned id to the Virtual Access Metric.",
                    required = true,
                    example = "61dbe3f10086512c9ff1197a",
                    schema = @Schema(type = SchemaType.STRING))
            @PathParam("metric_id") String metricId,
            MetricRegistrationDtoRequest metricRegistrationDtoRequest){


        return Response.ok().build();

    }

    @Tag(name = "Retrieve Virtual Access Metrics.")
    @Operation(
            summary = "Retrieves Virtual Access Metrics for specific Metric Registration.",
            description = "This operation returns the metrics assigned to a Metric Registration.")
    @APIResponse(
            responseCode = "200",
            description = "Success operation.",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = MetricsPaginationDto.class)))
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
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response get(@Parameter(
            description = "Reference Id from the metric definition.",
            required = true,
            example = "507f1f77bcf86cd799439011",
            schema = @Schema(type = SchemaType.STRING))
                            @PathParam("metric_registration_id") String metricRegistrationId,
                        @DefaultValue("0") @QueryParam("page") int page,
                        @DefaultValue("10") @QueryParam("size") int size) {

        MetricsPaginationDto metrics = metricService.storedMetricsToResponseDto(metricRegistrationId, page, size);

        return Response.ok().entity(metrics).build();

    }

}
