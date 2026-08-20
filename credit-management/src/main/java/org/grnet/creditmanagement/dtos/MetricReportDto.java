package org.grnet.creditmanagement.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@JsonPropertyOrder({ "metric_definition_id", "total_value", "total_credits", "segments" })
public class MetricReportDto {

    @Schema(type = SchemaType.STRING, description = "The metric definition id.", example = "6a46466990abcf66c50422a6")
    @JsonProperty("metric_definition_id")
    public String metricDefinitionId;

    @Schema(description = "The window broken down into sub-periods, one per pricing rate change (including gaps with no policy in effect).")
    public List<SegmentDto> segments;

    @Schema(type = SchemaType.NUMBER, description = "The total consumed metric value across all segments.", example = "215.0")
    @JsonProperty("total_value")
    public double totalValue;

    @Schema(type = SchemaType.NUMBER, description = "The total credits across all segments.", example = "136.0")
    @JsonProperty("total_credits")
    public double totalCredits;
}
