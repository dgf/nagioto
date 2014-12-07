package de.g2d.nagioto;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import de.g2d.nagioto.domain.Cgi2Response;
import de.g2d.nagioto.domain.IcingaMapper;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    private static final String TAG = ApplicationTest.class.getSimpleName();

    public ApplicationTest() {
        super(Application.class);
    }


    public void testMapper() throws Exception {

        // read saved json data from private app directory
        String data = Utils.readFromFile(getContext());
        Log.d(TAG, data);

        IcingaMapper icingaMapper = new IcingaMapper();
        Cgi2Response cgiResponse = icingaMapper.map(data);
        assertEquals("1.7.1", cgiResponse.version);

        assertTrue(cgiResponse.status.servers.size() > 0);

    }
}