package com.example.lucas.controlcar.config;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.example.lucas.controlcar.obd.ComandosObd;
import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by lucas on 25/11/17.
 */

public class ThreadConexaoOBD extends Thread {

    private BluetoothDevice btDevice = null;
    private BluetoothSocket btSocket = null;

    public static final UUID bt_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    protected boolean stop = false;
    protected ArrayList<String> comandos = null;
    protected int atualiza = 4000;

    public ThreadConexaoOBD(BluetoothDevice btDevice) {
        this.btDevice = btDevice;
//        this.atualiza = atualiza;
    }

    protected void conectaDispositivo() throws IOException, InterruptedException {
        btSocket = this.btDevice.createRfcommSocketToServiceRecord(bt_UUID);
        btSocket.connect();

        while (!stop) {
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
        }
    }

    public void run(){
        try{
            conectaDispositivo();
            for (ObdCommand command : ComandosObd.getComandos()) {
                comandos.add(command.getFormattedResult());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void cancel(){
        stop = true;
    }

    public void close(){
        try {
            stop = true;
            btSocket.close();
        } catch (Exception e) {
        }
    }

}



