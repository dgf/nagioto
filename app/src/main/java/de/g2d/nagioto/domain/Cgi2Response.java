package de.g2d.nagioto.domain;

import org.codehaus.jackson.annotate.JsonProperty;

public class Cgi2Response {

    @JsonProperty("cgi_json_version")
    public String version;

    @JsonProperty("status")
    public Status status;

}
