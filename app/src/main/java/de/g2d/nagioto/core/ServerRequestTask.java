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

import de.g2d.nagioto.StatusCallback;
import de.g2d.nagioto.Utils;
import de.g2d.nagioto.domain.HostResponse;
import de.g2d.nagioto.domain.IcingaMapper;
import de.g2d.nagioto.domain.Settings;

public class ServerRequestTask extends AsyncTask<Settings, Void, HostResponse> {
    public final static String QUERY = "status.cgi?jsonoutput&style=hostdetail";
    private static final String TAG = ServerRequestTask.class.getSimpleName();
    private StatusCallback callback;
    private Context context;

    public ServerRequestTask(Context context, StatusCallback callback) {
        this.context = context;
        Log.d(TAG, "create task");
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(HostResponse cgiResponse) {
        Log.d(TAG, "post execute");
        callback.onServerList(cgiResponse.status.servers);
    }

    @Override
    protected HostResponse doInBackground(Settings... params) {
        Settings settings = params[0];
        Log.d(TAG, "run baby");
        InputStream inputStream;
        String data;
        try {
            inputStream = requestServer(settings);
            data = Utils.slurp(inputStream, 1024);
            Log.d("RequestTask", "write JSON data " + data);
            Utils.writeToFile(context, "host.json", data);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        HostResponse cgiResponse;
        try {
            IcingaMapper mapper = new IcingaMapper();
            cgiResponse = mapper.mapHost(data);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Log.i("NAGIOTO", " success response count: " + cgiResponse);

        return cgiResponse;
    }

    public java.io.InputStream requestServer(Settings settings) throws IOException, AuthenticationException {
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(settings.getUsername(), settings.getPassword());
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(settings.getUrl() + QUERY);
        get.addHeader(new BasicScheme().authenticate(creds, get));
        Log.d("RequestTask", "request status CGI");
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();

        Log.d(TAG, "response entity " + entity);
        return entity.getContent();
    }

}
