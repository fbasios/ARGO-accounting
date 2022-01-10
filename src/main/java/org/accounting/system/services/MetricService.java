package org.accounting.system.services;

import org.accounting.system.dtos.MetricRequestDto;
import org.accounting.system.dtos.MetricResponseDto;
import org.accounting.system.dtos.MetricsPaginationDto;
import org.accounting.system.entities.Metric;
import org.accounting.system.mappers.MetricMapper;
import org.accounting.system.repositories.MetricRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class MetricService {

    @Inject
    private MetricRepository metricRepository;

    @Inject
    private MetricRegistrationService metricRegistrationService;

    public MetricService(MetricRepository metricRepository, MetricRegistrationService metricRegistrationService) {
        this.metricRepository = metricRepository;
        this.metricRegistrationService = metricRegistrationService;
    }

    public MetricResponseDto save(MetricRequestDto request, String metricRegistrationId) {

        metricRegistrationService.findByIdOrThrowException(metricRegistrationId);

        Metric metric = MetricMapper.INSTANCE.requestToMetric(request);

        metric.setMetricRegistrationId(metricRegistrationId);

        metricRepository.persist(metric);

        return MetricMapper.INSTANCE.metricToResponse(metric);

    }

    public Optional<MetricResponseDto> storedMetricToResponseDto(String metricId, String metricRegistrationId){

        Optional<Metric> optionalMetric = metricRepository.findMetricByMetricIdAndMetricRegistrationId(metricId, metricRegistrationId);

        return optionalMetric.map(MetricMapper.INSTANCE::metricToResponse).stream().findAny();

    }

    public MetricsPaginationDto storedMetricsToResponseDto(String metricRegistrationId, int page, int size){

        metricRegistrationService.findByIdOrThrowException(metricRegistrationId);

        MetricsPaginationDto metrics = metricRepository.findMetricsMetricRegistrationIdPageable(metricRegistrationId, page, size);

        return metrics;

    }






}
