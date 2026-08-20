package org.grnet.creditmanagement.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class ReportFiltersDto {

    @Schema(type = SchemaType.STRING, description = "The installation_id filter applied, if any.", nullable = true)
    @JsonProperty("installation_id")
    public String installationId;

    @Schema(type = SchemaType.STRING, description = "The metric_definition_id filter applied, if any.", nullable = true)
    @JsonProperty("metric_definition_id")
    public String metricDefinitionId;

    @Schema(type = SchemaType.STRING, description = "The user_id filter applied, if any.", nullable = true)
    @JsonProperty("user_id")
    public String userId;

    @Schema(type = SchemaType.STRING, description = "The group_id filter applied, if any.", nullable = true)
    @JsonProperty("group_id")
    public String groupId;
}
