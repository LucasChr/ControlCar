package com.example.lucas.controlcar.relatorio;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lucas.controlcar.R;

public class RelatorioCadActivity extends AppCompatActivity {

   /* private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_PAIRED_DEVICE = 2;

    Button btnDispPareados;
    TextView estadoBluetooth;
    BluetoothAdapter bluetoothAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_cad);
        btnDispPareados = (Button) findViewById(R.id.listpaireddevices);
        estadoBluetooth = (TextView) findViewById(R.id.bluetoothstate);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        CheckBluetoothState();
        btnDispPareados.setOnClickListener(btnListPairedDevicesOnClickListener);
    }

    private void CheckBluetoothState() {
        if (bluetoothAdapter == null) {
            estadoBluetooth.setText("Bluetooth não suportado");
        } else {
            if (bluetoothAdapter.isEnabled()) {
                if (bluetoothAdapter.isDiscovering()) {
                    estadoBluetooth.setText("Bluetooth is currently in device discovery process.");
                } else {
                    estadoBluetooth.setText("Bluetooth está ativado");
                    btnDispPareados.setEnabled(true);
                }
            } else {
                estadoBluetooth.setText("Bluetooth não está ativado!");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private Button.OnClickListener btnListPairedDevicesOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(RelatorioCadActivity.this, DeviceListActivity.class);
            startActivityForResult(intent, REQUEST_PAIRED_DEVICE);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            CheckBluetoothState();
        }

        if (requestCode == REQUEST_PAIRED_DEVICE) {
            if (resultCode == RESULT_OK) {

            }
        }
    }*/
}
