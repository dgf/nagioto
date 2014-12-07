package de.g2d.nagioto;

import android.test.InstrumentationTestCase;
import android.util.Log;

import de.g2d.nagioto.domain.IcingaMapper;
import de.g2d.nagioto.domain.ServiceResponse;

public class ServiceResponseTest extends InstrumentationTestCase {

    public void testMapperHostResponse() throws Exception {

        String data = Utils.readFromFile(getInstrumentation().getContext(), "service_status.json");
        Log.d("ServiceResponseTest", data);

        IcingaMapper icingaMapper = new IcingaMapper();
        ServiceResponse cgiResponse = icingaMapper.mapService(data);
        assertEquals("1.7.1", cgiResponse.version);

        assertTrue(cgiResponse.status.services.size() > 0);

    }
}
