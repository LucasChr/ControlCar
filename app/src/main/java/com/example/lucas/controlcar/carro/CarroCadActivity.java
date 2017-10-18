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

public class CarroCadActivity extends AppCompatActivity {

    private EditText edtNome, edtMontadora, edtModelo, edtPlaca, edtAno, edtCor;
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

        edtNome = (EditText) findViewById(R.id.carro_cad_etNome);
        edtMontadora = (EditText) findViewById(R.id.carro_cad_etMontadora);
        edtModelo = (EditText) findViewById(R.id.carro_cad_etModelo);
        edtPlaca = (EditText) findViewById(R.id.carro_cad_etPlaca);
        edtAno = (EditText) findViewById(R.id.carro_cad_etAno);
        edtCor = (EditText) findViewById(R.id.carro_cad__etCor);
        imgFoto = (ImageView) findViewById(R.id.carro_cad_imgCarro);

        carroDAO = new CarroDAO(this);
    }

    public void salvarCarro(View v) {
        Carro carro = new Carro();

        if (edtNome.getText().toString().length() < 1 || edtNome.equals("")) {
            edtNome.setError("Você precisa inserir um nome para o carro");
        } else {
            carro.setNome(edtNome.getText().toString());
        }
        if (edtMontadora.getText().toString().length() < 1 || edtMontadora.equals("")) {
            edtMontadora.setError("Você precisa inserir uma montadora");
        } else {
            carro.setMontadora(edtMontadora.getText().toString());
        }
        if (edtModelo.getText().toString().length() < 1 || edtModelo.equals("")) {
            edtModelo.setError("Você precisa inserir um modelo");
        } else {
            carro.setModelo(edtModelo.getText().toString());
        }
        if (edtPlaca.getText().toString().length() < 1 || edtPlaca.equals("")) {
            edtPlaca.setError("Você precisa inserir a placa do veículo");
        } else {
            carro.setPlaca(edtPlaca.getText().toString());
        }
        if (edtAno.getText().toString().length() < 1 || edtAno.equals("")) {
            edtAno.setError("Você precisa inserir o ano");
        } else {
            carro.setAno(Integer.valueOf(edtAno.getText().toString()));
        }
        if (edtCor.getText().toString().length() < 1 || edtCor.equals("")) {
            carro.setCor("");
        } else {
            carro.setCor(edtCor.getText().toString());
        }
        if (srtFoto == null || srtFoto.equals("")) {
            carro.setFoto("");
        } else {
            carro.setFoto(srtFoto);
        }

        if (carro.getNome() != null && carro.getMontadora() != null && carro.getModelo() != null && carro.getPlaca() != null &&
                carro.getAno() != null && carro.getCor() != null) {
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
        outState.putString("Nome", edtNome.getText().toString());
        outState.putString("Montadora", edtMontadora.getText().toString());
        outState.putString("Modelo", edtModelo.getText().toString());
        outState.putString("Placa", edtPlaca.getText().toString());
        outState.putString("Ano", edtAno.getText().toString());
        outState.putString("Cor", edtCor.getText().toString());
        Log.i("bundle", "save");
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        edtNome.setText(bundle.getString("Nome"));
        edtMontadora.setText(bundle.getString("Montadora"));
        edtModelo.setText(bundle.getString("Modelo"));
        edtPlaca.setText(bundle.getString("Placa"));
        edtAno.setText(bundle.getString("Ano"));
        edtCor.setText(bundle.getString("Cor"));
        Log.i("bundle", "restore");
    }

}
