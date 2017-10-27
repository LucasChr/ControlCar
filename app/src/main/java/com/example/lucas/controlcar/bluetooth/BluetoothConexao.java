package com.example.lucas.controlcar.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by lucas on 26/10/17.
 */

public class BluetoothConexao {

    private static final String TAG = BluetoothConexao.class.getName();
    private static final UUID bt_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static BluetoothSocket conecta(BluetoothDevice device) throws IOException {
        BluetoothSocket btSocket = null;

        Log.d(TAG, "Conexao BT");
        try {
            btSocket = device.createRfcommSocketToServiceRecord(bt_UUID);
            btSocket.connect();
        } catch (Exception e) {

        }
        return btSocket;
    }
}
