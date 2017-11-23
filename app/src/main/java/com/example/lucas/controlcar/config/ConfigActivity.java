package com.example.lucas.controlcar.config;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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
import android.os.Message;

import com.example.lucas.controlcar.R;
import com.example.lucas.controlcar.bluetooth.BluetoothCheckActivity;
import com.example.lucas.controlcar.bluetooth.BluetoothClienteActivity;
import com.example.lucas.controlcar.bluetooth.BuscarDispositivosActivity;
import com.example.lucas.controlcar.bluetooth.ListaDispositivos;
import com.example.lucas.controlcar.bluetooth.ListaPareadosActivity;
import com.example.lucas.controlcar.bluetooth.ReceberMensagemActivity;
import com.example.lucas.controlcar.obd.ComandosObd;
import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class ConfigActivity extends AppCompatActivity {

    private Intent intent;

    Button btnConectar;

    TextView tvTeste;
    private static final int SOLICITA_ATIVACAO = 1;
    private static final int SOLICITA_CONEXAO = 2;
    private static final int MESSAGE_READ = 3;

    ConnectedThread connectedThread;
    ArrayAdapter adapter;
    TableLayout lista;
    Handler mHandler;
    StringBuilder dadosBluetooth = new StringBuilder();

    BluetoothAdapter btfAdapter = null;
    BluetoothDevice btfDevice = null;
    BluetoothSocket btfSocket = null;

    boolean conexao = false;

    private static String MAC = null;

    UUID btf_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        btnConectar = (Button) findViewById(R.id.btnConectar);
        tvTeste = (TextView) findViewById(R.id.tvTeste);
        lista = (TableLayout) findViewById(R.id.lista);

        btfAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btfAdapter == null) {
            Toast.makeText(getApplicationContext(), "O dispositivo não tem bluetooth", Toast.LENGTH_LONG).show();
        } else if (!btfAdapter.isEnabled()) {
            Intent it = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(it, SOLICITA_ATIVACAO);
        }

        btnConectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (conexao) {
                    //desconectar
                    try {
                        btfSocket.close();
                        conexao = false;
                        btnConectar.setText("Conectar");
                        Toast.makeText(getApplicationContext(), "Bluetooth desconectado", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "Ocorreu um erro: " + e, Toast.LENGTH_LONG).show();
                    }
                } else {
                    //conectar
                    Intent it = new Intent(ConfigActivity.this, ListaDispositivos.class);
                    startActivityForResult(it, SOLICITA_CONEXAO);
                }
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (msg.what == MESSAGE_READ) {
                    //recebe dados
                    String recebidos = (String) msg.obj;
                    //reune os dados
                    dadosBluetooth.append(recebidos);

                    int fimInfo = dadosBluetooth.indexOf("}");

                    if (fimInfo > 0) {
                        String dadosCompletos = dadosBluetooth.substring(0, fimInfo);

                        int tamInfo = dadosCompletos.length();
                        //Chegou aqui e porque a informação veio correta
                        if (dadosBluetooth.charAt(0) == '{') {
                            String dadosFinal = dadosBluetooth.substring(1, tamInfo);
                            Log.d("Recebidos", dadosFinal);

                            /*if(dadosFinal.contains("dados")){
                                tvTal.setText("INformação");
                            }*/
                        }
                        dadosBluetooth.delete(0, dadosBluetooth.length());
                    }
                }

            }

        };
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
                    btfDevice = btfAdapter.getRemoteDevice(MAC);
                    try {
                        btfSocket = btfDevice.createRfcommSocketToServiceRecord(btf_UUID);
                        btfSocket.connect();

                        conexao = true;

                        connectedThread = new ConnectedThread(btfSocket);
                        connectedThread.start();

                        btnConectar.setText("Desconectar");

                        Toast.makeText(getApplicationContext(), "Conecado com: " + MAC, Toast.LENGTH_LONG).show();
                        Log.d("Passou", MAC);
                        try {
                            new ObdResetCommand().run(btfSocket.getInputStream(), btfSocket.getOutputStream());
                            Log.d("Passou 1", "OBDRESETCOMMAND");
                            new EchoOffCommand().run(btfSocket.getInputStream(), btfSocket.getOutputStream());
                            Log.d("Passou 2", "ECHOOFFCOMMAND");
                            new LineFeedOffCommand().run(btfSocket.getInputStream(), btfSocket.getOutputStream());
                            Log.d("Passou 3", "LINEFEEDOFFCOMMAND");
                            new TimeoutCommand(125).run(btfSocket.getInputStream(), btfSocket.getOutputStream());
                            Log.d("Passou 4", "TIMEOUTCOMMAND");
                            new SelectProtocolCommand(ObdProtocols.AUTO).run(btfSocket.getInputStream(), btfSocket.getOutputStream());
                            Log.d("Passou 5", "SELECTPROTOCOLCOMMAND");
//                            new AmbientAirTemperatureCommand().run(btfSocket.getInputStream(), btfSocket.getOutputStream());
//                            Log.d("Passou 6", "AMBIENTAIRTEMPERATURE");
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            RPMCommand engineRpmCommand = new RPMCommand();
                            SpeedCommand speedCommand = new SpeedCommand();
                            while (!Thread.currentThread().isInterrupted()) {
                                engineRpmCommand.run(btfSocket.getInputStream(), btfSocket.getOutputStream());
                                speedCommand.run(btfSocket.getInputStream(), btfSocket.getOutputStream());
                                Log.d("Passou 6", "RPM: " + engineRpmCommand.getFormattedResult());
                                Log.d("Passou 7", "Speed: " + speedCommand.getFormattedResult());
//                                tvTeste.setText(engineRpmCommand.getFormattedResult());
//                                lista.addView(tvTeste);
//                                novaTableRow(engineRpmCommand.getName(), engineRpmCommand.getFormattedResult());
//                                novaTableRow(speedCommand.getName(), speedCommand.getFormattedResult());
//                                buscaDados();
                                for (ObdCommand command : ComandosObd.getComandos()) {
                                    novaTableRow(command.getName(), command.getFormattedResult());
                                }
                                adapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            // handle errors
                        }
                    } catch (IOException e) {
                        conexao = false;
                        Toast.makeText(getApplicationContext(), "Ocorreu um erro: " + e, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Falha ao obter MAC!", Toast.LENGTH_LONG).show();
                }
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

        new Thread() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (ObdCommand command : ComandosObd.getComandos()) {
                            novaTableRow(command.getName(), command.getFormattedResult());
                        }
                        pd.setMessage("Dados Coletados");
                        pd.dismiss();
                    }
                });
            }
        }.start();

    }

    public void Verifica(View v) {
        intent = new Intent(this, BluetoothCheckActivity.class);
        startActivityForResult(intent, 0);
    }

    public void Pareados(View v) {
        intent = new Intent(this, ListaPareadosActivity.class);
        startActivityForResult(intent, 1);
    }

    public void Buscar(View v) {
        intent = new Intent(this, BuscarDispositivosActivity.class);
        startActivityForResult(intent, 2);
    }

    public void Server(View v) {
        intent = new Intent(this, ReceberMensagemActivity.class);
        startActivityForResult(intent, 5);
    }

    public void Cliente(View v) {
        intent = new Intent(this, BluetoothClienteActivity.class);
        startActivityForResult(intent, 6);
    }

    public void Sair(View v) {
        finishAffinity();
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

//        public void run() {
//            byte[] buffer = new byte[1024];  // buffer store for the stream
//            int bytes; // bytes returned from read()
//
//            // Keep listening to the InputStream until an exception occurs
//            while (true) {
//                try {
//                    // Read from the InputStream
//                    bytes = mmInStream.read(buffer);
//
//                    String dadosBtf = new String(buffer, 0, bytes);
//                    Log.d("InStream", dadosBtf);
//                    // Send the obtained bytes to the UI activity
////                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, dadosBtf).sendToTarget();
//
//                } catch (IOException e) {
//                    break;
//                }
//            }
//        }

//        /* Call this from the main activity to send data to the remote device */
//        public void write(byte[] bytes) {
//            try {
//                mmOutStream.write(bytes);
//            } catch (IOException e) { }
//        }
//        /* Call this from the main activity to shutdown the connection */
//        public void cancel() {
//            try {
//                btfSocket.close();
//            } catch (IOException e) {
//            }
//        }
    }


//    //Thread para controlar a conexao e nao travar a tela
//    public class ThreadServidor extends Thread {
//        private String TAG = "Control car";
//
//        @Override
//        public void run() {
//            super.run();
//            try {
//                //Abre o socket servidor (quem for conectar precisa utilizar o mesmo UUID)
//                BluetoothServerSocket serverSocket = btfAdapter.listenUsingRfcommWithServiceRecord("Bluetooth", btf_UUID);
//                //Fica aguardando alguem conectar (Chamada bloqueante)
//                try {
//                    //Aguarda conexoes
//                    btfSocket = serverSocket.accept();
//                } catch (Exception e) {
//                    //Em caso de erro encerrar aqui
//                    return;
//                }
//
//                if (btfSocket != null) {
//                    //Alguem conectou entao encerra-se o socket server
//                    serverSocket.close();
//                    //O socket possui a InputStream e OutputStram para ler e escrever
//                    InputStream in = btfSocket.getInputStream();
//                    //Recupera o dispositivo cliente que fez a conexao
//                    btfDevice = btfSocket.getRemoteDevice();
//                    updateViewConectou(btfDevice);
//                    byte[] bytes = new byte[1024];
//                    int length;
//                    //Fica em loop para receber as mensagens
//                    while (running) {
//                        //Le a mensagem (chamada bloqueada até alguem escrever)
//                        length = in.read(bytes);
//                        String mensagemRecebida = new String(bytes, 0, length);
//                        TextView tvMsg = (TextView) findViewById(R.id.tvMsg);
//                        final String s = tvMsg.getText().toString() + mensagemRecebida + "\n";
//                        updateViewMensagem(s);
//                    }
//                }
//            } catch (IOException e) {
//                Log.e(TAG, "Erro no servidor: " + e.getMessage(), e);
//                running = false;
//            }
//        }
//    }
//
//    //Exibe a mensagem na tela, informando o nome do dispositivo que fez a conexao
//    private void updateViewConectou(final BluetoothDevice device) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                TextView tvNome = (TextView) findViewById(R.id.tvNomeDispositivo);
//                tvNome.setText(device.getName() + " - " + device.getAddress());
//            }
//        });
//    }
//
//    //Exibe a mensagem recebida na tela (Enviada pelo outro dispositivo)
//    private void updateViewMensagem(final String s) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                TextView tvMsg = (TextView) findViewById(R.id.tvMsg);
//                tvMsg.setText(s);
//            }
//        });
//    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        running = false;
//        try {
//            if (btfSocket != null) {
//                btfSocket.close();
//            }
//            if (serverSocket != null) {
//                serverSocket.close();
//            }
//        } catch (IOException e) {
//
//        }
//    }

}