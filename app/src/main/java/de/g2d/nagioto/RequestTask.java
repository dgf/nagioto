package de.g2d.nagioto;

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

import de.g2d.nagioto.domain.Cgi2Response;
import de.g2d.nagioto.domain.IcingaMapper;
import de.g2d.nagioto.domain.Settings;

public class RequestTask extends AsyncTask<Settings, Void, Cgi2Response> {

    private StatusCallback onResponse;
    private Context context;

    public RequestTask(Context context, StatusCallback onResponse) {
        this.context = context;
        Log.d("RequestTask", "create task");
        this.onResponse = onResponse;
    }

    @Override
    protected void onPostExecute(Cgi2Response cgiResponse) {
        Log.d("RequestTask", "post execute");
        onResponse.onFinish(cgiResponse);
    }

    @Override
    protected Cgi2Response doInBackground(Settings... params) {
        Settings settings = params[0];
        Log.d("RequestTask", "run baby");
        InputStream inputStream;
        String data;
        try {
            inputStream = requestServer(settings);
            data = Utils.slurp(inputStream, 1024);
            Utils.writeToFile(context, data);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Cgi2Response cgiResponse;
        try {
            IcingaMapper mapper = new IcingaMapper();
            cgiResponse = mapper.map(data);
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
        HttpGet get = new HttpGet(settings.getUrl() + MainActivity.QUERY);
        get.addHeader(new BasicScheme().authenticate(creds, get));
        Log.d("RequestTask", "request status CGI");
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();

        Log.d("RequestTask", "response entity " + entity);
        return entity.getContent();
    }

}
