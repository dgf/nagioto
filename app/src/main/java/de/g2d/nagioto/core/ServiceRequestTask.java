package de.g2d.nagioto.core;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import de.g2d.nagioto.domain.IcingaMapper;
import de.g2d.nagioto.domain.ServiceResponse;

/**
 * Created by sasse_h on 07.12.14.
 */
public class ServiceRequestTask extends AbstractRequestTask<ServiceResponse> {
    public final static String QUERY = "status.cgi?jsonoutput";
    private static final String TAG = ServiceRequestTask.class.getSimpleName();
    private ServiceRequestCallback callback;
    private Context context;

    @Override
    protected String getQuery() {
        return QUERY;
    }

    @Override
    protected ServiceResponse map(String json) throws IOException {
        return new IcingaMapper().mapService(json);
    }

    public interface ServiceRequestCallback {
        void onFinish(ServiceResponse services);
    }

    public ServiceRequestTask(Context context, ServiceRequestCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(ServiceResponse cgiResponse) {
        Log.d(TAG, "post execute");
        callback.onFinish(cgiResponse);
    }

}
