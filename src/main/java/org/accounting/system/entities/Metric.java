package org.accounting.system.entities;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.time.Instant;

@Getter
@Setter
public class Metric {

    private ObjectId id;
    private String resourceId;
    private String metricRegistrationId;
    private Instant start;
    private Instant end;
    private double value;


}
