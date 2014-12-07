package de.g2d.nagioto.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import de.g2d.nagioto.R;
import de.g2d.nagioto.StatusCallback;
import de.g2d.nagioto.domain.Settings;

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


    public void fetchServers(Settings settings, StatusCallback callback) {
        ServerRequestTask serverRequestTask = new ServerRequestTask(getApplicationContext(), callback);
        serverRequestTask.execute(settings);
    }

}
