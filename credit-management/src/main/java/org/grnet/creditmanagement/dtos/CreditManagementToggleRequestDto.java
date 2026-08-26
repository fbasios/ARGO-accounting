package org.grnet.creditmanagement.dtos;

import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class CreditManagementToggleRequestDto {

    @NotNull(message = "enabled may not be empty.")
    @Schema(
            type = SchemaType.BOOLEAN,
            description = "Whether Credit Management should be enabled (true) or disabled (false) for this project.",
            example = "true",
            required = true
    )
    public Boolean enabled;
}