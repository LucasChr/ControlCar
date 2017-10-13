package com.example.lucas.controlcar.configuracoes;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.lucas.controlcar.R;
import com.example.lucas.controlcar.bluetooth.BluetoothCheckActivity;
import com.example.lucas.controlcar.bluetooth.BluetoothClienteActivity;
import com.example.lucas.controlcar.bluetooth.BuscaEnderecoActivity;
import com.example.lucas.controlcar.bluetooth.BuscarDispositivosActivity;
import com.example.lucas.controlcar.bluetooth.ListaDispositivos;
import com.example.lucas.controlcar.bluetooth.ListaPareadosActivity;
import com.example.lucas.controlcar.bluetooth.ReceberMensagemActivity;

import java.io.IOException;
import java.util.UUID;

public class ConfiguracoesActivity extends AppCompatActivity {

    Button btnConectar;
    BluetoothAdapter btfAdapter = null;
    BluetoothDevice btfDevice = null;
    BluetoothSocket btfSocket = null;
    boolean conexao = false;
    private static final int SOLICITA_ATIVACAO = 1;
    private static final int SOLICITA_CONEXAO = 2;
    private static String MAC = null;
    UUID btf_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        btnConectar = (Button) findViewById(R.id.btnConectar);

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
                    Intent it = new Intent(ConfiguracoesActivity.this, ListaDispositivos.class);
                    startActivityForResult(it, SOLICITA_CONEXAO);
                }
            }
        });
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
                        btnConectar.setText("Desconectar");
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

}