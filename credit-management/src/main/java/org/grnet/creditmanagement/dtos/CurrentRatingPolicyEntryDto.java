package org.grnet.creditmanagement.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;

public class CurrentRatingPolicyEntryDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The metric definition id.",
            example = "707f1f77bcf86cd799439014"
    )
    @JsonProperty("metric_definition_id")
    public String metricDefinitionId;

    @Schema(
            type = SchemaType.NUMBER,
            implementation = Double.class,
            description = "The currently effective rate for this metric.",
            example = "0.5"
    )
    public Double rate;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The date from which this rate has been effective.",
            example = "2026-08-01T00:00:00Z"
    )
    @JsonProperty("valid_from")
    public Instant validFrom;
}