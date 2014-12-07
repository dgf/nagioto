package de.g2d.nagioto.domain;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class ServiceStatus {

    @JsonProperty("service_status")
    public List<Service> services;

}
