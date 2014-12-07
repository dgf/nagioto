package de.g2d.nagioto;

import android.test.InstrumentationTestCase;
import android.util.Log;

import de.g2d.nagioto.domain.HostResponse;
import de.g2d.nagioto.domain.IcingaMapper;
import de.g2d.nagioto.domain.ServiceResponse;
import de.g2d.nagioto.domain.Status;

public class StatusResponseTest extends InstrumentationTestCase {

    public void testMapperHostResponse() throws Exception {

        String hostData = Utils.readFromFile(getInstrumentation().getContext(), "host_status.json");
        String serviceData = Utils.readFromFile(getInstrumentation().getContext(), "service_status.json");
        Log.d("HostResponseTest", hostData);
        Log.d("ServiceResponseTest", serviceData);

        IcingaMapper icingaMapper = new IcingaMapper();
        HostResponse hostResponse = icingaMapper.mapHost(hostData);
        ServiceResponse serviceResponse = icingaMapper.mapService(serviceData);
        Status status = icingaMapper.mapStatus(hostResponse, serviceResponse);

        assertEquals("1.7.1", status.version);
        assertEquals(2, status.hosts.size());
        assertEquals(1, status.hosts.get(0).services.size());
    }
}
