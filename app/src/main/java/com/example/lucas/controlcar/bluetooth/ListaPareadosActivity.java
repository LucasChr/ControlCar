package com.example.lucas.controlcar.bluetooth;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.controlcar.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ListaPareadosActivity extends ListActivity  {

    private BluetoothAdapter btfAdapter;
    public static String ENDERECO_MAC = null;
    protected List<BluetoothDevice> lista;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_pareados);

        ArrayAdapter<String> ArrayBluetooth = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        btfAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> dispositivosPareados = btfAdapter.getBondedDevices();

        if(dispositivosPareados.size() > 0){
            for (BluetoothDevice dispositivo : dispositivosPareados){
                String nomeBtf = dispositivo.getName();
                String macBtf = dispositivo.getAddress();
                ArrayBluetooth.add(nomeBtf + "\n" + macBtf);
            }
        }
        setListAdapter(ArrayBluetooth);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String infoGeral = ((TextView) v).getText().toString();
        String endMac = infoGeral.substring(infoGeral.length() - 17);

        Intent it = new Intent();
        it.putExtra(ENDERECO_MAC, endMac);
        setResult(RESULT_OK, it);
        finish();
    }
}
