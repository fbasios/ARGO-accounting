package org.grnet.creditmanagement.repositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
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
import java.util.Optional;
import java.util.regex.Pattern;

@ApplicationScoped
public class ExternalEntityLookupRepository {

    @Inject
    MongoClient mongoClient;

    @ConfigProperty(name = "quarkus.mongodb.database")
    String database;

    public static final String PATH_SEPARATOR = ".";


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

    /**
     * Whether Credit Management is enabled for the given project, per the
     * project's credit_management_enabled flag. Defaults to false if the
     * field is absent (e.g. for projects created before this flag existed)
     * or if the project itself doesn't exist.
     */
    public boolean isCreditManagementEnabled(String projectId) {

        var project = getProjectCollection()
                .find(Filters.eq("_id", projectId))
                .projection(Projections.include("credit_management_enabled"))
                .first();

        if (project == null) {
            return false;
        }

        return project.getBoolean("credit_management_enabled", false);
    }

    /**
     * Resolves the project_id that owns the given installation, via the
     * HierarchicalRelation lookup (id = projectId/providerId/installationId).
     * Used by endpoints whose path only carries installation_id, not
     * project_id directly.
     */
    public Optional<String> resolveProjectIdForInstallation(String installationId) {

        var relation = getHierarchicalRelationCollection()
                .find(Filters.eq("externalId", installationId))
                .first();

        if (relation == null) {
            return Optional.empty();
        }

        var id = relation.getString("_id");
        var parts = id.split(Pattern.quote(PATH_SEPARATOR));

        if (parts.length < 1) {
            return Optional.empty();
        }

        return Optional.of(parts[0]);
    }

    /**
     * Sets the credit_management_enabled flag on the given project. Returns
     * false if the project does not exist (no document matched), true if the
     * update was applied.
     */
    public boolean setCreditManagementEnabled(String projectId, boolean enabled) {

        var result = getProjectCollection().updateOne(
                Filters.eq("_id", projectId),
                Updates.set("credit_management_enabled", enabled)
        );

        return result.getMatchedCount() > 0;
    }

    /**
     * Fetches the display name of an Installation ("installation" field on
     * the nested Project.providers.installations subdocument), by its id.
     * Returns null if not found.
     */
    public String fetchInstallationName(String installationId) {

        var matchInstallation = Aggregates.match(Filters.eq("providers.installations._id", installationId));

        var stages = List.of(
                Aggregates.unwind("$providers"),
                Aggregates.unwind("$providers.installations"),
                matchInstallation,
                Aggregates.project(Projections.fields(
                        Projections.excludeId(),
                        Projections.computed("name", "$providers.installations.installation")
                ))
        );

        var result = getProjectCollection().aggregate(stages).first();

        return result == null ? null : result.getString("name");
    }

    /**
     * Fetches the display name of a Metric Definition ("metric_name" field),
     * by its id. Returns null if not found or the id is not a valid ObjectId.
     */
    public String fetchMetricDefinitionName(String metricDefinitionId) {

        if (!ObjectId.isValid(metricDefinitionId)) {
            return null;
        }

        var result = getMetricDefinitionCollection()
                .find(Filters.eq("_id", new ObjectId(metricDefinitionId)))
                .projection(Projections.include("metric_name"))
                .first();

        return result == null ? null : result.getString("metric_name");
    }
}
