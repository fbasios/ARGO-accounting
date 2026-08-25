package org.grnet.creditmanagement.repositories;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import org.grnet.creditmanagement.entities.CreditAllocationEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CreditAllocationRepository implements PanacheMongoRepositoryBase<CreditAllocationEntity, String> {

    /**
     * Returns true if any stored allocation for the given project_id and
     * group_id overlaps with the given [validFrom, validTo) period.
     * Standard half-open interval overlap test: two intervals
     * [aStart, aEnd) and [bStart, bEnd) overlap if aStart < bEnd and
     * bStart < aEnd.
     */
    public boolean existsOverlapping(String projectId, String groupId, Instant validFrom, Instant validTo) {

        return find("projectId = ?1 and groupId = ?2 and validFrom < ?3 and validTo > ?4",
                projectId, groupId, validTo, validFrom)
                .firstResultOptional()
                .isPresent();
    }

    /**
     * Returns the allocation, if any, whose [valid_from, valid_to) range
     * covers the given point in time, for the given project_id/group_id.
     * Since stored ranges never overlap (enforced at write time), at most
     * one result is possible.
     */
    public Optional<CreditAllocationEntity> findEffectiveAt(String projectId, String groupId, Instant at) {

        return find("projectId = ?1 and groupId = ?2 and validFrom <= ?3 and validTo > ?3",
                projectId, groupId, at)
                .firstResultOptional();
    }

    public List<CreditAllocationEntity> findByProjectAndGroup(String projectId, String groupId) {

        return find("projectId = ?1 and groupId = ?2", Sort.by("validFrom"), projectId, groupId)
                .list();
    }

    /**
     * A page of all allocation entries for the given project_id/group_id,
     * ordered by valid_from descending (most recent first), regardless of
     * whether they are past, current, or future-dated. page is 1-based.
     */
    public PanacheQuery<CreditAllocationEntity> findByProjectAndGroupPaged(String projectId, String groupId, int page, int size) {

        return find("projectId = ?1 and groupId = ?2", Sort.by("validFrom").descending(), projectId, groupId)
                .page(page - 1, size);
    }

    /**
     * All stored allocations for the given project_id/group_id that overlap
     * at all with [from, to).
     */
    public List<CreditAllocationEntity> findOverlapping(String projectId, String groupId, Instant from, Instant to) {

        return find("projectId = ?1 and groupId = ?2 and validFrom < ?3 and validTo > ?4",
                projectId, groupId, to, from)
                .list();
    }
}
