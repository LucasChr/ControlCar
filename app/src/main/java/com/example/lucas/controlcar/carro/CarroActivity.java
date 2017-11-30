package com.example.lucas.controlcar.carro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.lucas.controlcar.R;

public class CarroActivity extends AppCompatActivity {

    private CarroDAO carroDAO;
    private Carro carro;
    private EditText etNome, etMontadora, etModelo, etPlaca, etAno, etCor;
    private ImageView imgCarro;
    private Bitmap ivFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carro);

        carroDAO = new CarroDAO(this);
        Intent it = getIntent();

        if (it != null) {
            carro = new Carro();
            carro = carroDAO.buscar(it.getStringExtra(Carro.ID));
        }

        etNome = (EditText) findViewById(R.id.activity_carro_etNome);
        etMontadora = (EditText) findViewById(R.id.activity_carro_etMontadora);
        etModelo = (EditText) findViewById(R.id.activity_carro_etModelo);
        etPlaca = (EditText) findViewById(R.id.activity_carro_etPlaca);
        etAno = (EditText) findViewById(R.id.activity_carro_etAno);
        etCor = (EditText) findViewById(R.id.activity_carro_etCor);

        etNome.setText(carro.getNome());
        etMontadora.setText(carro.getMontadora());
        etModelo.setText(carro.getModelo());
        etPlaca.setText(carro.getPlaca());
        etAno.setText(String.valueOf(carro.getAno()));
        etCor.setText(carro.getCor());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("Nome", etNome.getText().toString());
        outState.putString("Montadora", etMontadora.getText().toString());
        outState.putString("Modelo", etModelo.getText().toString());
        outState.putString("Placa", etPlaca.getText().toString());
        outState.putString("Ano", etAno.getText().toString());
        outState.putString("Cor", etCor.getText().toString());
        Log.i("bundle", "save");
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        etNome.setText(bundle.getString("Nome"));
        etMontadora.setText(bundle.getString("Montadora"));
        etModelo.setText(bundle.getString("Modelo"));
        etPlaca.setText(bundle.getString("Placa"));
        etAno.setText(bundle.getString("Ano"));
        etCor.setText(bundle.getString("Cor"));
        Log.i("bundle", "restore");
    }
}
