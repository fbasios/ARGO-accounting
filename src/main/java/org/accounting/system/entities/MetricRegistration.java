package org.accounting.system.entities;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
public class MetricRegistration {

    private ObjectId id;
    private String metricName;
    private String metricDescription;
    private String unitType;
    private String metricType;

}
