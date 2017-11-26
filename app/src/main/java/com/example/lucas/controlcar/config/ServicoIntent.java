package com.example.lucas.controlcar.config;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by lucas on 19/11/17.
 */

public class ServicoIntent extends IntentService {
    private int count;
    private boolean ativo;
    private boolean stopAll;

    public ServicoIntent() {
        super("ServicoIntentThread");

        count = 0;
        ativo = true;
        stopAll = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle b = intent.getExtras();
        if (b != null) {
            int desligar = b.getInt("desligar");
            if (desligar == 1) {
                stopAll = false;
            }
        }
        return (super.onStartCommand(intent, flags, startId));
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        while (stopAll && ativo && count < 5) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            Log.i("Servico_Intent", "COUNT:" + count);
            count++;
        }

        ativo = true;
        count = 0;
    }
}
