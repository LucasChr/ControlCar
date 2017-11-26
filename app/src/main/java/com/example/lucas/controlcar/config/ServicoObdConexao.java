package com.example.lucas.controlcar.config;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Created by lucas on 25/11/17.
 */

public class ServicoObdConexao implements ServiceConnection {

    private ServicoObd service = null;

    public void onServiceConnected(ComponentName componentName, IBinder service) {
        this.service = ((ServicoObd.ServicoOBDBinder)service).getService();
    }

    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    public ServicoObd getService() {
        return service;
    }

    public boolean isRunning() {
        if (service == null) {
            return false;
        }
        return service.isRunning();
    }
}
