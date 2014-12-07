package de.g2d.nagioto.domain;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class Status {

    @JsonProperty("host_status")
    public List<Server> servers;

}
