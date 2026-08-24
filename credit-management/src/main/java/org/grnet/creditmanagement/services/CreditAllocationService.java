package org.grnet.creditmanagement.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.bson.types.ObjectId;
import org.grnet.creditmanagement.dtos.CreditAllocationRequestDto;
import org.grnet.creditmanagement.dtos.CreditAllocationResponseDto;
import org.grnet.creditmanagement.entities.CreditAllocationEntity;
import org.grnet.creditmanagement.exceptions.CreditAllocationOverlapException;
import org.grnet.creditmanagement.repositories.CreditAllocationRepository;
import org.grnet.creditmanagement.repositories.ExternalEntityLookupRepository;

@ApplicationScoped
public class CreditAllocationService {

    @Inject
    CreditAllocationRepository creditAllocationRepository;

    @Inject
    ExternalEntityLookupRepository externalEntityLookupRepository;

    public CreditAllocationResponseDto createAllocation(String projectId,
                                                        String groupId,
                                                        CreditAllocationRequestDto request) {

        if (!request.validFrom.isBefore(request.validTo)) {
            throw new BadRequestException("valid_from must be strictly earlier than valid_to.");
        }

        if (request.totalCredits <= 0) {
            throw new BadRequestException("total_credits must be greater than zero.");
        }

        if (!externalEntityLookupRepository.projectExists(projectId)) {
            throw new NotFoundException("Project not found: " + projectId);
        }

        if (creditAllocationRepository.existsOverlapping(projectId, groupId, request.validFrom, request.validTo)) {
            throw new CreditAllocationOverlapException(
                    "The requested period [" + request.validFrom + ", " + request.validTo + ") overlaps with an " +
                            "existing Credit Allocation for project " + projectId + " and group " + groupId + ".");
        }

        var entity = new CreditAllocationEntity();
        entity.setId(new ObjectId().toString());
        entity.setProjectId(projectId);
        entity.setGroupId(groupId);
        entity.setTotalCredits(request.totalCredits);
        entity.setValidFrom(request.validFrom);
        entity.setValidTo(request.validTo);

        creditAllocationRepository.persist(entity);

        return toResponseDto(entity);
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
}