package org.grnet.creditmanagement.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;

public class SegmentDto {

    @Schema(type = SchemaType.STRING, description = "The start of this sub-period (inclusive).", example = "2026-07-01T00:00:00Z")
    public Instant from;

    @Schema(type = SchemaType.STRING, description = "The end of this sub-period (exclusive).", example = "2026-07-15T00:00:00Z")
    public Instant to;

    @Schema(type = SchemaType.STRING, description = "The Rating Policy entry applicable during this sub-period, or null if no policy was in effect.", example = "64f1a2b3c4d5e6f7a8b9c0d1", nullable = true)
    @JsonProperty("pricing_policy_id")
    public String pricingPolicyId;

    @Schema(type = SchemaType.NUMBER, description = "The rate applicable during this sub-period, or null if no policy was in effect.", example = "0.5", nullable = true)
    public Double rate;

    @Schema(type = SchemaType.NUMBER, description = "The consumed metric value during this sub-period.", example = "120.0")
    @JsonProperty("total_value")
    public double totalValue;

    @Schema(type = SchemaType.NUMBER, description = "The credits for this sub-period (total_value * rate, or 0 if no policy was in effect).", example = "60.0")
    public double credits;
}
