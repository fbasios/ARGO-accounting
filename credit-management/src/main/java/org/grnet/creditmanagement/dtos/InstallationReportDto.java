package org.grnet.creditmanagement.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@JsonPropertyOrder({ "installation_id", "provider_id", "metrics" })
public class InstallationReportDto {

    @Schema(type = SchemaType.STRING, description = "The installation id.", example = "707f1f77bcf86cd799439013")
    @JsonProperty("installation_id")
    public String installationId;

    @Schema(type = SchemaType.STRING, description = "The provider id that owns this installation.", example = "707f1f77bcf86cd799439012")
    @JsonProperty("provider_id")
    public String providerId;

    @Schema(description = "One entry per Metric Definition rated or reported on this installation within the window.")
    public List<MetricReportDto> metrics;
}
