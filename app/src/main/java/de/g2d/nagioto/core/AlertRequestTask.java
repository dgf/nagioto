package de.g2d.nagioto.core;

import android.content.Context;
import android.os.AsyncTask;

import de.g2d.nagioto.UiCallback;
import de.g2d.nagioto.domain.ServiceResponse;
import de.g2d.nagioto.domain.Settings;

/**
 * Created by sasse_h on 07.12.14.
 */
public class AlertRequestTask extends AsyncTask<Settings, Void, ServiceResponse> {
    public final static String QUERY = "status.cgi?jsonoutput&alerttypes=3";
    private static final String TAG = AlertRequestTask.class.getSimpleName();
    private UiCallback callback;
    private Context context;

    @Override
    protected ServiceResponse doInBackground(Settings... params) {
        return null;
    }
}
