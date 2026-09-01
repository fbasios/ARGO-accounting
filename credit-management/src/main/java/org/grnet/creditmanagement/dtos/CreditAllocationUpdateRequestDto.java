package org.grnet.creditmanagement.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Positive;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;

public class CreditAllocationUpdateRequestDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The new start of the allocation period (inclusive). Omit to leave unchanged.",
            example = "2026-08-01T00:00:00Z"
    )
    @JsonProperty("valid_from")
    public Instant validFrom;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The new end of the allocation period (exclusive). Omit to leave unchanged.",
            example = "2026-09-01T00:00:00Z"
    )
    @JsonProperty("valid_to")
    public Instant validTo;

    @Positive(message = "total_credits must be greater than zero.")
    @Schema(
            type = SchemaType.NUMBER,
            implementation = Double.class,
            description = "The new total credits for this allocation. Omit to leave unchanged.",
            example = "1000"
    )
    @JsonProperty("total_credits")
    public Double totalCredits;
}
