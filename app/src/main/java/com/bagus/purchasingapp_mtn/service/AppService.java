package com.bagus.purchasingapp_mtn.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bagus.purchasingapp_mtn.R;
import com.bagus.purchasingapp_mtn.ui.user_main.UserMainActivity;
import com.bagus.purchasingapp_mtn.utils.Constants;
import com.bagus.purchasingapp_mtn.utils.Utils;

import static android.support.v4.app.NotificationCompat.PRIORITY_MIN;

public class AppService extends Service {

    private Notification notification;
    private NotificationManager notificationManager;

    private String CHANNEL_ID = "my_channel_01";// The id of the channel.
    private CharSequence name = "Purchasing APPS";// The user-visible name of the channel.
    private int importance = NotificationManager.IMPORTANCE_MIN;
    private NotificationChannel mChannel = null;

    private Handler handler;
    private Runnable runnable;
    private long UPDATE_INTERVAL = 10 * 3000;  /* 30 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 sec */
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            Log.w("getAction", intent.getAction());
            if (intent.getAction().equals(Constants.STARTFOREGROUND_ACTION)) {
                startServiceOreoCondition();
            } else if (intent.getAction().equals(Constants.STOPFOREGROUND_ACTION)) {
                Log.w("Stop", "stop");
                stopForeground(true);
                this.stopSelf();
            }
        }
        handler.postDelayed(runnable, FASTEST_INTERVAL);
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        handler = new Handler();
        runnable = () -> {
            if (Utils.getBooleanPreference(this, Constants.IS_LOGIN)) {
                runningBackground();
            } else {
                Log.w("getAction", "starting... on (login = false)" + Utils.getTimestampNow());
            }
            handler.postDelayed(runnable, UPDATE_INTERVAL);
        };
    }

    private void runningBackground() {
        
        Log.w("TAG RUNNING BACKGROUND", Utils.getTimestampNow());
        
    }
    

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(String.valueOf(this), "Service Stop!");
        Intent intent = new Intent(AppService.this, AppBroadcastReceiver.class);
        sendBroadcast(intent);
    }

    private void startServiceOreoCondition() {
        if (Build.VERSION.SDK_INT >= 26) {

            String CHANNEL_ID = "my_service";
            String CHANNEL_NAME = "ROAD_OPTIMISATION_SERVICE";

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(PRIORITY_MIN)
                    .setOngoing(false)
                    .setAutoCancel(true)
                    .build();

            startForeground(101, notification);
            launchNotification("Service", "Tap to open apps", 101);
        }
    }

    private Notification createNotificationNew(String title, String message) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = null;
        PendingIntent pendingIntent = null;
        intent = new Intent(this, UserMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setLights(Color.RED, 3000, 3000)
                .setVibrate(new long[]{500, 500, 500, 500});
        notificationBuilder.setSound(defaultSoundUri);

        Notification notification = notificationBuilder.setChannelId(CHANNEL_ID).build();
        notification.ledARGB = 0xFFff0000;
        notification.flags = Notification.FLAG_SHOW_LIGHTS;
        notification.ledOnMS = 100;
        notification.ledOffMS = 100;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        return notification;
    }

    private void launchNotification(String title, String message, int notificationID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
            notification = createNotificationNew(title, message);
            notificationManager.notify(notificationID, notification);
        } else {
            notification = createNotificationNew(title, message);
            notificationManager.notify(notificationID, notification);
        }
    }
}
