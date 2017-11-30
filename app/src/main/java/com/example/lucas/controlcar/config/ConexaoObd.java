package com.example.lucas.controlcar.config;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Mesa on 15/11/2017.
 */

public class ConexaoObd implements IObdConexao {
    private static final String TAG = "Conexao OBD";
    private BluetoothSocket btSocket = null;
    private BluetoothAdapter btAdapter = null;
    final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private boolean conectado = false;

    public void iniciaConexaoObd() throws Exception {
        Log.d(TAG, "Iniciando conexão ODB");

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null || !btAdapter.isEnabled()) {
            throw new Exception("Bluetooth desabilitado");
        }
        String mac = "00:00:00:00:00:01";

        BluetoothDevice dev = btAdapter.getRemoteDevice(mac);
        try {
            btSocket = dev.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            throw new Exception("Não foi possivel criar o socket");
        }

        try {
            Log.d(TAG, "Conectando");
            btSocket.connect();
            Log.d(TAG, "Conectado");
        } catch (IOException e) {
            throw new Exception("OBDII fora de alcance");
        }

        enviaComandoObd("AT Z"); // reset
        enviaComandoObd("AT E0"); // echo off
        enviaComandoObd("AT L0"); // linefeed off
        enviaComandoObd("AT ST 62"); // tempo de resposta em milisegundos
        enviaComandoObd("AT SP 0"); // seleciona protocolo automatico
        enviaComandoObd("01 05"); // auto-descoberta

        enviaComandoObd("AT RV"); // get voltagem
        enviaComandoObd("01 05"); // get temperatura arrefecimento

        conectado = true;
    }

    public void pararConexaoObd() {
        conectado = false;
        if (btSocket != null) {
            try {
                btSocket.close();
                btSocket = null;
            } catch (IOException e) {
                // ignore
            }
        }

    }

    public void atualizaDados(DadosVeiculo data) {
        data.m_VelocidadeVeiculo = data.m_VelocidadeVeiculo * 0.6 + 0.4
                * getVoltagemVeiculo();

        double rpmmotor = data.m_RpmMotor * 0.6 + 0.4 * getRpmMotor();
        double sampletime = System.currentTimeMillis() / 1000.0;

        data.m_RateRpmMotor = (rpmmotor - data.m_RpmMotor)
                / (sampletime - data.m_LastSampleTime);

        data.m_RpmMotor = rpmmotor;
        data.m_LastSampleTime = sampletime;

        // data.m_CoolingTemperature = getCoolingTemperature();
        // data.m_IntakePressure = getIntakePressure();
        // data.m_IntakeTemperature = getIntakeTemperature();
        // data.m_MafRate = getMafRate();
        // data.m_RailPressure = getRailPressure();
    }

    private String enviaComandoObd(String cmd) throws IOException {
        InputStream inStream = btSocket.getInputStream();
        OutputStream outStream = btSocket.getOutputStream();

        String cmdcr = cmd + '\r';
        outStream.write(cmdcr.getBytes());
        outStream.flush();

        String ans = "";

        while (true) {
            char b = (char) (inStream.read());
            if (b == '>')
                break;

            ans += b;
        }
        return ans;
    }

    private double getCargaMotor() {
        String dado;
        double load = 0;

        try {
            if (conectado)
                dado = enviaComandoObd("01 04");
                // return is 41 04 XX, load is XX*100/255 %
            else
                dado = "41 04 82\r\r";

            dado = dado.substring(6, 6 + 2);
            load = Integer.parseInt(dado, 16) * 100.0 / 255.0;
        } catch (IOException e) {
            load = 0;
        }

        return load;
    }

    // private double getCoolingTemperature() {
    // String ans;
    // double temperature = 0;
    //
    // try {
    // if (conectado)
    // ans = enviaComandoObd("01 05");
    // // return is 41 05 XX, temp is XX-40 celcius
    // else
    // ans = "41 05 68\r\r";
    //
    // ans = ans.substring(6, 6 + 2);
    // temperature = Integer.parseInt(ans, 16) - 40;
    // } catch (IOException e) {
    // temperature = 0;
    // }
    //
    // return temperature;
    // }
    //


    // private double getIntakePressure() {
    // String ans;
    // double pressure = 0;
    //
    // try {
    // if (conectado)
    // ans = enviaComandoObd("01 0B");
    // // return is 41 0B XX, pressure is XX kPa
    // else
    // ans = "41 0B 72\r\r";
    //
    // ans = ans.substring(6, 6 + 2);
    // pressure = Integer.parseInt(ans, 16);
    // } catch (IOException e) {
    // pressure = 0;
    // }
    //
    // return pressure;
    // }
    //

    private double getRpmMotor() {
        String dado, aux;
        double rpm = 0;

        try {
            if (conectado)
                dado = enviaComandoObd("01 0C");
                // return is 41 0C XX YY, rpm is (XX*256 + YY)/4 rpm
            else
                dado = "41 0C 22 20\r\r";

            aux = dado.substring(6, 6 + 2);
            rpm = Integer.parseInt(aux, 16) * 256;

            aux = dado.substring(9, 9 + 2);
            rpm += Integer.parseInt(aux, 16);

            rpm /= 4;
        } catch (IOException e) {
            rpm = 0;
        }

        return rpm;
    }

    private double getVelocidadeVeiculo() {
        String dado;
        double velocidade = 0;

        try {
            if (conectado)
                dado = enviaComandoObd("01 0D");
                // return is 41 0D XX, velocidade is XX km/h
            else
                dado = "41 0D 10\r\r";

            dado = dado.substring(6, 6 + 2);
            velocidade = Integer.parseInt(dado, 16);
        } catch (IOException e) {
            velocidade = 0;
        }

        return velocidade;
    }

    private double getVoltagemVeiculo() {
        String dado;
        double voltagem = 0;

        try {
            if (conectado)
                dado = enviaComandoObd("01 42");
                // return is 41 0D XX, velocidade is XX km/h
            else
                dado = "41 0D 10\r\r";

            dado = dado.substring(6, 6 + 2);
            voltagem = Integer.parseInt(dado, 16);
        } catch (IOException e) {
            voltagem = 0;
        }

        return voltagem;
    }

    // private double getIntakeTemperature() {
    // String ans;
    // double temperature = 0;
    //
    // try {
    // if (conectado)
    // ans = enviaComandoObd("01 0F");
    // // return is 41 0F XX, temp is XX-40 celcius
    // else
    // ans = "41 0F 72\r\r";
    //
    // ans = ans.substring(6, 6 + 2);
    // temperature = Integer.parseInt(ans, 16) - 40;
    // } catch (IOException e) {
    // temperature = 0;
    // }
    //
    // return temperature;
    // }

    // private double getMafRate() {
    // String ans, anst;
    // double rate = 0;
    //
    // try {
    // if (conectado)
    // ans = enviaComandoObd("01 10");
    // // return is 41 10 XX YY, rate = (XX*256+YY) / 100 g/s
    // else
    // ans = "41 10 10 20\r\r";
    //
    // anst = ans.substring(6, 6 + 2);
    // rate = Integer.parseInt(anst, 16) * 256;
    //
    // anst = ans.substring(9, 9 + 2);
    // rate += Integer.parseInt(anst, 16);
    //
    // rate /= 100;
    // } catch (IOException e) {
    // rate = 0;
    // }
    //
    // return rate;
    // }

    // private double getRailPressure() {
    // String ans, anst;
    // double pressure = 0;
    //
    // try {
    // if (conectado)
    // ans = enviaComandoObd("01 23");
    // // return is 41 23 XX YY , pressure = (XX*256 + YY) * 10 kPa
    // else
    // ans = "41 23 10 20\r\r";
    //
    // anst = ans.substring(6, 6 + 2);
    // pressure = Integer.parseInt(anst, 16) * 256;
    //
    // anst = ans.substring(9, 9 + 2);
    // pressure += Integer.parseInt(anst, 16);
    //
    // pressure *= 10;
    // } catch (IOException e) {
    // pressure = 0;
    // }
    //
    // return pressure;
    // }
}
