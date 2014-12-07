package de.g2d.nagioto.domain;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Host {

    @JsonProperty
    public String host;

    @JsonProperty
    public String status;

}

