package ru.sawimmod.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import protocol.Protocol;
import ru.sawimmod.Options;
import ru.sawimmod.R;
import ru.sawimmod.SawimNotification;
import ru.sawimmod.roster.RosterHelper;

public class SawimService extends Service {

    private static final String LOG_TAG = SawimService.class.getSimpleName();
    private final Messenger messenger = new Messenger(new IncomingHandler());
    private PowerManager.WakeLock wakeLock;

    public static final int UPDATE_CONNECTION_STATUS = 1;
    public static final int UPDATE_APP_ICON = 2;
    public static final int SEND_NOTIFY = 3;
    public static final int SET_STATUS = 4;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG_TAG, "onStart();");
    }

    @Override
    public void onDestroy() {
        Log.i(LOG_TAG, "onDestroy();");
        release();
        stopForeground(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return messenger.getBinder();
    }

    private void updateLock() {
        if (!Options.getBoolean(Options.OPTION_WAKE_LOCK)) {
            release();
            return;
        }
        RosterHelper cl = RosterHelper.getInstance();
        boolean need = cl.isConnected() || cl.isConnecting();
        if (need) {
            if (!isHeld()) acquire();
        } else {
            if (isHeld()) release();
        }
    }

    private void acquire() {
        if (wakeLock == null) {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, LOG_TAG);
        } else {
            wakeLock.acquire();
        }
    }

    private void release() {
        if (isHeld()) wakeLock.release();
        wakeLock = null;
    }

    private boolean isHeld() {
        return (null != wakeLock) && wakeLock.isHeld();
    }

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(final Message msg) {
            try {
                switch (msg.what) {
                    case UPDATE_CONNECTION_STATUS:
                        updateLock();
                        break;
                    case UPDATE_APP_ICON:
                        SawimService.this.startForeground(R.string.app_name, SawimNotification.get(SawimService.this, false));
                        break;
                    case SEND_NOTIFY:
                        //SawimNotification.sendNotify(SawimService.this, ((String[])msg.obj)[0], ((String[])msg.obj)[1]);
                        SawimService.this.startForeground(R.string.app_name, SawimNotification.get(SawimService.this, (boolean)((Object[])msg.obj)[2]));
                        break;
                    case SET_STATUS:
                        final Protocol protocol = (Protocol) ((Object[]) msg.obj)[0];
                        final int statusIndex = (int) ((Object[]) msg.obj)[1];
                        final String statusMsg = (String) ((Object[]) msg.obj)[2];
                        protocol.setStatus(statusIndex, statusMsg);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
