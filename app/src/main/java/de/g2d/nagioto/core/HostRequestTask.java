package de.g2d.nagioto.core;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import de.g2d.nagioto.UiCallback;
import de.g2d.nagioto.api.HostRequestCallback;
import de.g2d.nagioto.domain.HostResponse;
import de.g2d.nagioto.domain.IcingaMapper;

public class HostRequestTask extends AbstractRequestTask<HostResponse> {
    public final static String QUERY = "status.cgi?jsonoutput&style=hostdetail";
    private static final String TAG = HostRequestTask.class.getSimpleName();
    private HostRequestCallback callback;
    private Context context;

    @Override
    protected String getQuery() {
        return QUERY;
    }

    @Override
    protected HostResponse map(String json) throws IOException {
        IcingaMapper mapper = new IcingaMapper();
        return mapper.mapHost(json);
    }

    public HostRequestTask(Context context, UiCallback errorCallback, HostRequestCallback callback) {
        this.context = context;
        this.errorCallback = errorCallback;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(HostResponse cgiResponse) {
        Log.d(TAG, "post execute");
        callback.onFinish(cgiResponse);
    }

}
