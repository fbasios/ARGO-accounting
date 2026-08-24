package org.grnet.creditmanagement.entities;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.Instant;

@MongoEntity(collection = "CreditAllocation")
@Getter
@Setter
public class CreditAllocationEntity {

    @BsonId
    private String id;

    @BsonProperty("project_id")
    private String projectId;

    @BsonProperty("group_id")
    private String groupId;

    @BsonProperty("total_credits")
    private Double totalCredits;

    @BsonProperty("valid_from")
    private Instant validFrom;

    @BsonProperty("valid_to")
    private Instant validTo;

    @BsonProperty("created_on")
    private Instant createdOn = Instant.now();
}
