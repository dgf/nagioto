package de.g2d.nagioto.core;

import android.os.AsyncTask;

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
import de.g2d.nagioto.domain.Settings;

public abstract class AbstractRequestTask<T> extends AsyncTask<Settings, Void, T> {

    @Override
    protected T doInBackground(Settings... params) {
        Settings settings = params[0];
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

        T cgiResponse;
        try {
            cgiResponse = map(data);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return cgiResponse;
    }


    public java.io.InputStream requestServer(Settings settings) throws IOException, AuthenticationException {
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(settings.getUsername(), settings.getPassword());
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(settings.getUrl() + getQuery());
        get.addHeader(new BasicScheme().authenticate(creds, get));
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        return entity.getContent();
    }

    protected abstract String getQuery();
    protected abstract T map(String json) throws IOException;
}