package de.g2d.nagioto.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.io.IOException;

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

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        // later...
        Notification notification = new NotificationCompat.Builder(BackgroundService.this).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText("Kukkuk")
                .setAutoCancel(true)
                .setOngoing(true)
//                .addAction(R.drawable.ic_btn_delete, getString(R.string.notification_stop_recording), PendingIntent.getBroadcast(this, 0, stopRecordIntent, PendingIntent.FLAG_UPDATE_CURRENT))
//                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT), PendingIntent.FLAG_UPDATE_CURRENT))
                .build();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    }

    public class BackgroundServiceBinder extends Binder {
        public BackgroundService getService() {
            return BackgroundService.this;
        }
    }


    public void fetchStatus(final Settings settings, final UiCallback callback) {
        HostRequestTask hostRequestTask = new HostRequestTask(getApplicationContext(), new HostRequestTask.HostRequestCallback() {
            @Override
            public void onFinish(final HostResponse hosts) {
                ServiceRequestTask serviceRequestTask = new ServiceRequestTask(getApplicationContext(), new ServiceRequestTask.ServiceRequestCallback(){
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
