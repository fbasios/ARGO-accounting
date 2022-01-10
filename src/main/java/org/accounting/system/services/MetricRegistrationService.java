package org.accounting.system.services;

import org.bson.types.ObjectId;
import org.accounting.system.dtos.MetricRegistrationDtoRequest;
import org.accounting.system.dtos.MetricRegistrationDtoResponse;
import org.accounting.system.entities.MetricRegistration;
import org.accounting.system.mappers.MetricRegistrationMapper;
import org.accounting.system.repositories.MetricRegistrationRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.Optional;

@ApplicationScoped
public class MetricRegistrationService {

    @Inject
    private MetricRegistrationRepository metricRegistrationRepository;

    public MetricRegistrationService(MetricRegistrationRepository metricRegistrationRepository) {
        this.metricRegistrationRepository = metricRegistrationRepository;
    }

    public MetricRegistrationDtoResponse save(MetricRegistrationDtoRequest request){

        MetricRegistration metricRegistration = MetricRegistrationMapper.INSTANCE.requestToMetricRegistration(request);

        metricRegistrationRepository.persist(metricRegistration);

        return MetricRegistrationMapper.INSTANCE.metricRegistrationToResponse(metricRegistration);

    }


    public MetricRegistrationDtoResponse update(String id, MetricRegistrationDtoRequest request){

        Optional<MetricRegistration> optionalMetricRegistration = metricRegistrationRepository.findByIdOptional(new ObjectId(id));

        MetricRegistration metricRegistration = optionalMetricRegistration.orElseThrow(()->new NotFoundException("The Metric Registration has not been found."));

        MetricRegistrationMapper.INSTANCE.updateMetricRegistrationFromDto(request, metricRegistration);

        metricRegistrationRepository.update(metricRegistration);

        return MetricRegistrationMapper.INSTANCE.metricRegistrationToResponse(metricRegistration);
    }



    public Optional<MetricRegistrationDtoResponse> storedMetricRegistrationToResponseDto(String id){

        Optional<MetricRegistration> optionalMetricRegistration = metricRegistrationRepository.findByIdOptional(new ObjectId(id));

        return optionalMetricRegistration.map(MetricRegistrationMapper.INSTANCE::metricRegistrationToResponse).stream().findAny();

    }

    public MetricRegistration findByIdOrThrowException(String id){

        Optional<MetricRegistration> optionalMetricRegistration = metricRegistrationRepository.findByIdOptional(new ObjectId(id));

        return optionalMetricRegistration.orElseThrow(()->new NotFoundException("There is no Metric Registration with the following id: "+id));


    }
}
