package org.grnet.creditmanagement.repositories;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import org.grnet.creditmanagement.entities.CreditAllocationEntity;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class CreditAllocationRepository implements PanacheMongoRepositoryBase<CreditAllocationEntity, String> {

    /**
     * Returns true if any stored allocation for the given project_id and
     * group_id overlaps with the given [validFrom, validTo) period.
     * Standard half-open interval overlap test: two intervals
     * [aStart, aEnd) and [bStart, bEnd) overlap iff aStart < bEnd and
     * bStart < aEnd.
     */
    public boolean existsOverlapping(String projectId, String groupId, Instant validFrom, Instant validTo) {

        return find("projectId = ?1 and groupId = ?2 and validFrom < ?3 and validTo > ?4",
                projectId, groupId, validTo, validFrom)
                .firstResultOptional()
                .isPresent();
    }

    public List<CreditAllocationEntity> findByProjectAndGroup(String projectId, String groupId) {

        return find("projectId = ?1 and groupId = ?2", Sort.by("validFrom"), projectId, groupId)
                .list();
    }
}
