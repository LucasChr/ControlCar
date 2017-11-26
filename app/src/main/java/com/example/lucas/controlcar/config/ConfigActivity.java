package com.example.lucas.controlcar.config;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.example.lucas.controlcar.R;
import com.github.pires.obd.enums.ObdProtocols;

import java.util.ArrayList;
import java.util.Set;


public class ConfigActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    public static final String LISTA_DEV_BLUETOOTH = "lista_dev_bluetooth_preference";
    public static final String PERIODO_ATUALIZACAO_KEY = "periodo_atualizacao_obd_preference";
    public static final String LISTA_PROTOCOLOS_KEY = "lista_protocolos_preference";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        ArrayList<CharSequence> dispositivosPareadosStrings = new ArrayList<CharSequence>();
        ArrayList<CharSequence> values = new ArrayList<CharSequence>();
        ListPreference listaDevsBluetooth = (ListPreference) getPreferenceScreen().findPreference(LISTA_DEV_BLUETOOTH);
        String[] prefKeys = new String[]{PERIODO_ATUALIZACAO_KEY};
        ArrayList<CharSequence> protocolosStrings = new ArrayList<>();
        ListPreference listaProtocolos = (ListPreference) getPreferenceScreen()
                .findPreference(LISTA_PROTOCOLOS_KEY);
        for (String prefKey : prefKeys) {
            EditTextPreference txtPref = (EditTextPreference) getPreferenceScreen().findPreference(prefKey);
            txtPref.setOnPreferenceChangeListener(this);
        }
        for (ObdProtocols protocol : ObdProtocols.values()) {
            protocolosStrings.add(protocol.name());
        }
        listaProtocolos.setEntries(protocolosStrings.toArray(new CharSequence[0]));
        listaProtocolos.setEntryValues(protocolosStrings.toArray(new CharSequence[0]));

        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            listaDevsBluetooth.setEntries(dispositivosPareadosStrings.toArray(new CharSequence[0]));
            listaDevsBluetooth.setEntryValues(values.toArray(new CharSequence[0]));
            return;
        }
        final Activity thisAtivity = this;
        listaDevsBluetooth.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @SuppressWarnings("unused")
            public boolean onPreferenceClick(Preference preference) {
                if (btAdapter == null) {
                    Toast.makeText(thisAtivity, "Este dispositivo não suporta Bluetooth", Toast.LENGTH_LONG);
                    return false;
                }
                return true;
            }
        });
        Set<BluetoothDevice> dispositivosPareados = btAdapter.getBondedDevices();
        if (dispositivosPareados.size() > 0) {
            for (BluetoothDevice device : dispositivosPareados) {
                dispositivosPareadosStrings.add(device.getName() + "\n" + device.getAddress());
                values.add(device.getAddress());
            }
        }


        listaDevsBluetooth.setEntries(dispositivosPareadosStrings.toArray(new CharSequence[0]));
        listaDevsBluetooth.setEntryValues(values.toArray(new CharSequence[0]));
    }

    public boolean onPreferenceChange(Preference preference, Object valor) {
        if (PERIODO_ATUALIZACAO_KEY.equals(preference.getKey())) {
            try {
                Double.parseDouble(valor.toString());
                return true;
            } catch (Exception e) {
                Toast.makeText(this, "Não foi possível mudar o tempo para: " + valor.toString() + "/n Verifique se é um número.", Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

    public static int getPeriodoAtualizacao(SharedPreferences prefs) {
        String periodString = prefs.getString(ConfigActivity.PERIODO_ATUALIZACAO_KEY, "4");
        int period = 4000;
        try {
            period = Integer.parseInt(periodString) * 1000;
        } catch (Exception e) {
        }
        if (period <= 0) {
            period = 250;
        }
        return period;
    }
}
//    private Intent intent;
//
//    Button btnConectar;
//
//    TextView tvTeste;
//    private static final int SOLICITA_ATIVACAO = 1;
//    private static final int SOLICITA_CONEXAO = 2;
//    private static final int MESSAGE_READ = 3;
//
//    ConnectedThread connectedThread;
//    ArrayAdapter adapter;
//    TableLayout lista;
//    Handler mHandler;
//    StringBuilder dadosBluetooth = new StringBuilder();
//
//    BluetoothAdapter btfAdapter = null;
//    BluetoothDevice btfDevice = null;
//    BluetoothSocket btfSocket = null;
//
//    boolean conexao = false;
//
//    private static String MAC = null;
//
//    UUID btf_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_configuracoes);
//
//        btnConectar = (Button) findViewById(R.id.btnConectar);
//        tvTeste = (TextView) findViewById(R.id.tvTeste);
//        lista = (TableLayout) findViewById(R.id.lista);
//
//        btfAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        if (btfAdapter == null) {
//            Toast.makeText(getApplicationContext(), "O dispositivo não tem bluetooth", Toast.LENGTH_LONG).show();
//        } else if (!btfAdapter.isEnabled()) {
//            Intent it = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(it, SOLICITA_ATIVACAO);
//        }
//
//        btnConectar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (conexao) {
//                    //desconectar
//                    try {
//                        btfSocket.close();
//                        conexao = false;
//                        btnConectar.setText("Conectar");
//                        Toast.makeText(getApplicationContext(), "Bluetooth desconectado", Toast.LENGTH_LONG).show();
//                    } catch (IOException e) {
//                        Toast.makeText(getApplicationContext(), "Ocorreu um erro: " + e, Toast.LENGTH_LONG).show();
//                    }
//                } else {
//                    //conectar
//                    Intent it = new Intent(ConfigActivity.this, ListaDispositivos.class);
//                    startActivityForResult(it, SOLICITA_CONEXAO);
//                }
//            }
//        });
//
//        mHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//
//                if (msg.what == MESSAGE_READ) {
//                    //recebe dados
//                    String recebidos = (String) msg.obj;
//                    //reune os dados
//                    dadosBluetooth.append(recebidos);
//
//                    int fimInfo = dadosBluetooth.indexOf("}");
//
//                    if (fimInfo > 0) {
//                        String dadosCompletos = dadosBluetooth.substring(0, fimInfo);
//
//                        int tamInfo = dadosCompletos.length();
//                        //Chegou aqui e porque a informação veio correta
//                        if (dadosBluetooth.charAt(0) == '{') {
//                            String dadosFinal = dadosBluetooth.substring(1, tamInfo);
//                            Log.d("Recebidos", dadosFinal);
//
//                            /*if(dadosFinal.contains("dados")){
//                                tvTal.setText("INformação");
//                            }*/
//                        }
//                        dadosBluetooth.delete(0, dadosBluetooth.length());
//                    }
//                }
//
//            }
//
//        };
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case SOLICITA_ATIVACAO:
//                if (resultCode == Activity.RESULT_OK) {
//                    Toast.makeText(getApplicationContext(), "Bluetooth ativado!", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(getApplicationContext(), "Bluetooth não ativado neste aparelho", Toast.LENGTH_LONG).show();
//                }
//                break;
//            case SOLICITA_CONEXAO:
//                if (resultCode == Activity.RESULT_OK) {
//                    MAC = data.getExtras().getString(ListaDispositivos.ENDERECO_MAC);
//                    btfDevice = btfAdapter.getRemoteDevice(MAC);
//                    try {
//                        btfSocket = btfDevice.createRfcommSocketToServiceRecord(btf_UUID);
//                        btfSocket.connect();
//
//                        conexao = true;
//
//                        connectedThread = new ConnectedThread(btfSocket);
//                        connectedThread.start();
//
//                        btnConectar.setText("Desconectar");
//
//                        Toast.makeText(getApplicationContext(), "Conecado com: " + MAC, Toast.LENGTH_LONG).show();
//                        Log.d("Passou", MAC);
//                        try {
//                            new ObdResetCommand().run(btfSocket.getInputStream(), btfSocket.getOutputStream());
//                            Log.d("Passou 1", "OBDRESETCOMMAND");
//                            new EchoOffCommand().run(btfSocket.getInputStream(), btfSocket.getOutputStream());
//                            Log.d("Passou 2", "ECHOOFFCOMMAND");
//                            new LineFeedOffCommand().run(btfSocket.getInputStream(), btfSocket.getOutputStream());
//                            Log.d("Passou 3", "LINEFEEDOFFCOMMAND");
//                            new TimeoutCommand(125).run(btfSocket.getInputStream(), btfSocket.getOutputStream());
//                            Log.d("Passou 4", "TIMEOUTCOMMAND");
//                            new SelectProtocolCommand(ObdProtocols.AUTO).run(btfSocket.getInputStream(), btfSocket.getOutputStream());
//                            Log.d("Passou 5", "SELECTPROTOCOLCOMMAND");
////                            new AmbientAirTemperatureCommand().run(btfSocket.getInputStream(), btfSocket.getOutputStream());
////                            Log.d("Passou 6", "AMBIENTAIRTEMPERATURE");
//                            try {
//                                Thread.sleep(500);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            RPMCommand engineRpmCommand = new RPMCommand();
//                            SpeedCommand speedCommand = new SpeedCommand();
//                            while (!Thread.currentThread().isInterrupted()) {
//                                engineRpmCommand.run(btfSocket.getInputStream(), btfSocket.getOutputStream());
//                                speedCommand.run(btfSocket.getInputStream(), btfSocket.getOutputStream());
//                                Log.d("Passou 6", "RPM: " + engineRpmCommand.getFormattedResult());
//                                Log.d("Passou 7", "Speed: " + speedCommand.getFormattedResult());
////                                tvTeste.setText(engineRpmCommand.getFormattedResult());
////                                lista.addView(tvTeste);
////                                novaTableRow(engineRpmCommand.getName(), engineRpmCommand.getFormattedResult());
////                                novaTableRow(speedCommand.getName(), speedCommand.getFormattedResult());
////                                buscaDados();
//                                for (ObdCommand command : ComandosObd.getComandos()) {
//                                    novaTableRow(command.getName(), command.getFormattedResult());
//                                }
//                                adapter.notifyDataSetChanged();
//                            }
//                        } catch (Exception e) {
//                            // handle errors
//                        }
//                    } catch (IOException e) {
//                        conexao = false;
//                        Toast.makeText(getApplicationContext(), "Ocorreu um erro: " + e, Toast.LENGTH_LONG).show();
//                    }
//                } else {
//                    Toast.makeText(getApplicationContext(), "Falha ao obter MAC!", Toast.LENGTH_LONG).show();
//                }
//        }
//    }
//
//    private void novaTableRow(String comando, String resultado) {
//        TableRow tr = new TableRow(this);
//        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        tr.setLayoutParams(params);
//
//        TextView name = new TextView(this);
//        name.setGravity(Gravity.RIGHT);
//        name.setText(comando + ": ");
//        TextView value = new TextView(this);
//        value.setGravity(Gravity.LEFT);
//        value.setText(resultado);
//        tr.addView(name);
//        tr.addView(value);
//        lista.addView(tr, params);
//    }
//
//    public void buscaDados() {
//        final ProgressDialog pd = new ProgressDialog(this);
//        pd.setMessage("Buscando dados da Ecu");
//        pd.show();
//
//        new Thread() {
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        for (ObdCommand command : ComandosObd.getComandos()) {
//                            novaTableRow(command.getName(), command.getFormattedResult());
//                        }
//                        pd.setMessage("Dados Coletados");
//                        pd.dismiss();
//                    }
//                });
//            }
//        }.start();
//
//    }
//
//    public void Verifica(View v) {
//        intent = new Intent(this, BluetoothCheckActivity.class);
//        startActivityForResult(intent, 0);
//    }
//
//    public void Pareados(View v) {
//        intent = new Intent(this, ListaPareadosActivity.class);
//        startActivityForResult(intent, 1);
//    }
//
//    public void Buscar(View v) {
//        intent = new Intent(this, BuscarDispositivosActivity.class);
//        startActivityForResult(intent, 2);
//    }
//
//    public void Server(View v) {
//        intent = new Intent(this, ReceberMensagemActivity.class);
//        startActivityForResult(intent, 5);
//    }
//
//    public void Cliente(View v) {
//        intent = new Intent(this, BluetoothClienteActivity.class);
//        startActivityForResult(intent, 6);
//    }
//
//    public void Sair(View v) {
//        finishAffinity();
//    }
//
//    private class ConnectedThread extends Thread {
//        private final InputStream mmInStream;
//        private final OutputStream mmOutStream;
//
//        public ConnectedThread(BluetoothSocket socket) {
//            InputStream tmpIn = null;
//            OutputStream tmpOut = null;
//
//            // Get the input and output streams, using temp objects because
//            // member streams are final
//            try {
//                tmpIn = socket.getInputStream();
//                tmpOut = socket.getOutputStream();
//            } catch (IOException e) {
//            }
//
//            mmInStream = tmpIn;
//            mmOutStream = tmpOut;
//        }