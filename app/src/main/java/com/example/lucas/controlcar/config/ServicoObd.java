package com.example.lucas.controlcar.config;

import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by lucas on 18/11/17.
 */

public class ServicoObd extends Service {

    private ThreadConexaoOBD threadConexaoOBD = null;
    private final IBinder binder = new ServicoOBDBinder();
    private Intent notificationIntent = null;
    private PendingIntent contentIntent = null;
    private Context context = null;
    public static String MAC = "00:00:00:00:00:01";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Servico", "onCreate");
        context = getApplicationContext();
        Log.i("Servico", "context");
        notificationIntent = new Intent(this, ServicoObd.class);
        Log.i("Servico", "intent");
        contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Log.i("Servico", "pendingIntent");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService();
        stopSelf();
    }

    public boolean isRunning() {
        if (threadConexaoOBD == null) {
            return false;
        }
        return threadConexaoOBD.isAlive();
    }

    public IBinder onBind(Intent intent) {
        return binder;
    }

    public boolean startService() {
        if (threadConexaoOBD != null && threadConexaoOBD.isAlive()) {
            return true;
        }
        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Toast.makeText(getApplicationContext(), "O dispositivo não tem bluetooth", Toast.LENGTH_LONG).show();
            stopSelf();
            return false;
        }
        Log.i("Servico", "Pre MAC");
        BluetoothDevice btDevice = btAdapter.getRemoteDevice(MAC);
        Log.i("Servico", "Pos MAC");
        threadConexaoOBD = new ThreadConexaoOBD(btDevice);
        Log.i("Servico", "Thread");
        new ServicoObdWorkerThread(threadConexaoOBD).start();
        Log.i("Servico", "Worker");
        return true;
    }

    public boolean stopService() {
        if (threadConexaoOBD == null) {
            return true;
        }
        while (threadConexaoOBD.isAlive()) {
            try {
                threadConexaoOBD.cancel();
                threadConexaoOBD.join(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        threadConexaoOBD.close();
        stopSelf();
        return true;
    }

    public class ServicoOBDBinder extends Binder {
        ServicoObd getService() {
            return ServicoObd.this;
        }
    }

    private class ServicoObdWorkerThread extends Thread {

        ThreadConexaoOBD obd = null;

        public ServicoObdWorkerThread(ThreadConexaoOBD obd) {
            this.obd = obd;
        }

        public void run() {
            try {
                obd.start();
//                long when = System.currentTimeMillis();
//                Notification notification = new Notification(R.drawable.ic_menu_manage, "Serviço OBD em execução", when);
//                notification.setLatestEventInfo(context, "OBD Service Running", "", contentIntent);
//                notification.flags |= Notification.FLAG_NO_CLEAR;
//                notification.flags |= Notification.FLAG_ONGOING_EVENT;
//                notifyMan.notify(OBD_SERVICE_RUNNING_NOTIFY, notification);
                obd.join();
            } catch (Exception e) {
            } finally {
//                notifyMan.cancel(OBD_SERVICE_RUNNING_NOTIFY);
                stopSelf();
            }
        }
    }
}

//    void executaComandos() throws InterruptedException {
//        ArrayList retornoComandos = new ArrayList<>();
//        for (ObdCommand command : ComandosObd.getComandos()) {
//            retornoComandos.add(command.getFormattedResult());
//        }
//    }
//
//    private Looper mServiceLooper;
//    private ServiceHandler mServiceHandler;
//
//    // Handler that receives messages from the thread
//    private final class ServiceHandler extends Handler {
//        public ServiceHandler(Looper looper) {
//            super(looper);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            // Normally we would do some work here, like download a file.
//            // For our sample, we just sleep for 5 seconds.
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                // Restore interrupt status.
//                Thread.currentThread().interrupt();
//            }
//            // Stop the service using the startId, so that we don't stop
//            // the service in the middle of handling another job
//            stopSelf(msg.arg1);
//        }
//    }
//
//    @Override
//    public void onCreate() {
//        // Start up the thread running the service.  Note that we create a
//        // separate thread because the service normally runs in the process's
//        // main thread, which we don't want to block.  We also make it
//        // background priority so CPU-intensive work will not disrupt our UI.
////        HandlerThread thread = new HandlerThread("ServiceStartArguments",
////                Process.THREAD_PRIORITY_BACKGROUND);
////        thread.start();
//
//        // Get the HandlerThread's Looper and use it for our Handler
////        mServiceLooper = thread.getLooper();
//        mServiceHandler = new ServiceHandler(mServiceLooper);
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
//
//        // For each start request, send a message to start a job and deliver the
//        // start ID so we know which request we're stopping when we finish the job
//        Message msg = mServiceHandler.obtainMessage();
//        msg.arg1 = startId;
//        mServiceHandler.sendMessage(msg);
//
//        ArrayList retornoComandos = new ArrayList<>();
//        for (ObdCommand command : ComandosObd.getComandos()) {
//            retornoComandos.add(command.getFormattedResult());
//        }
//
//        // If we get killed, after returning from here, restart
//        return START_STICKY;
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // We don't provide binding, so return null
//        return null;
//    }
//
//    @Override
//    public void onDestroy() {
//        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
//    }


/**
 * A constructor is required, and must call the super IntentService(String)    * constructor with a name for the worker thread.
 * <p>
 * The IntentService calls this method from the default worker thread with    * the intent that started the service. When this method returns, IntentService    * stops the service, as appropriate.
 * <p>
 * The IntentService calls this method from the default worker thread with    * the intent that started the service. When this method returns, IntentService    * stops the service, as appropriate.
 * <p>
 * The IntentService calls this method from the default worker thread with    * the intent that started the service. When this method returns, IntentService    * stops the service, as appropriate.
 * <p>
 * The IntentService calls this method from the default worker thread with    * the intent that started the service. When this method returns, IntentService    * stops the service, as appropriate.
 * <p>
 * The IntentService calls this method from the default worker thread with    * the intent that started the service. When this method returns, IntentService    * stops the service, as appropriate.
 */
//    public ServicoObd() {
//        super("HelloIntentService");
//    }

/**
 * The IntentService calls this method from the default worker thread with    * the intent that started the service. When this method returns, IntentService    * stops the service, as appropriate.
 */
//    @Override
//    protected void onHandleIntent(Intent intent) {
//        // Normally we would do some work here, like download a file.
//        // For our sample, we just sleep for 5 seconds.
//        long endTime = System.currentTimeMillis() + 5 * 1000;
//        while (System.currentTimeMillis() < endTime) {
//            synchronized (this) {
//                try {
//                    wait(endTime - System.currentTimeMillis());
//                } catch (Exception e) {
//                }
//            }
//        }
//    }}
