package org.grnet.creditmanagement.repositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.grnet.creditmanagement.usage.InstallationRef;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Returns the (installation_id, provider_id) pairs for every Installation
     * under the given project, optionally restricted to a single
     * installation_id. Returns an empty list if the project has no
     * installations, or if installationIdFilter doesn't match any
     * installation under this project.
     */
    public List<InstallationRef> listInstallationsUnderProject(String projectId, String installationIdFilter) {

        var stages = new ArrayList<Bson>();
        stages.add(Aggregates.match(Filters.eq("_id", projectId)));
        stages.add(Aggregates.unwind("$providers"));
        stages.add(Aggregates.unwind("$providers.installations"));

        if (installationIdFilter != null) {
            stages.add(Aggregates.match(Filters.eq("providers.installations._id", installationIdFilter)));
        }

        stages.add(Aggregates.project(Projections.fields(
                Projections.excludeId(),
                Projections.computed("installation_id", "$providers.installations._id"),
                Projections.computed("provider_id", "$providers._id")
        )));

        return getProjectCollection()
                .aggregate(stages)
                .into(new ArrayList<Document>())
                .stream()
                .map(doc -> new InstallationRef(doc.getString("installation_id"), doc.getString("provider_id")))
                .toList();
    }

    public boolean metricDefinitionExists(String metricDefinitionId) {

        if (!ObjectId.isValid(metricDefinitionId)) {
            throw new BadRequestException("Invalid Metric Definition id: " + metricDefinitionId);
        }

        return getMetricDefinitionCollection()
                .find(Filters.eq("_id", new ObjectId(metricDefinitionId)))
                .first() != null;
    }

    public boolean projectExists(String projectId) {

        return getProjectCollection()
                .find(Filters.eq("_id", projectId))
                .first() != null;
    }

    private MongoCollection<Document> getProjectCollection() {
        return mongoClient.getDatabase(database).getCollection("Project");
    }

    private MongoCollection<Document> getHierarchicalRelationCollection() {
        return mongoClient.getDatabase(database).getCollection("HierarchicalRelation");
    }

    private MongoCollection<Document> getMetricDefinitionCollection() {
        return mongoClient.getDatabase(database).getCollection("MetricDefinition");
    }
}
