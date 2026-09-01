package org.grnet.creditmanagement.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.UriInfo;
import org.bson.types.ObjectId;
import org.grnet.creditmanagement.dtos.CreditAllocationRequestDto;
import org.grnet.creditmanagement.dtos.CreditAllocationResponseDto;
import org.grnet.creditmanagement.dtos.CreditAllocationUpdateRequestDto;
import org.grnet.creditmanagement.entities.CreditAllocationEntity;
import org.grnet.creditmanagement.exceptions.CreditAllocationOverlapException;
import org.grnet.creditmanagement.pagination.PageResource;
import org.grnet.creditmanagement.repositories.CreditAllocationRepository;
import org.grnet.creditmanagement.repositories.ExternalEntityLookupRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@ApplicationScoped
public class CreditAllocationService {

    @Inject
    CreditAllocationRepository creditAllocationRepository;

    @Inject
    ExternalEntityLookupRepository externalEntityLookupRepository;

    public CreditAllocationResponseDto createAllocation(String projectId,
                                                        String groupId,
                                                        CreditAllocationRequestDto request) {

        var validFrom = request.validFrom.truncatedTo(ChronoUnit.DAYS);
        var validTo = request.validTo.truncatedTo(ChronoUnit.DAYS);

        if (!validFrom.isBefore(validTo)) {
            throw new BadRequestException("valid_from must be strictly earlier than valid_to.");
        }

        if (request.totalCredits <= 0) {
            throw new BadRequestException("total_credits must be greater than zero.");
        }

        if (!externalEntityLookupRepository.projectExists(projectId)) {
            throw new NotFoundException("Project not found: " + projectId);
        }

        if (creditAllocationRepository.existsOverlapping(projectId, groupId, validFrom, validTo)) {
            throw new CreditAllocationOverlapException(
                    "The requested period [" + validFrom + ", " + validTo + ") overlaps with an " +
                            "existing Credit Allocation for project " + projectId + " and group " + groupId + ".");
        }

        var entity = new CreditAllocationEntity();
        entity.setId(new ObjectId().toString());
        entity.setProjectId(projectId);
        entity.setGroupId(groupId);
        entity.setTotalCredits(request.totalCredits);
        entity.setValidFrom(validFrom);
        entity.setValidTo(validTo);

        creditAllocationRepository.persist(entity);

        return toResponseDto(entity);
    }

    public CreditAllocationResponseDto getEffectiveAllocation(String projectId, String groupId, Instant at) {

        if (!externalEntityLookupRepository.projectExists(projectId)) {
            throw new NotFoundException("Project not found: " + projectId);
        }

        var entity = creditAllocationRepository.findEffectiveAt(projectId, groupId, at)
                .orElseThrow(() -> new NotFoundException(
                        "There is no Credit Allocation effective at " + at + " for project " + projectId +
                                " and group " + groupId + "."));

        return toResponseDto(entity);
    }

    public CreditAllocationResponseDto getCurrentAllocation(String projectId, String groupId) {

        return getEffectiveAllocation(projectId, groupId, Instant.now());
    }

    private CreditAllocationResponseDto toResponseDto(CreditAllocationEntity entity) {

        var response = new CreditAllocationResponseDto();
        response.id = entity.getId();
        response.projectId = entity.getProjectId();
        response.groupId = entity.getGroupId();
        response.totalCredits = entity.getTotalCredits();
        response.validFrom = entity.getValidFrom();
        response.validTo = entity.getValidTo();
        return response;
    }

    public PageResource<CreditAllocationResponseDto> getAllocationHistory(String projectId, String groupId, int page, int size, UriInfo uriInfo) {

        if (!externalEntityLookupRepository.projectExists(projectId)) {
            throw new NotFoundException("Project not found: " + projectId);
        }

        var entries = creditAllocationRepository.findByProjectAndGroupPaged(projectId, groupId, page, size);

        var content = entries
                .stream()
                .map(this::toResponseDto)
                .toList();

        return new PageResource<>(entries, content, uriInfo);
    }

    public CreditAllocationResponseDto updateAllocation(String projectId,
                                                        String groupId,
                                                        String allocationId,
                                                        CreditAllocationUpdateRequestDto request) {

        if (!externalEntityLookupRepository.projectExists(projectId)) {
            throw new NotFoundException("Project not found: " + projectId);
        }

        var entity = creditAllocationRepository.find("id = ?1 and projectId = ?2 and groupId = ?3",
                        allocationId, projectId, groupId)
                .firstResultOptional()
                .orElseThrow(() -> new NotFoundException(
                        "There is no Credit Allocation with id " + allocationId +
                                " for project " + projectId + " and group " + groupId + "."));

        var newValidFrom = request.validFrom != null
                ? request.validFrom.truncatedTo(ChronoUnit.DAYS)
                : entity.getValidFrom();

        var newValidTo = request.validTo != null
                ? request.validTo.truncatedTo(ChronoUnit.DAYS)
                : entity.getValidTo();

        var newTotalCredits = request.totalCredits != null
                ? request.totalCredits
                : entity.getTotalCredits();

        if (!newValidFrom.isBefore(newValidTo)) {
            throw new BadRequestException("valid_from must be strictly earlier than valid_to.");
        }

        if (newTotalCredits < 0) {
            throw new BadRequestException("total_credits must be greater than zero.");
        }

        if (creditAllocationRepository.existsOverlappingExcludingId(projectId, groupId, newValidFrom, newValidTo, allocationId)) {
            throw new CreditAllocationOverlapException(
                    "The requested period [" + newValidFrom + ", " + newValidTo + ") overlaps with an " +
                            "existing Credit Allocation for project " + projectId + " and group " + groupId + ".");
        }

        entity.setValidFrom(newValidFrom);
        entity.setValidTo(newValidTo);
        entity.setTotalCredits(newTotalCredits);

        creditAllocationRepository.update(entity);

        return toResponseDto(entity);
    }
}