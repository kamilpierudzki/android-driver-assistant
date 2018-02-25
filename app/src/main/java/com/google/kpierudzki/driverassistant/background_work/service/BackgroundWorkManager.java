package com.google.kpierudzki.driverassistant.background_work.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.MainActivity;
import com.google.kpierudzki.driverassistant.R;
import com.google.kpierudzki.driverassistant.service.connector.IBaseManager;
import com.google.kpierudzki.driverassistant.service.connector.ManagerConnectorType;
import com.google.kpierudzki.driverassistant.service.manager.BaseServiceManager;

/**
 * Created by Kamil on 23.07.2017.
 */

public class BackgroundWorkManager extends BaseServiceManager {

    private final static int NOTIFICATION_ID = 2227;
    private final static int SHOW_APP_REQUEST_CODE = 1235;
    private final static String APP_CHANNEL = "eco_driving_assistant_channel";

    private Context context;
    private NotificationManager mNotificationManager;

    public BackgroundWorkManager(Context context) {
        super(new ManagerConnectorType[]{ManagerConnectorType.BackgroundWork});
        this.context = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void addListener(@NonNull ManagerConnectorType connectorType, @NonNull IBaseManager.IBaseManagerListener listener) {
        mNotificationManager.cancel(NOTIFICATION_ID);

        Intent contentIntent = new Intent(context, MainActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(context,
                SHOW_APP_REQUEST_CODE, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, APP_CHANNEL);
        notificationBuilder.setContentTitle(App.getAppContext().getString(R.string.app_name))
                .setContentText(App.getAppContext().getString(R.string.BackgroundWork_Notification_Message))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .setContentIntent(contentPendingIntent);

        ((Service) context).startForeground(NOTIFICATION_ID, notificationBuilder.build());
    }

    @Override
    public void removeListener(@NonNull ManagerConnectorType connectorType, @NonNull IBaseManager.IBaseManagerListener listener) {
        ((Service) context).stopForeground(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNotificationManager.cancel(NOTIFICATION_ID);
    }
}
