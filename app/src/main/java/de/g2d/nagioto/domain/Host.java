package de.g2d.nagioto.domain;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Host {

    @JsonProperty("host")
    public String name;

    @JsonProperty
    public String status;

    public List<Service> services = new ArrayList<Service>();
}

