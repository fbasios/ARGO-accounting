package org.grnet.creditmanagement.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class RatingPolicyUpdateRequestDto {

    @NotNull(message = "rate may not be empty.")
    @PositiveOrZero(message = "rate must be zero or a positive number.")
    @Schema(
            type = SchemaType.NUMBER,
            implementation = Double.class,
            description = "The new rate to apply for this Rating Policy entry.",
            example = "18",
            required = true
    )
    public Double rate;
}