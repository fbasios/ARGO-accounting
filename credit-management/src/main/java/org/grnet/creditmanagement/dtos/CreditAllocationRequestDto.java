package org.grnet.creditmanagement.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;

public class CreditAllocationRequestDto {

    @NotNull(message = "valid_from may not be empty.")
    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The start of the allocation period (inclusive).",
            example = "2026-08-01T00:00:00Z",
            required = true
    )
    @JsonProperty("valid_from")
    public Instant validFrom;

    @NotNull(message = "valid_to may not be empty.")
    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The end of the allocation period (exclusive). Must be strictly after valid_from.",
            example = "2026-09-01T00:00:00Z",
            required = true
    )
    @JsonProperty("valid_to")
    public Instant validTo;

    @NotNull(message = "total_credits may not be empty.")
    @Positive(message = "total_credits must be greater than zero.")
    @Schema(
            type = SchemaType.NUMBER,
            implementation = Double.class,
            description = "The total number of credits allocated for this period. Must be greater than zero.",
            example = "1000",
            required = true
    )
    @JsonProperty("total_credits")
    public Double totalCredits;
}
