package com.example.lucas.controlcar.relatorio;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.controlcar.R;
import com.example.lucas.controlcar.bluetooth.ListaDispositivos;
import com.example.lucas.controlcar.config.ServicoObd;
import com.example.lucas.controlcar.obd.ConexaoObd;
import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.control.ModuleVoltageCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.github.pires.obd.exceptions.UnableToConnectException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class RelatorioCadActivity extends AppCompatActivity {


    // Message types sent from the OBD2MonitorService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // LogFile
    public static final String DIR_NAME_OBD2_MONITOR = "OBDIIMonitorLog";
    public static final String FILE_NAME_OBD2_MONITOR_LOG = "obd2_monitor_log.txt";

    // Key names received from the OBD2MonitorService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    Button btnConectar;
    TextView teste;

    private static final int SOLICITA_ATIVACAO = 1;
    private static final int SOLICITA_CONEXAO = 2;

    RelatorioCadActivity.ConnectedThread connectedThread;
    ArrayAdapter adapter;
    TableLayout lista;
//    Handler mHandler;
//    StringBuilder dadosBluetooth = new StringBuilder();

    BluetoothAdapter btAdapter = null;
    BluetoothDevice btDevice = null;
    BluetoothSocket btSocket = null;

    Context ctx;

    Handler handlerDados;

    ConexaoObd conexaoObd;
    boolean conexao = false;

    private static String MAC = null;


    UUID btf_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private ServicoObd servico;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_cad);

        btnConectar = (Button) findViewById(R.id.relatorio_cad_btnConectar);
        lista = (TableLayout) findViewById(R.id.relatorio_cad_lista);
        teste = (TextView) findViewById(R.id.relatorio_cad_tvTeste);
//        vv = (LinearLayout) findViewById(R.id.vehicle_view);

        handlerDados = new Handler();
        preRequisitos();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SOLICITA_ATIVACAO:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "Bluetooth ativado!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth não ativado neste aparelho", Toast.LENGTH_LONG).show();
                }
                break;
            case SOLICITA_CONEXAO:
                if (resultCode == Activity.RESULT_OK) {
                    MAC = data.getExtras().getString(ListaDispositivos.ENDERECO_MAC);
                    btDevice = btAdapter.getRemoteDevice(MAC);
                    try {
                        btSocket = btDevice.createRfcommSocketToServiceRecord(btf_UUID);
                        btSocket.connect();
                        conexao = true;
                        connectedThread = new RelatorioCadActivity.ConnectedThread(btSocket);
                        connectedThread.start();
                        btnConectar.setText("Desconectar");
                        Toast.makeText(getApplicationContext(), "Conecado com: " + MAC, Toast.LENGTH_LONG).show();
                        Log.d("Passou", MAC);
                        preparaConexao();
                    } catch (IOException e) {
                        conexao = false;
                        Toast.makeText(getApplicationContext(), "Ocorreu um erro: " + e, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Falha ao obter MAC!", Toast.LENGTH_LONG).show();
                }
        }
    }

    private void preRequisitos() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null) {
            Toast.makeText(getApplicationContext(), "O dispositivo não tem bluetooth", Toast.LENGTH_LONG).show();
        } else if (!btAdapter.isEnabled()) {
            Intent it = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(it, SOLICITA_ATIVACAO);
        }
    }

    public void Conectar(View v) {
        if (conexao) {
            //desconectar
            try {
                btSocket.close();
                conexao = false;
                btnConectar.setText("Conectar");
                Toast.makeText(getApplicationContext(), "Bluetooth desconectado", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Ocorreu um erro: " + e, Toast.LENGTH_LONG).show();
            }
        } else {
            //conectar
            Intent it = new Intent(RelatorioCadActivity.this, ListaDispositivos.class);
            startActivityForResult(it, SOLICITA_CONEXAO);
        }
    }

    ModuleVoltageCommand moduleVoltageCommand;
    RPMCommand rpmCommand;
    ObdCommand command;

    private void preparaConexao() {
        try {
            new ObdResetCommand().run(btSocket.getInputStream(), btSocket.getOutputStream());
            Log.d("OBD", "OBDRESETCOMMAND");
            new EchoOffCommand().run(btSocket.getInputStream(), btSocket.getOutputStream());
            Log.d("OBD", "ECHOOFFCOMMAND");
            new LineFeedOffCommand().run(btSocket.getInputStream(), btSocket.getOutputStream());
            Log.d("OBD", "LINEFEEDOFFCOMMAND");
            new TimeoutCommand(125).run(btSocket.getInputStream(), btSocket.getOutputStream());
            Log.d("OBD", "TIMEOUTCOMMAND");
            new SelectProtocolCommand(ObdProtocols.AUTO).run(btSocket.getInputStream(), btSocket.getOutputStream());
            Log.d("OBD", "SELECTPROTOCOLCOMMAND");

            Thread.sleep(500);

            final Thread dados = new Thread() {

                @Override
                public void run() {
                    try {
                        rpmCommand = new RPMCommand();
                        rpmCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
//                        for (ObdCommand cmd : ComandosObd.getComandos()) {
//                            command = cmd;
//                            command.run(btSocket.getInputStream(), btSocket.getOutputStream());
//                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (UnableToConnectException e) {
                        e.printStackTrace();
                        Log.e("Dado", "Unable" + e.getMessage());
                    }
//                    try {
//                        sleep(100);
//                    } catch (InterruptedException ie) {
//                    }


                    handlerDados.postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            novaTableRow("Voltagem da Bateria", moduleVoltageCommand.getFormattedResult());
//                            novaTableRow("Voltagem da Bateria", moduleVoltageCommand.getFormattedResult());
                            novaTableRow(rpmCommand.getName(), rpmCommand.getFormattedResult());
                        }
                    }, 1000);
                }
            };

            dados.start();


//            while (!Thread.currentThread().isInterrupted()) {
//                ObdCommandJob job = null;
//                try {
//                    job = jobsQueue.take();
//
//                    // log job
//                    Log.d(TAG, "Taking job[" + job.getId() + "] from queue..");
//
//                    if (job.getState().equals(ObdCommandJob.ObdCommandJobState.NEW)) {
//                        Log.d(TAG, "Job state is NEW. Run it..");
//                        job.setState(ObdCommandJob.ObdCommandJobState.RUNNING);
//                        if (sock.isConnected()) {
//                            job.getCommand().run(btSocket.getInputStream(), btSocket.getOutputStream());
//                        } else {
//                            job.setState(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR);
//                            Log.e(TAG, "Can't run command on a closed socket.");
//                        }
//                    } else
//                        // log not new job
//                        Log.e(TAG,
//                                "Job state was not new, so it shouldn't be in queue. BUG ALERT!");
//                } catch (InterruptedException i) {
//                    Thread.currentThread().interrupt();
//                } catch (UnsupportedCommandException u) {
//                    if (job != null) {
//                        job.setState(ObdCommandJob.ObdCommandJobState.NOT_SUPPORTED);
//                    }
//                    Log.d(TAG, "Command not supported. -> " + u.getMessage());
//                } catch (IOException io) {
//                    if (job != null) {
//                        if(io.getMessage().contains("Broken pipe"))
//                            job.setState(ObdCommandJob.ObdCommandJobState.BROKEN_PIPE);
//                        else
//                            job.setState(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR);
//                    }
//                    Log.e(TAG, "IO error. -> " + io.getMessage());
//                } catch (Exception e) {
//                    if (job != null) {
//                        job.setState(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR);
//                    }
//                    Log.e(TAG, "Failed to run command. -> " + e.getMessage());
//                }
//
//                if (job != null) {
//                    final ObdCommandJob job2 = job;
//                    ((RelatorioCadActivity) ctx).runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            ((RelatorioCadActivity) ctx).stateUpdate(job2);
//                        }
//                    });
//                }
//            }

            //                novaTableRow("RPM",engineRpmCommand.getFormattedResult());
//                novaTableRow("Speed", speedCommand.getFormattedResult());
//                teste.setText(moduleVoltageCommand.getFormattedResult());

//            while (!Thread.currentThread().isInterrupted()) {
//                for (ObdCommand command : ComandosObd.getComandos()) {
//                    command.run(btSocket.getInputStream(), btSocket.getOutputStream());
//                    novaTableRow(command.getName(), command.getFormattedResult());
//                }
//                buscaDados();
//                ModuleVoltageCommand m = new ModuleVoltageCommand();
//                m.run(btSocket.getInputStream(), btSocket.getOutputStream());
//                teste.setText(m.getFormattedResult());
            Log.d("Passou dados", "comandos");
            Toast.makeText(servico, "Dados Atualizados", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();


        } catch (Exception e) {

        }
    }

    public void novaTableRow(String comando, String resultado) {
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
        lista.addView(tr, params);
    }

    public void Atualizar(View v) {
        buscaDados();
    }

    public void buscaDados() {
        lista.removeAllViews();
        Thread atualiza = new Thread() {
            public void run() {
                try {
                    try {
                        rpmCommand = new RPMCommand();
                        rpmCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
//                        for (ObdCommand cmd : ComandosObd.getComandos()) {
//                            command = cmd;
//                            command.run(btSocket.getInputStream(), btSocket.getOutputStream());
//                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (UnableToConnectException e) {
                        e.printStackTrace();
                        Log.e("Dado", "Unable" + e.getMessage());
                    }
//                    try {
//                        sleep(100);
//                    } catch (InterruptedException ie) {
//                    }


                    handlerDados.postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            novaTableRow("Voltagem da Bateria", moduleVoltageCommand.getFormattedResult());
//                            novaTableRow("Voltagem da Bateria", moduleVoltageCommand.getFormattedResult());
                            novaTableRow(rpmCommand.getName(), rpmCommand.getFormattedResult());
                        }
                    }, 1000);
                } catch (Exception e) {
                }
            }
        };
        atualiza.start();
//        adapter.notifyDataSetChanged();
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
    }

//    private boolean isServiceBound;
//    private boolean preRequisites = true;
//    private SharedPreferences pref;
//    public Map<String, String> commandResult = new HashMap<String, String>();
//    private PowerManager.WakeLock wakeLock = null;
//    private LinearLayout vv;
//
//
//    private final Runnable mQueueCommands = new Runnable() {
//        public void run() {
//            if (service != null && service.isRunning() && service.queueEmpty()) {
//                queueCommands();
//                commandResult.clear();
//            }
//            // run again in period defined in preferences
//            new Handler().postDelayed(mQueueCommands, 4000);
//        }
//    };
//
//    private void queueCommands() {
//        if (isServiceBound) {
//            for (ObdCommand Command : ComandosObd.getComandos()) {
//                if (pref.getBoolean(Command.getName(), true))
//                    service.queueJob(new ObdCommandJob(Command));
//            }
//        }
//    }
//
//    private ServiceConnection serviceConn = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName className, IBinder binder) {
//            Log.d("DADOS", className.toString() + " service is bound");
//            isServiceBound = true;
//            service.setContext(RelatorioCadActivity.this);
//            Log.d("DADOS", "Starting live data");
//            try {
//                service.startService();
//                if (preRequisites) {
//                }
////                    btStatusTextView.setText(getString(R.string.status_bluetooth_connected));
//            } catch (IOException ioe) {
//                Log.e("DADOS", "Failure Starting live data");
////                btStatusTextView.setText(getString(R.string.status_bluetooth_error_connecting));
//                doUnbindService();
//            }
//        }
//
//        @Override
//        protected Object clone() throws CloneNotSupportedException {
//            return super.clone();
//        }
//
//        // This method is *only* called when the connection to the service is lost unexpectedly
//        // and *not* when the client unbinds (http://developer.android.com/guide/components/bound-services.html)
//        // So the isServiceBound attribute should also be set to false when we unbind from the service.
//        @Override
//        public void onServiceDisconnected(ComponentName className) {
//            Log.d("DADOS", className.toString() + " service is unbound");
//            isServiceBound = false;
//        }
//    };
//
//
//    private void startLiveData() {
//        Log.d("DADOS", "Starting live data..");
//
//        lista.removeAllViews(); //start fresh
//        doBindService();
//
//        // start command execution
//        new Handler().post(mQueueCommands);
//
//        // screen won't turn off until wakeLock.release()
////        wakeLock.acquire();
//    }
//
//    private void stopLiveData() {
//        Log.d("DADOS", "Stopping live data..");
//
//        doUnbindService();
//        releaseWakeLockIfHeld();
//    }
//
//    private void releaseWakeLockIfHeld() {
//        if (wakeLock.isHeld())
//            wakeLock.release();
//    }
//
//
//    private void doBindService() {
//        if (!isServiceBound) {
//            Log.d("DADOS", "Binding OBD service..");
//            if (preRequisites) {
////                btStatusTextView.setText(getString(R.string.status_bluetooth_connecting));
//                Intent serviceIntent = new Intent(this, ObdGatewayService.class);
//                bindService(serviceIntent, serviceConn, Context.BIND_AUTO_CREATE);
//            } else {
////                btStatusTextView.setText(getString(R.string.status_bluetooth_disabled));
//                Intent serviceIntent = new Intent(this, MockObdGatewayService.class);
//                bindService(serviceIntent, serviceConn, Context.BIND_AUTO_CREATE);
//            }
//        }
//    }
//
//    private void doUnbindService() {
//        if (isServiceBound) {
//            if (service.isRunning()) {
//                service.stopService();
//                if (preRequisites) {
//                }
////                    btStatusTextView.setText(getString(R.string.status_bluetooth_ok));
//            }
//            Log.d("DADOS", "Unbinding OBD service..");
//            unbindService(serviceConn);
//            isServiceBound = false;
////            obdStatusTextView.setText(getString(R.string.status_obd_disconnected));
//        }
//    }
//
//    public static String LookUpCommand(String txt) {
//        for (AvailableCommandNames item : AvailableCommandNames.values()) {
//            if (item.getValue().equals(txt)) return item.name();
//        }
//        return txt;
//    }
//
//    public void stateUpdate(final ObdCommandJob job) {
//        final String cmdName = job.getCommand().getName();
//        String cmdResult = "";
//        final String cmdID = LookUpCommand(cmdName);
//
//        if (job.getState().equals(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR)) {
//            cmdResult = job.getCommand().getResult();
//            if (cmdResult != null && isServiceBound) {
////                obdStatusTextView.setText(cmdResult.toLowerCase());
//            }
//        } else if (job.getState().equals(ObdCommandJob.ObdCommandJobState.BROKEN_PIPE)) {
//            if (isServiceBound)
//                stopLiveData();
//        } else if (job.getState().equals(ObdCommandJob.ObdCommandJobState.NOT_SUPPORTED)) {
//            cmdResult = getString(R.string.status_obd_no_support);
//        } else {
//            cmdResult = job.getCommand().getFormattedResult();
//            if (isServiceBound) {
//            }
////                obdStatusTextView.setText(getString(R.string.status_obd_data));
//        }
//
//        if (vv.findViewWithTag(cmdID) != null) {
//            TextView existingTV = (TextView) vv.findViewWithTag(cmdID);
//            existingTV.setText(cmdResult);
//        } else addTableRow(cmdID, cmdName, cmdResult);
//        commandResult.put(cmdID, cmdResult);
////        updateTripStatistic(job, cmdID);
//    }
//
//    private void addTableRow(String id, String key, String val) {
//
//        TableRow tr = new TableRow(this);
//        TextView name = new TextView(this);
//        name.setGravity(Gravity.RIGHT);
//        name.setText(key + ": ");
//        TextView value = new TextView(this);
//        value.setGravity(Gravity.LEFT);
//        value.setText(val);
//        value.setTag(id);
//        tr.addView(name);
//        tr.addView(value);
//        lista.addView(tr);
//    }
//
//    public void Start(View v) {
//        startLiveData();
//    }
}
