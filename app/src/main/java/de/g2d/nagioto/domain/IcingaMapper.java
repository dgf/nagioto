package de.g2d.nagioto.domain;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class IcingaMapper {

    public HostResponse mapHost(String jsonData) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonData, HostResponse.class);
    }

    public ServiceResponse mapService(String jsonData) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonData, ServiceResponse.class);
    }

}