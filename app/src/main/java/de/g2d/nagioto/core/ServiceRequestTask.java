package de.g2d.nagioto.core;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

import de.g2d.nagioto.Utils;
import de.g2d.nagioto.domain.IcingaMapper;
import de.g2d.nagioto.domain.ServiceResponse;
import de.g2d.nagioto.domain.Settings;

/**
 * Created by sasse_h on 07.12.14.
 */
public class ServiceRequestTask extends AsyncTask<Settings, Void, ServiceResponse> {
    public final static String QUERY = "status.cgi?jsonoutput";
    private static final String TAG = ServiceRequestTask.class.getSimpleName();
    private ServiceRequestCallback callback;
    private Context context;

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

    @Override
    protected ServiceResponse doInBackground(Settings... params) {
        Settings settings = params[0];
        Log.d(TAG, "run baby");
        InputStream inputStream;
        String data;
        try {
            inputStream = requestServer(settings);
            data = Utils.slurp(inputStream, 1024);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        ServiceResponse cgiResponse;
        try {
            IcingaMapper mapper = new IcingaMapper();
            cgiResponse = mapper.mapService(data);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return cgiResponse;
    }


    public java.io.InputStream requestServer(Settings settings) throws IOException, AuthenticationException {
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(settings.getUsername(), settings.getPassword());
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(settings.getUrl() + QUERY);
        get.addHeader(new BasicScheme().authenticate(creds, get));
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        Log.d(TAG, "response entity " + entity);
        return entity.getContent();
    }
}
