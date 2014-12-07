package de.g2d.nagioto.domain;

import org.codehaus.jackson.annotate.JsonProperty;

public class ServiceResponse {

    @JsonProperty("cgi_json_version")
    public String version;

    @JsonProperty
    public ServiceStatus status;

}
