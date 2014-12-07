package de.g2d.nagioto.domain;

import android.util.Log;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IcingaMapper {

    public HostResponse mapHost(String jsonData) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonData, HostResponse.class);
    }

    public ServiceResponse mapService(String jsonData) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonData, ServiceResponse.class);
    }

    public Status mapStatus(HostResponse hosts, ServiceResponse services) throws IOException {
        HashMap<String, Host> hostMap = new HashMap<>();
        for (Host host : hosts.status.servers) {
            hostMap.put(host.name, host);
        }
        for (Service service : services.status.services) {
            Host host = hostMap.get(service.host);
            host.services.add(service);
        }
        Status status = new Status();
        status.version = hosts.version;
        status.hosts = new ArrayList(hostMap.values());
        return status;
    }
}