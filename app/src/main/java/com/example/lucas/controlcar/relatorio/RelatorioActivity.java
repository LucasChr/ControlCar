package com.example.lucas.controlcar.relatorio;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.controlcar.R;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class RelatorioActivity extends Activity {

    TextView tvRPM;

    private static final int SOLICITA_ATIVACAO = 1;
    private static final int SOLICITA_CONEXAO = 2;
    private static final int MESSAGE_READ = 3;
    private static final String TAG = RelatorioActivity.class.getName();
    private static final int NO_BLUETOOTH_ID = 0;
    private static final int BLUETOOTH_DISABLED = 1;
    private static final int START_LIVE_DATA = 2;
    private static final int STOP_LIVE_DATA = 3;
    private static final int SETTINGS = 4;
    private static final int GET_DTC = 5;
    private static final int TABLE_ROW_MARGIN = 7;
    private static final int NO_ORIENTATION_SENSOR = 8;
    private static final int NO_GPS_SUPPORT = 9;
    private static final int TRIPS_LIST = 10;
    private static final int SAVE_TRIP_NOT_AVAILABLE = 11;
    private static final int REQUEST_ENABLE_BT = 1234;

    ConnectedThread connectedThread;

    Handler mHandler;
    StringBuilder dadosBluetooth = new StringBuilder();

    BluetoothAdapter btfAdapter = null;
    BluetoothDevice btfDevice = null;
    BluetoothSocket btfSocket = null;

    boolean conexao = false;

    private static String MAC = "00:00:00:00:00:01";

    UUID btf_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private TableLayout tl;
    private boolean isServiceBound;
    private boolean preRequisites = true;
    /// the trip log

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);

        tl = (TableLayout) findViewById(R.id.activity_relatorio_data_table);
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
                    //MAC = data.getExtras().getString(ListaDispositivos.ENDERECO_MAC);
                    btfDevice = btfAdapter.getRemoteDevice(MAC);
                    try {
                        btfSocket = btfDevice.createRfcommSocketToServiceRecord(btf_UUID);
                        btfSocket.connect();

                        conexao = true;

                        connectedThread = new ConnectedThread(btfSocket);
                        connectedThread.start();

                        try {
                            String teste = new EngineCoolantTemperatureCommand().toString();
                            Log.d("TESTE", teste);
                            tvRPM.setText(teste);
                        } catch (Exception e) {
                            // handle errors
                        }
                        //btnConectar.setText("Desconectar");

                        Toast.makeText(getApplicationContext(), "Conecado com: " + MAC, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        conexao = false;
                        Toast.makeText(getApplicationContext(), "Ocorreu um erro: " + e, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Falha ao obter MAC!", Toast.LENGTH_LONG).show();
                }
        }
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and ouput streams, using temp objects because
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

                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, dadosBtf).sendToTarget();

                } catch (IOException e) {
                    break;
                }
            }
        }
    }

}
