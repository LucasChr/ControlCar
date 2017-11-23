package com.example.lucas.controlcar.config;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by lucas on 18/11/17.
 */

public class Broadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Broadcast", "onReceive()");
        intent = new Intent("SERVICO_TEST");
        context.startService(intent);
    }
}
