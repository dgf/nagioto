package de.g2d.nagioto;

import android.test.InstrumentationTestCase;
import android.util.Log;

import de.g2d.nagioto.domain.HostResponse;
import de.g2d.nagioto.domain.IcingaMapper;

public class HostResponseTest extends InstrumentationTestCase {

    public void testMapperHostResponse() throws Exception {

        String data = Utils.readFromFile(getInstrumentation().getContext(), "host_status.json");
        Log.d("HostResponseTest", data);

        IcingaMapper icingaMapper = new IcingaMapper();
        HostResponse cgiResponse = icingaMapper.mapHost(data);
        assertEquals("1.7.1", cgiResponse.version);

        assertTrue(cgiResponse.status.servers.size() > 0);

    }
}
