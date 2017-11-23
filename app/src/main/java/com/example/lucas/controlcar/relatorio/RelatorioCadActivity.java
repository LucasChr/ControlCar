package com.example.lucas.controlcar.relatorio;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.nfc.Tag;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.example.lucas.controlcar.obd.ComandosObd;
import com.example.lucas.controlcar.config.Servico;
import com.example.lucas.controlcar.obd.ConexaoObd;
import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class RelatorioCadActivity extends AppCompatActivity {

    Button btnConectar;

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

    ConexaoObd conexaoObd;
    boolean conexao = false;

    private static String MAC = null;


    UUID btf_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private Servico servico;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_cad);

        btnConectar = (Button) findViewById(R.id.relatorio_cad_btnConectar);
        lista = (TableLayout) findViewById(R.id.relatorio_cad_lista);

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
                        conexao();
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

    private void conexao() {
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

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            while (!Thread.currentThread().isInterrupted()) {
                for (ObdCommand command : ComandosObd.getComandos()) {
                    novaTableRow(command.getName(), command.getFormattedResult());
                }
                Log.d("Passou dados", "comandos");
                Toast.makeText(servico, "Dados Atualizados", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            // handle errors
        }
    }

    private void novaTableRow(String comando, String resultado) {
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

    public void buscaDados() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Buscando dados da Ecu");
        pd.show();

        RelatorioCadActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                for (ObdCommand command : ComandosObd.getComandos()) {
                    novaTableRow(command.getName(), command.getFormattedResult());
                }
                pd.setMessage("Dados Coletados");
                pd.dismiss();
            }
        });

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

    public void startService() {
        Intent it = new Intent("SERVICO_TESTE");
        startService(it);
    }

}
