package org.grnet.creditmanagement.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;

public class RatingPolicyResponseDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The unique identifier of the Rating Policy entry.",
            example = "64f1a2b3c4d5e6f7a8b9c0d1"
    )
    public String id;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The installation id.",
            example = "707f1f77bcf86cd799439013"
    )
    @JsonProperty("installation_id")
    public String installationId;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The metric definition id.",
            example = "707f1f77bcf86cd799439014"
    )
    @JsonProperty("metric_definition_id")
    public String metricDefinitionId;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The date from which this rate is effective.",
            example = "2026-08-01T00:00:00Z"
    )
    @JsonProperty("valid_from")
    public Instant validFrom;

    @Schema(
            type = SchemaType.NUMBER,
            implementation = Double.class,
            description = "The rate applied to the metric from valid_from onward.",
            example = "0.5"
    )
    public Double rate;
}
