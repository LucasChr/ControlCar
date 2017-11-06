package com.example.lucas.controlcar.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;

import java.io.IOException;

/**
 * Created by lucas on 26/10/17.
 */

public class BluetoothGerenciaConexao {

    private static final String TAG = BluetoothGerenciaConexao.class.getName();
    private BluetoothDevice btDevice = null;
    private BluetoothSocket btSocket = null;
    protected boolean running;
    protected Long fila = 0L;

    private void iniciaConexaoOBD() throws IOException {
        Log.d(TAG, "Iniciando conexao com OBD");
        running = true;

        try {
            btSocket = BluetoothConexao.conecta(btDevice);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao estabilizar a conexao com bluetooth");
            throw new IOException();
        }

        //Below is to give the adapter enough time to reset before sending the commands, otherwise the first startup commands could be ignored.
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        fila = 0L;
        Log.d(TAG, "Iniciando Fila");

    }

}
