package com.google.kpierudzki.driverassistant.background_work.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.google.kpierudzki.driverassistant.App;
import com.google.kpierudzki.driverassistant.background_work.connector.EmptyListener;
import com.google.kpierudzki.driverassistant.common.IDestroyable;
import com.google.kpierudzki.driverassistant.service.connector.ManagerConnectorType;
import com.google.kpierudzki.driverassistant.service.helper.AssistantServiceManagerHelper;

/**
 * Created by Kamil on 08.12.2017.
 */

public class AppInBackgroundReceiver extends BroadcastReceiver implements IDestroyable {

    private Context context;
    private AssistantServiceManagerHelper managerHelper;
    private EmptyListener emptyListener;

    public AppInBackgroundReceiver(Context context, AssistantServiceManagerHelper managerHelper) {
        this.managerHelper = managerHelper;
        this.context = context;
        emptyListener = new EmptyListener();
        context.registerReceiver(this, new IntentFilter() {{
            addAction(App.APP_IN_BACKGROUND_ACTION);
            addAction(App.APP_IN_FOREGROUND_ACTION);
        }});
    }

    @Override
    public void onDestroy() {
        managerHelper = null;
        context.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action;
        if (intent != null && (action = intent.getAction()) != null) {
            switch (action) {
                case App.APP_IN_BACKGROUND_ACTION:
                    managerHelper.addListener(ManagerConnectorType.BackgroundWork, emptyListener);
                    break;
                case App.APP_IN_FOREGROUND_ACTION:
                    managerHelper.removeListener(ManagerConnectorType.BackgroundWork, emptyListener);
                    break;
            }
        }
    }
}
