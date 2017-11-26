package com.example.lucas.controlcar.relatorio;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.controlcar.R;
import com.example.lucas.controlcar.config.ConfigActivity;
import com.example.lucas.controlcar.config.ServicoObd;
import com.example.lucas.controlcar.config.ServicoObdConexao;
import com.example.lucas.controlcar.obd.ComandosObd;
import com.github.pires.obd.commands.ObdCommand;
import com.google.inject.Inject;

import java.io.IOException;
import java.util.ArrayList;


public class RelatorioActivity extends AppCompatActivity {
    private static final String TAG = "Relatorio Activity";
    private static final int SOLICITA_ATIVACAO = 1;
    private static final int INICIA_CONEXAO = 2;
    private static final int INICIA_LIVE_DATA = 3;
    private static final int PARA_LIVE_DATA = 4;
    private static final int CONFIGURACOES = 5;
    private static String MAC = ServicoObd.MAC;

    private BluetoothSocket btSocket = null;
    private BluetoothDevice btDevice = null;
    private BluetoothAdapter btAdapter = null;

    private Button btnConectar = null;
    private TableLayout lista = null;

    private boolean conexao = false;

    private Handler handler = null;
    private Intent serviceIntent = null;
    private ServicoObdConexao serviceConn = null;
    private AtualizaThread atualiza = null;

    @Inject
    private PowerManager powerManager;

    private PowerManager.WakeLock wakeLock = null;
    @Inject
    private SharedPreferences pref = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_relatorio);

        btnConectar = (Button) findViewById(R.id.relatorio_cad_btnConectar);
        lista = (TableLayout) findViewById(R.id.relatorio_lista);

        handler = new Handler();
        serviceIntent = new Intent(this, ServicoObd.class);
        serviceConn = new ServicoObdConexao();
        bindService(serviceIntent, serviceConn, Context.BIND_AUTO_CREATE);

        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
//            showDialog(NO_BLUETOOTH_ID);
            return;
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
            case INICIA_CONEXAO:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Log.d("Dados", "Antes");
                        dadosTReal();
                        Log.d("Dados", "Depois");
                        conexao = true;
                        btnConectar.setText("Desconectar");
                        Toast.makeText(getApplicationContext(), "Conecado com: " + MAC, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        conexao = false;
                        Toast.makeText(getApplicationContext(), "Ocorreu um erro: " + e, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Falha ao obter MAC!", Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case INICIA_LIVE_DATA:
                dadosTReal();
                return true;
            case PARA_LIVE_DATA:
                cancel();
                return true;
            case CONFIGURACOES:
                atualizaConfiguracoes();
                return true;
        }
        return false;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem iniciaItem = menu.findItem(INICIA_LIVE_DATA);
        MenuItem paraItem = menu.findItem(PARA_LIVE_DATA);
        MenuItem configuracoesItem = menu.findItem(CONFIGURACOES);
        if (serviceConn.isRunning()) {
            iniciaItem.setEnabled(false);
            paraItem.setEnabled(true);
            configuracoesItem.setEnabled(false);
        } else {
            paraItem.setEnabled(false);
            iniciaItem.setEnabled(true);
            configuracoesItem.setEnabled(true);
        }
        return true;
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
            iniciaConexao();
        }
    }

    private void iniciaConexao() {
        try {
            Log.d("Dados", "Antes");
            dadosTReal();
            Log.d("Dados", "Depois");
            conexao = true;
            btnConectar.setText("Desconectar");
            Toast.makeText(getApplicationContext(), "Conecado com: " + MAC, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            conexao = false;
            Toast.makeText(getApplicationContext(), "Ocorreu um erro: " + e, Toast.LENGTH_LONG).show();
        }
    }

    private void dadosTReal() {
        Log.d("Dados", "Durante");
        if (!serviceConn.isRunning()) {
            serviceConn.getService().startService();
        }
        atualiza = new AtualizaThread();
        atualiza.start();
        wakeLock.acquire();
        Log.d("Dados", "Termina");
    }

    private void cancel() {
        stopService(serviceIntent);
        if (serviceConn.isRunning()) {
            serviceConn.getService().startService();
        }
        if (atualiza != null) {
            atualiza.stop = true;
        }
        wakeLock.release();
    }

    public void atualizaDadosTabela(final ArrayList<String> array) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                setDadosTabela(array);
            }
        });
    }

    public void atualizaTextView(final TextView view, final String txt) {
        handler.post(new Runnable() {
            public void run() {
                view.setText(txt);
            }
        });
    }

    public void setDadosTabela(ArrayList<String> array) {
//        TableLayout tl = (TableLayout) findViewById(R.id.data_table);
//        tl.removeAllViews();
//        Set<String> keySet = dataMap.keySet();
//        String[] keys = keySet.toArray(new String[0]);
//        Arrays.sort(keys);
//        for (String k:keys) {
//            addTableRow(tl,k,dataMap.get(k));
//        }
        lista.removeAllViews();
        TableRow tr = new TableRow(this);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tr.setLayoutParams(params);

        for (String resultado : array) {
            for (ObdCommand c : ComandosObd.getComandos()) {
                TextView name = new TextView(this);
                name.setGravity(Gravity.RIGHT);
                name.setText(c + ": ");
                TextView value = new TextView(this);
                value.setGravity(Gravity.LEFT);
                value.setText(resultado);
                tr.addView(name);
                tr.addView(value);
                lista.addView(tr, params);
            }
        }
    }

    private void atualizaConfiguracoes() {
        Intent configIntent = new Intent(this, ConfigActivity.class);
        startActivity(configIntent);
    }

    private class AtualizaThread extends Thread {
        boolean stop = false;

        public void run() {
            while (!stop && serviceConn.isRunning()) {
                ServicoObd svc = serviceConn.getService();
                ArrayList<String> dados = null;
                atualizaDadosTabela(dados);
                try {
                    Thread.sleep(ConfigActivity.getPeriodoAtualizacao(pref));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}