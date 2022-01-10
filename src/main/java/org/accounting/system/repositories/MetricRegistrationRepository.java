package org.accounting.system.repositories;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.accounting.system.entities.MetricRegistration;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MetricRegistrationRepository implements PanacheMongoRepository<MetricRegistration> {
}
