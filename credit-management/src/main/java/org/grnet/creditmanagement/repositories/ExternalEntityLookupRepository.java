package org.grnet.creditmanagement.repositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class ExternalEntityLookupRepository {

    @Inject
    MongoClient mongoClient;

    @ConfigProperty(name = "quarkus.mongodb.database")
    String database;


    /**
     * Validates that an Installation with the given id exists, using the
     * same HierarchicalRelation lookup pattern used in the api module.
     * Throws NotFoundException (mapped to 404) if it does not exist.
     */
    public void assertInstallationExists(String installationId) {

        var relation = getHierarchicalRelationCollection()
                .find(Filters.eq("externalId", installationId))
                .first();

        if (relation == null) {
            throw new NotFoundException("There is no Installation with the following id: " + installationId);
        }
    }

    public boolean metricDefinitionExists(String metricDefinitionId) {

        if (!ObjectId.isValid(metricDefinitionId)) {
            throw new BadRequestException("Invalid Metric Definition id: " + metricDefinitionId);
        }

        return getMetricDefinitionCollection()
                .find(Filters.eq("_id", new ObjectId(metricDefinitionId)))
                .first() != null;
    }

    private MongoCollection<Document> getHierarchicalRelationCollection() {
        return mongoClient.getDatabase(database).getCollection("HierarchicalRelation");
    }

    private MongoCollection<Document> getMetricDefinitionCollection() {
        return mongoClient.getDatabase(database).getCollection("MetricDefinition");
    }
}
