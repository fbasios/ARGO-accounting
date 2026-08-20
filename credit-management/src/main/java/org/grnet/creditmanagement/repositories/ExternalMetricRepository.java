package org.grnet.creditmanagement.repositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.grnet.creditmanagement.usage.MetricEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ExternalMetricRepository {

    @Inject
    MongoClient mongoClient;

    @ConfigProperty(name = "quarkus.mongodb.database")
    String database;

    public List<String> findDistinctMetricDefinitionIds(String installationId,
                                                        Instant from,
                                                        Instant to,
                                                        String metricDefinitionIdFilter,
                                                        String userId,
                                                        String groupId) {

        var filters = buildOverlapFilters(installationId, from, to, metricDefinitionIdFilter, userId, groupId);

        return getCollection()
                .distinct("metric_definition_id", Filters.and(filters), String.class)
                .into(new ArrayList<>());
    }

    /**
     * Returns the raw metric events for the given installation and metric
     * definition that overlap at all with [from, to), matching the optional
     * user_id/group_id filters. Each event's own time_period_start/end is
     * returned as-is (not clipped to the window) — callers are responsible
     * for prorating each event's value across whichever segment(s) it
     * overlaps.
     */
    public List<MetricEvent> findOverlappingEvents(String installationId,
                                                   String metricDefinitionId,
                                                   Instant from,
                                                   Instant to,
                                                   String userId,
                                                   String groupId) {

        var filters = buildOverlapFilters(installationId, from, to, metricDefinitionId, userId, groupId);

        var events = new ArrayList<MetricEvent>();

        for (Document doc : getCollection().find(Filters.and(filters))) {
            events.add(new MetricEvent(
                    doc.getDate("time_period_start").toInstant(),
                    doc.getDate("time_period_end").toInstant(),
                    doc.get("value", Number.class).doubleValue()
            ));
        }

        return events;
    }

    /**
     * Events "overlap" the window if they start before the window ends AND
     * end after the window starts — standard interval overlap test.
     */
    private List<Bson> buildOverlapFilters(String installationId,
                                           Instant from,
                                           Instant to,
                                           String metricDefinitionId,
                                           String userId,
                                           String groupId) {

        var filters = new ArrayList<Bson>();
        filters.add(Filters.eq("installation_id", installationId));

        if (metricDefinitionId != null) {
            filters.add(Filters.eq("metric_definition_id", metricDefinitionId));
        }

        filters.add(Filters.lt("time_period_start", to));
        filters.add(Filters.gt("time_period_end", from));

        if (userId != null) {
            filters.add(Filters.eq("user_id", userId));
        }

        if (groupId != null) {
            filters.add(Filters.eq("group_id", groupId));
        }

        return filters;
    }

    private MongoCollection<Document> getCollection() {
        return mongoClient.getDatabase(database).getCollection("Metric");
    }
}
