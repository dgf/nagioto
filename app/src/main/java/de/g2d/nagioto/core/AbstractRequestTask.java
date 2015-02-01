package de.g2d.nagioto.core;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.InputStream;

import de.g2d.nagioto.UiCallback;
import de.g2d.nagioto.Utils;
import de.g2d.nagioto.domain.Settings;

public abstract class AbstractRequestTask<T> extends AsyncTask<Settings, Void, T> {

    protected UiCallback errorCallback;

    private int timeoutConnection = 3000;
    private int timeoutSocket = 5000;

    @Override
    protected T doInBackground(Settings... params) {
        Settings settings = params[0];
        InputStream inputStream;
        T cgiResponse = null;
        String data;
        try {
            inputStream = requestServer(settings);
            data = Utils.slurp(inputStream, 1024);
            cgiResponse = map(data);
        } catch (AuthenticationException e) {
            errorCallback.onError(e);
            cancel(true);
        } catch (IOException e) {
            errorCallback.onError(e);
            cancel(true);
        }
        return cgiResponse;
    }

    public java.io.InputStream requestServer(Settings settings) throws IOException, AuthenticationException {
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(settings.getUsername(), settings.getPassword());
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        DefaultHttpClient client = new DefaultHttpClient(httpParameters);
        HttpGet get = new HttpGet(settings.getUrl() + getQuery());
        get.addHeader(new BasicScheme().authenticate(creds, get));
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        return entity.getContent();
    }

    protected abstract String getQuery();

    protected abstract T map(String json) throws IOException;
}
