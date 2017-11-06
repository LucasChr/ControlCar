package com.example.lucas.controlcar.relatorio;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.controlcar.R;
import com.example.lucas.controlcar.config.ConfigActivity;
import com.example.lucas.controlcar.config.ObdCommandJob;
import com.example.lucas.controlcar.principal.PrincipalActivity;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.enums.AvailableCommandNames;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RelatorioCadActivity extends AppCompatActivity {

    private static final int SOLICITA_ATIVACAO = 1;
    private static final int MESSAGE_READ = 3;

    BluetoothAdapter btfAdapter = null;

    private BluetoothDevice btDevice = null;
    private BluetoothSocket btSocket = null;

    private static String MAC = "00:00:00:00:00:01";
    private static final UUID bt_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private boolean conexao = false;
    private Context ctx = this;

    RelatorioCadActivity.ConnectedThread connectedThread;
    RelatorioCadActivity.AUX aux;
    private static final String TAG = "Activity Relatorio";

    StringBuilder dadosBluetooth = new StringBuilder();
    private TextView tvObdStatus, tvGpsStatus, tvBtfStatus;
    private TableLayout tlRelatorio;
    public Map<String, String> commandResult = new HashMap<String, String>();
    private boolean isServiceBound;
    private ListView lista;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_cad);

        tvObdStatus = (TextView) findViewById(R.id.tvOBD);
        tvGpsStatus = (TextView) findViewById(R.id.tvGPS);
        tvBtfStatus = (TextView) findViewById(R.id.tvBTF);
        lista = (ListView) findViewById(R.id.lista);

        arrayList = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom, arrayList);
        lista.setAdapter(adapter);

         /*Conecao com OBD*/
        btfAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btfAdapter == null) {
            Toast.makeText(getApplicationContext(), "O dispositivo não tem bluetooth", Toast.LENGTH_LONG).show();
        } else if (!btfAdapter.isEnabled()) {
            Intent it = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(it, SOLICITA_ATIVACAO);
        }

        try {
            IniciaConexao();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        }
    }

    public void IniciaConexao() throws Exception {
        Log.d("Conexao", "iniciou");

        if (conexao) {
            //desconectar
            try {
                btSocket.close();
                conexao = false;
                Toast.makeText(getApplicationContext(), "Bluetooth desconectado", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Ocorreu um erro: " + e, Toast.LENGTH_LONG).show();
            }
        } else {
            //conectar
            tvBtfStatus.setText(R.string.btf_ativado);
            btDevice = btfAdapter.getRemoteDevice(MAC);
            Log.d("Conexao", "MAC");
            try {
                btSocket = btDevice.createRfcommSocketToServiceRecord(bt_UUID);
                Log.d("Conexao", "passou UUID");
                btSocket.connect();
                Log.d("Conexao", "Conectou");
                conexao = true;
//                connectedThread = new ConnectedThread(btSocket);
//                connectedThread.start();
                //btnConectar.setText("Desconectar");
                Toast.makeText(getApplicationContext(), "Conecado com: " + MAC, Toast.LENGTH_LONG).show();
                tvObdStatus.setText(R.string.obd_conectado);
            } catch (IOException e) {
                conexao = false;
                AlertDialog.Builder builder = new AlertDialog.Builder(RelatorioCadActivity.this);
                builder.setTitle("OBD-II");
                builder.setMessage("O scanner não está disponível verifique as configurações");
                builder.setNegativeButton("Configurações", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent it = new Intent(getApplicationContext(), ConfigActivity.class);
                        startActivityForResult(it, 1);

                    }
                });
                builder.setPositiveButton("Sair", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Fecha a aplicação
                        finishAffinity();
                    }
                });
                AlertDialog alerta = builder.create();
                alerta.show();
                tvObdStatus.setText(R.string.obd_desconectado);
            }
        }

//        new EchoOffCommand().run(btSocket.getInputStream(), btSocket.getOutputStream());
//        Log.d("Primeiro Comando", "Passou 1");
//        new LineFeedOffCommand().run(btSocket.getInputStream(), btSocket.getOutputStream());
//        Log.d("Primeiro Comando", "Passou 2");
//        new TimeoutCommand(62).run(btSocket.getInputStream(), btSocket.getOutputStream());
//        Log.d("Primeiro Comando", "Passou 3");
//        new SelectProtocolCommand(ObdProtocols.AUTO).run(btSocket.getInputStream(), btSocket.getOutputStream());
//        Log.d("Primeiro Comando", "Passou 4");
//        while (!Thread.currentThread().isInterrupted()) {
//            RPMCommand engineRpmCommand = new RPMCommand();
//            SpeedCommand speedCommand = new SpeedCommand();
//            engineRpmCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
//            speedCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
//            Log.d(TAG, "RPM: " + engineRpmCommand.getFormattedResult());
//            Log.d(TAG, "Speed: " + speedCommand.getFormattedResult());
//        }
        ObdCommandJob job2;

        // Let's configure the connection.
        Log.d(TAG, "Queueing jobs for connection configuration..");
        new ObdCommandJob(new ObdResetCommand());
        Log.d("Primeiro Comando", "Passou 1");
        //Below is to give the adapter enough time to reset before sending the commands, otherwise the first startup commands could be ignored.
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        job2 = new ObdCommandJob(new EchoOffCommand());
        Log.d("JOB2", job2.getCommand().getResult());
        new ObdCommandJob(new LineFeedOffCommand());
        Log.d("Primeiro Comando", "Passou 3");
        new ObdCommandJob(new TimeoutCommand(62));
        Log.d("Primeiro Comando", "Passou 4");
        new ObdCommandJob(new SelectProtocolCommand(ObdProtocols.AUTO));
        Log.d("Primeiro Comando", "Passou 5");
        new ObdCommandJob(new AmbientAirTemperatureCommand());
        Log.d("Primeiro Comando", "Passou 6");

//            SpeedCommand speedCommand = new SpeedCommand();
        while (!Thread.currentThread().isInterrupted()) {
//            readRPMData(btSocket);
//            aux.Comandos();
            RPMCommand engineRpmCommand = new RPMCommand();
            engineRpmCommand.run(btSocket.getInputStream(), btSocket.getOutputStream());
            Log.d(TAG, String.valueOf(engineRpmCommand.getRPM()));
            arrayList.add(String.valueOf(engineRpmCommand.getRPM()));
            adapter.notifyDataSetChanged();
        }
    }

    private class AUX extends Thread {

        public void Comandos() {
            ObdCommandJob job = null;
            if (!new ObdCommandJob(new AirIntakeTemperatureCommand()).equals("null")) {
                job = new ObdCommandJob(new AirIntakeTemperatureCommand());
                if (!job.getCommand().getResult().contains("null")) {
                    Log.d(TAG, "RPM: " + job.getCommand().getResult());
                    if (job.getCommand().getResult() != null) {
                        arrayList.add(job.getCommand().getResult());
                        adapter.notifyDataSetChanged();
                    } else {
                        Thread.currentThread().stop();
                    }
                } else {
                    Log.d(TAG, "NULO");
                    Thread.currentThread().stop();
                }
            } else {
                Log.d(TAG, "nada");
                Thread.currentThread().stop();
            }
        }
    }

    public void addTableRow(String id, String key, String val) {
        TableRow tr = new TableRow(this);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tr.setLayoutParams(params);

        TextView name = new TextView(this);
        name.setGravity(Gravity.RIGHT);
        name.setText(key + ": ");
        TextView value = new TextView(this);
        value.setGravity(Gravity.LEFT);
        value.setText(val);
        value.setTag(id);
        tr.addView(name);
        tr.addView(value);
        tlRelatorio.addView(tr, params);
    }

    public static String LookUpCommand(String txt) {
        for (AvailableCommandNames item : AvailableCommandNames.values()) {
            if (item.getValue().equals(txt)) return item.name();
        }
        return txt;
    }

//    private String readRPMData(BluetoothSocket bluetoothSocket) throws Exception {
//        List buffer = new ArrayList<Integer>();
//        Thread.sleep(400);
//        String rawData = null;
//        String value = "";
//        InputStream in = bluetoothSocket.getInputStream();
//        byte b = 0;
//        StringBuilder res = new StringBuilder();
//
//        // read until '>' arrives
//        while ((char) (b = (byte) in.read()) != '>')
//            res.append((char) b);
//
//
//        rawData = res.toString().trim();
//
//        if (!rawData.contains("01 0C")) {
//            Log.d("Comando", rawData);
//            return rawData;
//
//        }
//
//        rawData = rawData.replaceAll("\r", " ");
//        rawData = rawData.replaceAll("01 0C", "");
//        rawData = rawData.replaceAll("41 0C", " ").trim();
//        String[] data = rawData.split(" ");
////
////        Log.i("com.example.app", "rawData: "+rawData);
////        Log.i("com.example.app", "data: "+data[0]);
////        Log.i("com.example.app", "datawew: "+Integer.decode("0x" + data[0]));
////        Log.i("com.example.app", "datawew: "+String.valueOf(Integer.decode("0x" + data[0])));
////
//        int a = Integer.decode("0x" + data[0]).intValue();
////
////
////        Log.i("com.example.app", "rawData1: "+rawData);
////        Log.i("com.example.app", "data1: "+data[1]);
////        Log.i("com.example.app", "datawew1: "+Integer.decode("0x" + data[1]));
////        Log.i("com.example.app", "datawew1: "+String.valueOf(Integer.decode("0x" + data[1])));
////
//        int b1 = Integer.decode("0x" + data[1]).intValue();
////
////
//        int values = ((a * 256) + b1) / 4;
////
////        Log.i("com.example.app", "values RPM: "+values);
//        return String.valueOf(values);
//    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
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

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    String dadosBtf = new String(buffer, 0, bytes);
                    Log.d("InStream", dadosBtf);
                    // Send the obtained bytes to the UI activity
                    //mHandler.obtainMessage(MESSAGE_READ, bytes, -1, dadosBtf).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }


}
