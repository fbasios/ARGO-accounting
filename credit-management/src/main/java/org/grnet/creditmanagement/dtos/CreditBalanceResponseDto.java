package org.grnet.creditmanagement.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@JsonPropertyOrder({ "project_id", "group_id", "from", "to", "allocated_credits", "consumed_credits", "balance", "installations" })
public class CreditBalanceResponseDto {

    @Schema(type = SchemaType.STRING, description = "The project id.", example = "707f1f77bcf86cd799439011")
    @JsonProperty("project_id")
    public String projectId;

    @Schema(type = SchemaType.STRING, description = "The group id.", example = "group-42")
    @JsonProperty("group_id")
    public String groupId;

    @Schema(type = SchemaType.STRING, description = "The resolved start instant of the window (inclusive).", example = "2026-08-01T00:00:00Z")
    public Instant from;

    @Schema(type = SchemaType.STRING, description = "The resolved end instant of the window (exclusive).", example = "2026-09-01T00:00:00Z")
    public Instant to;

    @Schema(type = SchemaType.NUMBER, description = "The total_credits of every Credit Allocation for this group whose period overlaps the requested window, summed in full (not prorated).", example = "1500.0")
    @JsonProperty("allocated_credits")
    public double allocatedCredits;

    @Schema(type = SchemaType.NUMBER, description = "The total credits consumed by this group across all installations under the project during the window. Equal to the sum of total_credits across every entry in 'installations'.", example = "220.0")
    @JsonProperty("consumed_credits")
    public double consumedCredits;

    @Schema(type = SchemaType.NUMBER, description = "allocated_credits minus consumed_credits. Negative means the group has overspent its budget for this window.", example = "544.5")
    public double balance;

    @Schema(description = "The per-installation, per-metric consumption breakdown backing consumed_credits, in the same shape as the credit usage report.")
    public List<InstallationReportDto> installations;
}