package de.g2d.nagioto.domain;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class IcingaMapper {

    public Cgi2Response map(String jsonData) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonData, Cgi2Response.class);
    }

}