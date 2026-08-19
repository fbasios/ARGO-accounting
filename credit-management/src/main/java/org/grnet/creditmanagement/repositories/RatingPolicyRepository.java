package org.grnet.creditmanagement.repositories;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import org.grnet.creditmanagement.entities.RatingPolicyEntity;

import java.time.Instant;
import java.util.Optional;

@ApplicationScoped
public class RatingPolicyRepository implements PanacheMongoRepositoryBase<RatingPolicyEntity, String> {

    public boolean existsByInstallationMetricAndValidFrom(String installationId, String metricDefinitionId, Instant validFrom) {

        return find("installationId = ?1 and metricDefinitionId = ?2 and validFrom = ?3",
                installationId, metricDefinitionId, validFrom)
                .firstResultOptional()
                .isPresent();
    }

    /**
     * Returns the earliest valid_from currently recorded for this
     * installation + metric definition combination, if any entries exist.
     */
    public Optional<Instant> findEarliestValidFrom(String installationId, String metricDefinitionId) {

        return find("installationId = ?1 and metricDefinitionId = ?2", Sort.by("validFrom"),
                installationId, metricDefinitionId)
                .firstResultOptional()
                .map(RatingPolicyEntity::getValidFrom);
    }
}
