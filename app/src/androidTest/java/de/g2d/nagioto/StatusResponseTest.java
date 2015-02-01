package de.g2d.nagioto;

import android.test.InstrumentationTestCase;

import de.g2d.nagioto.domain.HostResponse;
import de.g2d.nagioto.domain.IcingaMapper;
import de.g2d.nagioto.domain.ServiceResponse;
import de.g2d.nagioto.domain.Status;

public class StatusResponseTest extends InstrumentationTestCase {

    private IcingaMapper icingaMapper = new IcingaMapper();

    public void testMapperHostResponse() throws Exception {
        String hostData = Utils.readFromFile(getInstrumentation().getContext(), "host_status.json");
        HostResponse hostResponse = icingaMapper.mapHost(hostData);
        assertEquals("1.7.1", hostResponse.version);
        assertEquals(2, hostResponse.status.servers.size());
        assertEquals("host01", hostResponse.status.servers.get(0).name);
    }

    public void testMapperServiceResponse() throws Exception {
        String serviceData = Utils.readFromFile(getInstrumentation().getContext(), "service_status.json");
        ServiceResponse serviceResponse = icingaMapper.mapService(serviceData);
        assertEquals("1.7.1", serviceResponse.version);
        assertEquals(3, serviceResponse.status.services.size());
        assertEquals("www01", serviceResponse.status.services.get(0).service);
    }

    public void testMapperAlertResponse() throws Exception {
        String alertData = Utils.readFromFile(getInstrumentation().getContext(), "alert_status.json");
        ServiceResponse serviceResponse = icingaMapper.mapService(alertData);
        assertEquals("1.7.1", serviceResponse.version);
        assertEquals(2, serviceResponse.status.services.size());
        assertEquals("CRITICAL", serviceResponse.status.services.get(0).status);
    }

    public void testMapperStatusResponse() throws Exception {
        String hostData = Utils.readFromFile(getInstrumentation().getContext(), "host_status.json");
        String serviceData = Utils.readFromFile(getInstrumentation().getContext(), "service_status.json");
        IcingaMapper icingaMapper = new IcingaMapper();
        HostResponse hostResponse = icingaMapper.mapHost(hostData);
        ServiceResponse serviceResponse = icingaMapper.mapService(serviceData);
        Status status = icingaMapper.mapStatus(hostResponse, serviceResponse);
        assertEquals("1.7.1", status.version);
        assertEquals(2, status.hosts.size());
        assertEquals("host01", status.hosts.get(0).name);
        assertEquals(2, status.hosts.get(0).services.size());
        assertEquals("www01", status.hosts.get(0).services.get(0).service);
    }

}
