package com.example.lucas.controlcar.carro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

//        actionBar.setDisplayHomeAsUpEnabled(true);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setHomeButtonEnabled(true);
//        } else {
//            getActionBar().setDisplayHomeAsUpEnabled(true);
//            getActionBar().setHomeButtonEnabled(true);
//        }

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
        imgCarro = (ImageView) findViewById(R.id.activity_carro_imgCarro);

        byte[] bytearray = Base64.decode(carro.getFoto(), Base64.DEFAULT);
        ivFoto = BitmapFactory.decodeByteArray(bytearray, 0, bytearray.length);
        imgCarro.setImageBitmap(ivFoto);

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
