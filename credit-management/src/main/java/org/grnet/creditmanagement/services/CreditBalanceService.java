package org.grnet.creditmanagement.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.grnet.creditmanagement.dtos.CreditBalanceResponseDto;
import org.grnet.creditmanagement.repositories.CreditAllocationRepository;
import org.grnet.creditmanagement.repositories.ExternalEntityLookupRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@ApplicationScoped
public class CreditBalanceService {

    @Inject
    ExternalEntityLookupRepository externalEntityLookupRepository;

    @Inject
    CreditAllocationRepository creditAllocationRepository;

    @Inject
    CreditUsageReportService creditUsageReportService;

    /**
     * Balance = allocated_credits - consumed_credits for the given
     * project_id/group_id, over the calendar-date window [fromDate, toDate]
     * (both inclusive). allocated_credits is the sum of every stored
     * allocation's total_credits, prorated by how much of that allocation's
     * own period overlaps the requested window. consumed_credits is the
     * total credits accrued by this group across every installation under
     * the project during the window, computed from metric usage against
     * the Rating Policy rates in effect at the time — reusing the credit
     * usage report, whose per-installation/per-metric breakdown is also
     * included in the response for transparency.
     */
    public CreditBalanceResponseDto getBalance(String projectId, String groupId, LocalDate fromDate, LocalDate toDate) {

        if (fromDate == null || toDate == null) {
            throw new BadRequestException("Both 'from' and 'to' query parameters are required.");
        }

        if (fromDate.isAfter(toDate)) {
            throw new BadRequestException("'from' must not be after 'to'.");
        }

        var from = fromDate.atStartOfDay(ZoneOffset.UTC).toInstant();
        var to = toDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        if (!externalEntityLookupRepository.projectExists(projectId)) {
            throw new NotFoundException("Project not found: " + projectId);
        }

        var allocatedCredits = computeAllocatedCredits(projectId, groupId, from, to);

        var usageReport = creditUsageReportService.generateReport(projectId, fromDate, toDate, null, null, null, groupId);

        var consumedCredits = usageReport.installations.stream()
                .flatMap(installation -> installation.metrics.stream())
                .mapToDouble(metric -> metric.totalCredits)
                .sum();

        var response = new CreditBalanceResponseDto();
        response.projectId = projectId;
        response.groupId = groupId;
        response.from = from;
        response.to = to;
        response.allocatedCredits = allocatedCredits;
        response.consumedCredits = consumedCredits;
        response.balance = allocatedCredits - consumedCredits;
        response.installations = usageReport.installations;

        return response;
    }

    /**
     * Sums each overlapping allocation's total_credits, prorated by the
     * fraction of that allocation's own [valid_from, valid_to) period that
     * falls inside [from, to).
     */
    private double computeAllocatedCredits(String projectId, String groupId, Instant from, Instant to) {

        var allocations = creditAllocationRepository.findOverlapping(projectId, groupId, from, to);

        double sum = 0;

        for (var allocation : allocations) {

            var overlapStart = allocation.getValidFrom().isAfter(from) ? allocation.getValidFrom() : from;
            var overlapEnd = allocation.getValidTo().isBefore(to) ? allocation.getValidTo() : to;

            if (overlapStart.isBefore(overlapEnd)) {

                var allocationDurationMillis = allocation.getValidTo().toEpochMilli() - allocation.getValidFrom().toEpochMilli();

                if (allocationDurationMillis <= 0) {
                    continue;
                }

                var overlapMillis = overlapEnd.toEpochMilli() - overlapStart.toEpochMilli();
                var fraction = (double) overlapMillis / allocationDurationMillis;

                sum += allocation.getTotalCredits() * fraction;
            }
        }

        return sum;
    }
}
