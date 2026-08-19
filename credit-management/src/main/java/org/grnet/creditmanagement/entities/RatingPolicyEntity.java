package org.grnet.creditmanagement.entities;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.Instant;

@MongoEntity(collection = "RatingPolicy")
@Getter
@Setter
public class RatingPolicyEntity {

    @BsonId
    private String id;
    @BsonProperty("installation_id")
    private String installationId;
    @BsonProperty("metric_definition_id")
    private String metricDefinitionId;
    @BsonProperty("valid_from")
    private Instant validFrom;
    @BsonProperty("rate")
    private Double rate;
    @BsonProperty("created_on")
    private Instant createdOn = Instant.now();
}
