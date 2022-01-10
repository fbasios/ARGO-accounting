package org.accounting.system.mappers;

import org.accounting.system.dtos.MetricRequestDto;
import org.accounting.system.dtos.MetricResponseDto;
import org.accounting.system.entities.Metric;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;

@Mapper(imports = Instant.class)
public interface MetricMapper {

    MetricMapper INSTANCE = Mappers.getMapper( MetricMapper.class );

    @Mapping( target="start", expression="java(Instant.parse(request.start))")
    @Mapping( target="end", expression="java(Instant.parse(request.end))")
    Metric requestToMetric(MetricRequestDto request);

    @Mapping( target="id", expression="java(metric.getId().toString())")
    MetricResponseDto metricToResponse(Metric metric);

    @Mapping( target="id", expression="java(metric.getId().toString())")
    List<MetricResponseDto> metricsToResponse(List<Metric> metrics);

}
