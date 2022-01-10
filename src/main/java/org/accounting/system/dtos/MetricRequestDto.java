package org.accounting.system.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.accounting.system.constraints.ZuluTime;

import java.time.Instant;

@Schema(name="VirtualAccessMetricRequest", description="An object represents a request for creating a Virtual Access Metric.")
public class MetricRequestDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Unique Identifier from the resource."
    )
    @JsonProperty("resource_id")
    public String resourceId;

    @Schema(
            type = SchemaType.STRING,
            implementation = Instant.class,
            description = "Timestamp of the starting date time (Zulu timestamp).",
            example = "2022-01-05T09:13:07Z",
            required = true
    )
    @JsonProperty("time_period_start")
    @ZuluTime(message = "time_period_start")
    public String start;

    @Schema(
            type = SchemaType.STRING,
            implementation = Instant.class,
            description = "Timestamp of the end date time (Zulu timestamp).",
            example = "2022-01-05T09:13:07Z",
            required = true
    )
    @JsonProperty("time_period_end")
    @ZuluTime(message = "time_period_end")
    public String end;

    @Schema(
            type = SchemaType.STRING,
            implementation = Double.class,
            description = "Value of the metric for the given period."
    )
    @JsonProperty("value")
    public double value;

}
