package com.example.lucas.controlcar.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.lucas.controlcar.R;

public class BuscaEnderecoActivity extends AppCompatActivity {
    private BluetoothAdapter btfAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_endereco);
        //Bluetooth adapter
        btfAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btfAdapter != null && btfAdapter.isEnabled()) {
            BluetoothDevice device = btfAdapter.getRemoteDevice("00:00:00:00:00:01");

            String nome = device.getName();
            String endereco = device.getAddress();
            String msg = nome + " - " + endereco;
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }
}
