package de.g2d.nagioto.domain;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class HostStatus {

    @JsonProperty("host_status")
    public List<Host> servers;

}
