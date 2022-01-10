package org.accounting.system.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(name="MetricsPagination", description="An object represents the stored Virtual Access Metrics.")
public class MetricsPaginationDto {

    @JsonProperty("total")
    public long total;

    @JsonProperty("pages")
    public int pages;

    @JsonProperty("page")
    public int page;

    @JsonProperty("size")
    public int size;

    @JsonProperty("metrics")
    public List<MetricResponseDto> metrics;



}
