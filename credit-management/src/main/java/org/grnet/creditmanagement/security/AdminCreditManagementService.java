package org.grnet.creditmanagement.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.grnet.creditmanagement.dtos.CreditManagementToggleRequestDto;
import org.grnet.creditmanagement.dtos.CreditManagementToggleResponseDto;
import org.grnet.creditmanagement.repositories.ExternalEntityLookupRepository;

@ApplicationScoped
public class AdminCreditManagementService {

    @Inject
    ExternalEntityLookupRepository externalEntityLookupRepository;

    public CreditManagementToggleResponseDto setEnabled(String projectId, CreditManagementToggleRequestDto request) {

        var updated = externalEntityLookupRepository.setCreditManagementEnabled(projectId, request.enabled);

        if (!updated) {
            throw new NotFoundException("Project not found: " + projectId);
        }

        var response = new CreditManagementToggleResponseDto();
        response.projectId = projectId;
        response.creditManagementEnabled = request.enabled;

        return response;
    }
}