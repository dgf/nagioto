package de.g2d.nagioto.domain;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Service {

    @JsonProperty
    public String host;

    @JsonProperty
    public String service;

    @JsonProperty
    public String status;


    @Override
    public String toString() {
        return "Service{" +
                "host='" + host + '\'' +
                ", service='" + service + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

