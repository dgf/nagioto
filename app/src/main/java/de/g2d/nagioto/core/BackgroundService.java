package de.g2d.nagioto.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.g2d.nagioto.MainActivity;
import de.g2d.nagioto.R;
import de.g2d.nagioto.UiCallback;
import de.g2d.nagioto.domain.HostResponse;
import de.g2d.nagioto.domain.IcingaMapper;
import de.g2d.nagioto.domain.ServiceResponse;
import de.g2d.nagioto.domain.Settings;
import de.g2d.nagioto.domain.Status;

/**
 * Created by sasse_h on 07.12.14.
 */
public class BackgroundService extends Service {
    private IBinder binder = new BackgroundServiceBinder();
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notification;
    private Handler notify;
    private Thread alertRunner;
    private AlertRequestTask task;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        task.setRunning(false);
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(BackgroundService.this).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setAutoCancel(true)
                .setOngoing(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT), PendingIntent.FLAG_UPDATE_CURRENT));

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notify = new Handler() {
            private int callCount = 0;

            @Override
            public void handleMessage(Message msg) {
                callCount++;
                ServiceResponse response = (ServiceResponse) msg.obj;
                List<String> alerts = new ArrayList<>();
                for (de.g2d.nagioto.domain.Service service : response.status.services) {
                    alerts.add(service.status + ": " + service.host + " > " + service.service);
                }
                String msgText = "" + callCount + ":" + StringUtils.join(alerts, ", ");
                Log.d("ALERT RESPONSE", ReflectionToStringBuilder.toString(response, ToStringStyle.MULTI_LINE_STYLE));
                Notification notification = notificationBuilder.setContentText(msgText).build();
                notificationManager.notify(37, notification);
            }
        };

    }

    public class BackgroundServiceBinder extends Binder {
        public BackgroundService getService() {
            return BackgroundService.this;
        }
    }

    public void startPolling(final Settings settings) {
        task = new AlertRequestTask(getApplicationContext(), notify, settings);
        alertRunner = new Thread(task);
        alertRunner.start();
    }

    public void fetchStatus(final Settings settings, final UiCallback callback) {
        HostRequestTask hostRequestTask = new HostRequestTask(getApplicationContext(), new HostRequestTask.HostRequestCallback() {
            @Override
            public void onFinish(final HostResponse hosts) {
                ServiceRequestTask serviceRequestTask = new ServiceRequestTask(getApplicationContext(), new ServiceRequestTask.ServiceRequestCallback() {
                    @Override
                    public void onFinish(ServiceResponse services) {
                        try {
                            Status status = new IcingaMapper().mapStatus(hosts, services);
                            callback.onStatusResponse(status);
                        } catch (IOException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }

                    }
                });
                serviceRequestTask.execute(settings);
            }
        });
        hostRequestTask.execute(settings);

    }

}
