package de.g2d.nagioto.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import de.g2d.nagioto.MainActivity;
import de.g2d.nagioto.R;
import de.g2d.nagioto.UiCallback;
import de.g2d.nagioto.Utils;
import de.g2d.nagioto.api.HostRequestCallback;
import de.g2d.nagioto.api.ServiceRequestCallback;
import de.g2d.nagioto.domain.HostResponse;
import de.g2d.nagioto.domain.IcingaMapper;
import de.g2d.nagioto.domain.ServiceResponse;
import de.g2d.nagioto.domain.Settings;
import de.g2d.nagioto.domain.Status;

/**
/**
 * Created by sasse_h on 07.12.14.
 */
public class BackgroundService extends Service {
    private static final String TAG = MainActivity.class.getSimpleName();
    private IBinder binder = new BackgroundServiceBinder();
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notification;
    private Handler notify;
    private Thread alertRunner;
    private AlertRequestTask task;
    private AtomicInteger hostDemoCount = new AtomicInteger(0);
    private AtomicInteger alertCount = new AtomicInteger(0);
    private RemoteViews notificationView;
    private Canvas trafficCanvas;
    private Paint paint;
    private Bitmap trafficBitmap;

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

        notificationView = new RemoteViews(getPackageName(), R.layout.notification);
        paint = new Paint(0);
        trafficBitmap = Bitmap.createBitmap(19, 19, Bitmap.Config.ARGB_8888);
        trafficCanvas = new Canvas(trafficBitmap);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent alertIntent = new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent pendingAlert = PendingIntent.getActivity(this, 0, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setAutoCancel(true)
                .setOngoing(true)
                .setContentIntent(pendingAlert);

        // FIXME use interface as well
        notify = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ServiceResponse response = (ServiceResponse) msg.obj;

                List<String> alerts = new ArrayList<>();
                String status = "OK";
                int color = Color.GREEN;
                for (de.g2d.nagioto.domain.Service service : response.status.services) {
                    if (color != Color.RED) {
                        if (color != Color.YELLOW && service.status.equals("WARNING")) {
                            color = Color.YELLOW;
                            status = "WARNING !";
                        } else {
                            color = Color.RED;
                            status = "ERROR !";
                        }
                    }
                    alerts.add(service.status + ": " + service.host + " > " + service.service);
                }
                paint.setColor(color);
                trafficCanvas.drawCircle(10, 10, 7, paint);

                notificationView.setImageViewBitmap(R.id.traffic, trafficBitmap);
                notificationView.setTextViewText(R.id.count, "c: " + alertCount.addAndGet(1));
                notificationView.setTextViewText(R.id.status, status);
                notificationView.setTextViewText(R.id.services, StringUtils.join(alerts, ", "));

                Notification notification = notificationBuilder.setContent(notificationView).build();
                notificationManager.notify(37, notification);
            }
        };

    }

    public class BackgroundServiceBinder extends Binder {
        public BackgroundService getService() {
            return BackgroundService.this;
        }
    }

    public void startPolling(final Settings settings, final UiCallback callback) {
        if (!isNetworkUp(getApplicationContext())) {
            onNoConnection(callback);
        }
        task = new AlertRequestTask(getApplicationContext(), callback, notify, settings);
        alertRunner = new Thread(task);
        alertRunner.start();
    }

    public void fetchStatus(final Settings settings, final UiCallback callback) {
        if (settings.isDemo()) { // rotate loaded demo files
            int i = hostDemoCount.addAndGet(1) % 2;
            if (i == 0) {
                i = 2;
            }
            String fileName = "host_status_" + i + ".json";
            String hostData = Utils.readFromFile(this.getApplicationContext(), fileName);
            String serviceData = Utils.readFromFile(this.getApplicationContext(), "service_status_1.json");
            try {
                IcingaMapper icingaMapper = new IcingaMapper();
                HostResponse hostResponse = icingaMapper.mapHost(hostData);
                ServiceResponse serviceResponse = icingaMapper.mapService(serviceData);
                Status status = icingaMapper.mapStatus(hostResponse, serviceResponse);
                callback.onStatusResponse(status);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (!isNetworkUp(getApplicationContext())) {
            onNoConnection(callback);
        } else {
            new HostRequestTask(getApplicationContext(), callback, new HostRequestCallback() {
                @Override
                public void onFinish(final HostResponse hosts) {
                    new ServiceRequestTask(getApplicationContext(), callback, new ServiceRequestCallback() {
                        @Override
                        public void onFinish(ServiceResponse services) {
                            try {
                                Status status = new IcingaMapper().mapStatus(hosts, services);
                                callback.onStatusResponse(status);
                            } catch (IOException e) {
                                callback.onError(e);
                            }
                        }
                    }).execute(settings);
                }
            }).execute(settings);
        }

    }

    public static boolean isNetworkUp(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void onNoConnection(UiCallback errorCallback) {
        if (!isNetworkUp(getApplicationContext())) {
            String label = getResources().getString(R.string.error_network_label);
            String message = getResources().getString(R.string.error_network);
            errorCallback.onError(label, new Throwable(message));
        }
    }

}
