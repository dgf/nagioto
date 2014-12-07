package de.g2d.nagioto.core;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import de.g2d.nagioto.domain.IcingaMapper;
import de.g2d.nagioto.domain.ServiceResponse;

public class AlertRequestTask extends AbstractRequestTask<ServiceResponse> {
    public final static String QUERY = "status.cgi?jsonoutput&style=detail&servicestatustypes=28";
    private static final String TAG = AlertRequestTask.class.getSimpleName();
    private AlertRequestCallback callback;
    private Context context;

    @Override
    protected String getQuery() {
        return QUERY;
    }

    @Override
    protected ServiceResponse map(String json) throws IOException {
        return new IcingaMapper().mapService(json);
    }

    public interface AlertRequestCallback {
        void onFinish(ServiceResponse services);
    }

    public AlertRequestTask(Context context, AlertRequestCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(ServiceResponse cgiResponse) {
        Log.d(TAG, "post execute");
        callback.onFinish(cgiResponse);
    }
}