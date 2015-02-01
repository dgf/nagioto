package de.g2d.nagioto.core;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

import de.g2d.nagioto.UiCallback;
import de.g2d.nagioto.Utils;
import de.g2d.nagioto.domain.IcingaMapper;
import de.g2d.nagioto.domain.ServiceResponse;
import de.g2d.nagioto.domain.Settings;

public class AlertRequestTask implements Runnable {

    public final static String QUERY = "status.cgi?jsonoutput&style=detail&servicestatustypes=28";
    private static final String TAG = AlertRequestTask.class.getSimpleName();
    private UiCallback errorCallback;
    private Handler handler;
    private Settings settings;
    private Context context;
    private boolean running = true;

    public AlertRequestTask(Context context, UiCallback callback, Handler handler, Settings settings) {
        this.context = context;
        errorCallback = callback;
        this.handler = handler;
        this.settings = settings;
    }

    @Override
    public void run() {
        while (running) {
            InputStream inputStream;
            String data;
            ServiceResponse cgiResponse;
            try {
                inputStream = requestServer(settings);
                data = Utils.slurp(inputStream, 1024);
                IcingaMapper icingaMapper = new IcingaMapper();
                cgiResponse = icingaMapper.mapService(data);

                Message msg = new Message();
                msg.obj = cgiResponse;
                handler.sendMessage(msg);
            } catch (AuthenticationException e) {
                errorCallback.onError(e);
            } catch (IOException e) {
                errorCallback.onError(e);
            }

            try {
                Thread.sleep(1000 * settings.getSeconds());
            } catch (InterruptedException e) {
                // no handling
            }
        }
    }


    private java.io.InputStream requestServer(Settings settings) throws IOException, AuthenticationException {
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(settings.getUsername(), settings.getPassword());
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(settings.getUrl() + QUERY);
        get.addHeader(new BasicScheme().authenticate(creds, get));
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        return entity.getContent();
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}