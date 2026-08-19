package org.grnet.creditmanagement.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;

public class RatingPolicyRequestDto {

    @NotNull(message = "valid_from may not be empty.")
    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The date from which this rate becomes effective. No end date is accepted; " +
                    "the effective end is implicitly determined by the next later valid_from entry, if any, " +
                    "for the same Installation and Metric Definition.",
            example = "2026-08-01T00:00:00Z",
            required = true
    )
    @JsonProperty("valid_from")
    public Instant validFrom;

    @NotNull(message = "rate may not be empty.")
    @PositiveOrZero(message = "rate must be zero or a positive number.")
    @Schema(
            type = SchemaType.NUMBER,
            implementation = Double.class,
            description = "The rate applied to the metric from valid_from onward.",
            example = "0.5",
            required = true
    )
    public Double rate;
}