package com.example.lucas.controlcar.usuario;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.lucas.controlcar.R;

public class UsuarioActivity extends AppCompatActivity {

    private EditText edtUsuario, edtNome, edtEmail, edtTelefone;
    private Bitmap ivFoto;
    private ImageView imgUsuario;
    private Usuario usuario;
    private UsuarioDAO usuarioDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carro);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        usuarioDAO = new UsuarioDAO(this);
        Intent it = getIntent();

        if (it != null) {
            usuario = new Usuario();
            usuario = usuarioDAO.buscar(it.getStringExtra(Usuario.ID));
        }

        edtUsuario = (EditText) findViewById(R.id.activity_carro_etNome);
        edtNome = (EditText) findViewById(R.id.activity_carro_etMontadora);
        edtEmail = (EditText) findViewById(R.id.activity_carro_etModelo);
        edtTelefone = (EditText) findViewById(R.id.activity_carro_etPlaca);
        imgUsuario = (ImageView) findViewById(R.id.activity_carro_imgCarro);

        byte[] bytearray = Base64.decode(usuario.getFoto(), Base64.DEFAULT);
        ivFoto = BitmapFactory.decodeByteArray(bytearray, 0, bytearray.length);
        imgUsuario.setImageBitmap(ivFoto);

        edtUsuario.setText(usuario.getUsuario());
        edtNome.setText(usuario.getNome());
        edtEmail.setText(usuario.getEmail());
        edtTelefone.setText(usuario.getTelefone());
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
        outState.putString("Usuario", edtUsuario.getText().toString());
        outState.putString("Nome", edtNome.getText().toString());
        outState.putString("Email", edtEmail.getText().toString());
        outState.putString("Telefone", edtTelefone.getText().toString());
        Log.i("bundle", "save");
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        edtUsuario.setText(bundle.getString("Usuario"));
        edtNome.setText(bundle.getString("Nome"));
        edtEmail.setText(bundle.getString("Email"));
        edtTelefone.setText(bundle.getString("Telefone"));
        Log.i("bundle", "restore");
    }
}
