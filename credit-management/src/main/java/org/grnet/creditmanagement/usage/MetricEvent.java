package org.grnet.creditmanagement.usage;

import java.time.Instant;

public record MetricEvent(Instant start, Instant end, double value) {}
