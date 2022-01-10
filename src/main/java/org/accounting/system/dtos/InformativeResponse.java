package org.accounting.system.dtos;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="Response", description="Illustrates if an API operation is successful or not.")
public class InformativeResponse {

    @Schema(
            type = SchemaType.STRING,
            implementation = Integer.class,
            description = "A code that indicates whether a specific request has been completed."
    )
    public int code;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "A message that informs whether a specific request has been completed."
    )
    public String message;

}
