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

    @JsonProperty("last_check")
    public String lastCheck;

    @JsonProperty
    public String duration;

    public List<Service> services = new ArrayList<Service>();

    @Override
    public String toString() {
        return "Host{" +
                "name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", lastCheck='" + lastCheck + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }
}

