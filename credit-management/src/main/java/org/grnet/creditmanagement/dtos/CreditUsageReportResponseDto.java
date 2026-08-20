package org.grnet.creditmanagement.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@JsonPropertyOrder({ "project_id", "from", "to", "filters", "installations" })
public class CreditUsageReportResponseDto {

    @Schema(type = SchemaType.STRING, description = "The project id.", example = "707f1f77bcf86cd799439011")
    @JsonProperty("project_id")
    public String projectId;

    @Schema(type = SchemaType.STRING, description = "The resolved start instant of the reporting window (inclusive) — start of day UTC of the 'from' date.", example = "2026-07-01T00:00:00Z")
    public Instant from;

    @Schema(type = SchemaType.STRING, description = "The resolved end instant of the reporting window (exclusive) — start of day UTC of the day after the 'to' date.", example = "2026-08-01T00:00:00Z")
    public Instant to;

    @Schema(description = "The filters that were applied to this report.")
    public ReportFiltersDto filters;

    @Schema(description = "One entry per Installation under the project that has at least one rated or reported Metric Definition within the window.")
    public List<InstallationReportDto> installations;
}
