package org.grnet.creditmanagement.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@JsonPropertyOrder({ "project_id", "credit_management_enabled" })
public class CreditManagementToggleResponseDto {

    @Schema(type = SchemaType.STRING, description = "The project id.", example = "707f1f77bcf86cd799439011")
    @JsonProperty("project_id")
    public String projectId;

    @Schema(type = SchemaType.BOOLEAN, description = "The current value of the credit_management_enabled flag for this project.", example = "true")
    @JsonProperty("credit_management_enabled")
    public boolean creditManagementEnabled;
}
