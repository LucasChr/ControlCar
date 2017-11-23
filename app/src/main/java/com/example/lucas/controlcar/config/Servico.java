package com.example.lucas.controlcar.config;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.lucas.controlcar.obd.ComandosObd;
import com.github.pires.obd.commands.ObdCommand;
import com.openxc.VehicleManager;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lucas on 18/11/17.
 */

public class Servico extends Service {
//    public ArrayList<Worker> threads = new ArrayList<Worker>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //onCreate cria uma vez só
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Servico", "onCreate");
        Timer mTimer = new Timer();

        try {
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // Ai verifica a conexão.
                }
            }, 0, 7000);
        } catch (Exception e) {
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Servico", "onStartCommand");
        return (super.onStartCommand(intent, flags, startId));
    }

    public class ServicoBinder extends Binder {
        public Servico getService() {
            return Servico.this;
        }
    }


    class Worker extends Thread {
        int count;
        int startId;
        public boolean ativo = true;

        public Worker(int startId) {
            this.startId = startId;
        }

        public void run() {
            while (ativo && count < 10) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                count++;
                Log.i("Servico", "Contador: " + count);
            }
            //Elimina o processo que foi aberto
            stopSelf(startId);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        for (int i = 0; i < threads.size(); i++) {
//            threads.get(i).ativo = false;
//        }
    }

    void executaComandos(TableLayout layout) throws InterruptedException {
        ArrayList comands = new ArrayList<>();
        for (ObdCommand command : ComandosObd.getComandos()) {
            novaTableRow(command.getName(), command.getFormattedResult(), layout);
        }
    }

    private void novaTableRow(String comando, String resultado, TableLayout layout) {
        TableRow tr = new TableRow(this);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tr.setLayoutParams(params);

        TextView name = new TextView(this);
        name.setGravity(Gravity.RIGHT);
        name.setText(comando + ": ");
        TextView value = new TextView(this);
        value.setGravity(Gravity.LEFT);
        value.setText(resultado);
        tr.addView(name);
        tr.addView(value);
        layout.addView(tr, params);
    }
}
