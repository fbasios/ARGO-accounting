package org.accounting.system.repositories;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import org.bson.types.ObjectId;
import org.accounting.system.dtos.MetricResponseDto;
import org.accounting.system.dtos.MetricsPaginationDto;
import org.accounting.system.entities.Metric;
import org.accounting.system.mappers.MetricMapper;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MetricRepository implements PanacheMongoRepository<Metric> {

    public MetricsPaginationDto findMetricsMetricRegistrationIdPageable(String metricRegistrationId, int page, int size){

        PanacheQuery<Metric> panacheQuery = find("metricRegistrationId", Sort.by("metricRegistrationId"), metricRegistrationId);

        MetricsPaginationDto metricsPaginationDto = new MetricsPaginationDto();

        metricsPaginationDto.total = panacheQuery.count();

        metricsPaginationDto.size = size;

        metricsPaginationDto.pages = panacheQuery.page(Page.ofSize(size)).pageCount();

        metricsPaginationDto.page = page;

        List<Metric> metrics = panacheQuery.page(Page.of(page, size)).list();

//        List<Metric> metrics = panacheQuery
//                .stream()
//                .skip(page * size)
//                .limit(size)
//                .collect(Collectors.toList());

        List<MetricResponseDto> metricsDto = MetricMapper.INSTANCE.metricsToResponse(metrics);

        metricsPaginationDto.metrics = metricsDto;

        return metricsPaginationDto;


    }

    public Optional<Metric> findMetricByMetricIdAndMetricRegistrationId(String id, String metricRegistrationId){

        return find("_id = ?1 and metricRegistrationId = ?2", new ObjectId(id), metricRegistrationId).stream().findFirst();

    }

}
