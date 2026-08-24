package org.grnet.creditmanagement.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;

@JsonPropertyOrder({ "id", "project_id", "group_id", "total_credits", "valid_from", "valid_to" })
public class CreditAllocationResponseDto {

    @Schema(type = SchemaType.STRING, description = "The unique identifier of the Credit Allocation.", example = "64f1a2b3c4d5e6f7a8b9c0d1")
    @JsonProperty("id")
    public String id;

    @Schema(type = SchemaType.STRING, description = "The project id.", example = "707f1f77bcf86cd799439011")
    @JsonProperty("project_id")
    public String projectId;

    @Schema(type = SchemaType.STRING, description = "The group id.", example = "group-42")
    @JsonProperty("group_id")
    public String groupId;

    @Schema(type = SchemaType.NUMBER, description = "The total number of credits allocated for this period.", example = "1000")
    @JsonProperty("total_credits")
    public Double totalCredits;

    @Schema(type = SchemaType.STRING, description = "The start of the allocation period (inclusive).", example = "2026-08-01T00:00:00Z")
    @JsonProperty("valid_from")
    public Instant validFrom;

    @Schema(type = SchemaType.STRING, description = "The end of the allocation period (exclusive).", example = "2026-09-01T00:00:00Z")
    @JsonProperty("valid_to")
    public Instant validTo;
}