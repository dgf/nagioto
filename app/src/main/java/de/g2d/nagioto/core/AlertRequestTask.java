package de.g2d.nagioto.core;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
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

public class AlertRequestTask implements Runnable {

    public final static String QUERY = "status.cgi?jsonoutput&style=detail&servicestatustypes=28";
    private static final String TAG = AlertRequestTask.class.getSimpleName();
    private Handler handler;
    private Settings settings;
    private Context context;
    private boolean running = true;

    public AlertRequestTask(Context context, Handler handler, Settings settings) {
        this.context = context;
        this.handler = handler;
        this.settings = settings;
    }

    @Override
    public void run() {
        while(running) {
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
                IcingaMapper icingaMapper = new IcingaMapper();
                cgiResponse = icingaMapper.mapService(data);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            Message msg = new Message();
            msg.obj = cgiResponse;
            handler.sendMessage(msg);
            try {
                Thread.sleep(1000 * settings.getSeconds());
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
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