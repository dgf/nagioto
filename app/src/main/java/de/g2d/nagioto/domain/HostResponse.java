package de.g2d.nagioto.domain;

import org.codehaus.jackson.annotate.JsonProperty;

public class HostResponse {

    @JsonProperty("cgi_json_version")
    public String version;

    @JsonProperty
    public HostStatus status;

}
