package org.grnet.creditmanagement.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.bson.types.ObjectId;
import org.grnet.creditmanagement.dtos.CurrentRatingPolicyEntryDto;
import org.grnet.creditmanagement.dtos.RatingPolicyRequestDto;
import org.grnet.creditmanagement.dtos.RatingPolicyResponseDto;
import org.grnet.creditmanagement.entities.RatingPolicyEntity;
import org.grnet.creditmanagement.exceptions.RatingPolicyBeforeEarliestException;
import org.grnet.creditmanagement.exceptions.RatingPolicyConflictException;
import org.grnet.creditmanagement.repositories.ExternalEntityLookupRepository;
import org.grnet.creditmanagement.repositories.RatingPolicyRepository;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class RatingPolicyService {

    @Inject
    RatingPolicyRepository ratingPolicyRepository;

    @Inject
    ExternalEntityLookupRepository externalEntityLookupRepository;


    public RatingPolicyResponseDto createRatingPolicy(String installationId,
                                                      String metricDefinitionId,
                                                      RatingPolicyRequestDto request) {


        externalEntityLookupRepository.assertInstallationExists(installationId);


        if (!externalEntityLookupRepository.metricDefinitionExists(metricDefinitionId)) {
            throw new NotFoundException("Metric Definition not found: " + metricDefinitionId);
        }

        if (ratingPolicyRepository.existsByInstallationMetricAndValidFrom(installationId, metricDefinitionId, request.validFrom)) {
            throw new RatingPolicyConflictException(
                    "A rating policy already exists for this installation and metric with valid_from = " + request.validFrom);
        }

        ratingPolicyRepository.findEarliestValidFrom(installationId, metricDefinitionId)
                .filter(earliest -> request.validFrom.isBefore(earliest))
                .ifPresent(earliest -> {
                    throw new RatingPolicyBeforeEarliestException(
                            "valid_from (" + request.validFrom + ") cannot be earlier than the earliest " +
                                    "already recorded valid_from (" + earliest + ") for this installation and metric.");
                });

        var entity = new RatingPolicyEntity();
        entity.setId(new ObjectId().toString());
        entity.setInstallationId(installationId);
        entity.setMetricDefinitionId(metricDefinitionId);
        entity.setValidFrom(request.validFrom);
        entity.setRate(request.rate);

        ratingPolicyRepository.persist(entity);

        var response = new RatingPolicyResponseDto();
        response.id = entity.getId();
        response.installationId = entity.getInstallationId();
        response.metricDefinitionId = entity.getMetricDefinitionId();
        response.validFrom = entity.getValidFrom();
        response.rate = entity.getRate();

        return response;
    }

    public List<CurrentRatingPolicyEntryDto> getCurrentRatingPolicies(String installationId) {

        externalEntityLookupRepository.assertInstallationExists(installationId);

        var currentEntries = ratingPolicyRepository.findCurrentEffectiveRates(installationId, Instant.now());

        return currentEntries.stream()
                .map(entity -> {
                    var dto = new CurrentRatingPolicyEntryDto();
                    dto.metricDefinitionId = entity.getMetricDefinitionId();
                    dto.rate = entity.getRate();
                    dto.validFrom = entity.getValidFrom();
                    return dto;
                })
                .toList();
    }
}
