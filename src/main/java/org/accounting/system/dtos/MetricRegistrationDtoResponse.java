package org.accounting.system.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="MetricRegistrationResponse", description="An object represents the stored Metric Registration.")
public class MetricRegistrationDtoResponse {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The unique id for the Metric Registration."
    )
    @JsonProperty("metric_registration_id")
    public String id;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The name of the Virtual Access Metric."
    )
    @JsonProperty("metric_name")
    public String metricName;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Description of how the metric is collected."
    )
    @JsonProperty("metric_description")
    public String metricDescription;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Unit Type of the Virtual Access Metric."
    )
    @JsonProperty("unit_type")
    public String unitType;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Metric Type of the Virtual Access Metric."
    )
    @JsonProperty("metric_type")
    public String metricType;


}
