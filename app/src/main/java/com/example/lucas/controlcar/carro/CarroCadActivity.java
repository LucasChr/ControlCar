package com.example.lucas.controlcar.carro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.lucas.controlcar.R;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class CarroCadActivity extends AppCompatActivity {

    private EditText etNome, etMontadora, etModelo, etPlaca, etAno, etCor;
    private ImageView imgFoto;
    private String srtFoto;
    private CarroDAO carroDAO;
    private String srtLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carro_cad);

        // Intent it = getIntent();
        // Bundle bundle = it.getExtras();
        //  String txt = bundle.getString("txt");
        //  srtLista = txt;

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etNome = (EditText) findViewById(R.id.carro_cad_etNome);
        etMontadora = (EditText) findViewById(R.id.carro_cad_etMontadora);
        etModelo = (EditText) findViewById(R.id.carro_cad_etModelo);
        etPlaca = (EditText) findViewById(R.id.carro_cad_etPlaca);
        etAno = (EditText) findViewById(R.id.carro_cad_etAno);
        etCor = (EditText) findViewById(R.id.carro_cad__etCor);
        imgFoto = (ImageView) findViewById(R.id.carro_cad_imgCarro);

        carroDAO = new CarroDAO(this);
    }

    public void salvarCarro(View v) {
        Carro carro = new Carro();

        if (etNome.getText().toString().length() < 1 || etNome.equals("")) {
            etNome.setError("Você precisa inserir um nome para o carro");
        } else {
            carro.setNome(etNome.getText().toString());
        }
        if (etMontadora.getText().toString().length() < 1 || etMontadora.equals("")) {
            etMontadora.setError("Você precisa inserir uma montadora");
        } else {
            carro.setMontadora(etMontadora.getText().toString());
        }
        if (etModelo.getText().toString().length() < 1 || etModelo.equals("")) {
            etModelo.setError("Você precisa inserir um modelo");
        } else {
            carro.setModelo(etModelo.getText().toString());
        }
        if (etPlaca.getText().toString().length() < 1 || etPlaca.equals("")) {
            etPlaca.setError("Você precisa inserir a placa do veículo");
        } else {
            carro.setPlaca(etPlaca.getText().toString());
        }
        if (etAno.getText().toString().length() < 1 || etAno.equals("")) {
            etAno.setError("Você precisa inserir o ano");
        } else {
            carro.setAno(Integer.valueOf(etAno.getText().toString()));
        }
        if (etCor.getText().toString().length() < 1 || etCor.equals("")) {
            carro.setCor("");
        } else {
            carro.setCor(etCor.getText().toString());
        }
        if (srtFoto == null || srtFoto.equals("")) {
            carro.setFoto("");
        } else {
            carro.setFoto(srtFoto);
        }

        if (carro.getNome() != null && carro.getMontadora() != null && carro.getModelo() != null && carro.getPlaca() != null &&
                carro.getAno() != null && carro.getCor() != null && srtFoto != null) {
            carroDAO.salvar(carro);
            Log.i("Veiculo", "Salvo com sucesso");
            finish();
        }

    }

    public void capturarFoto(View v) {
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(it, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");

            imgFoto.setImageBitmap(bitmap);


            //converte a imagem para string para enviar ao banco
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

            byte[] bytes = baos.toByteArray();
            srtFoto = Base64.encodeToString(bytes, Base64.DEFAULT);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
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
