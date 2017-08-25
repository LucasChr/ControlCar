package com.example.lucas.controlcar.usuario;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.lucas.controlcar.R;

import java.util.List;

public class UsuarioCadActivity extends AppCompatActivity {

    private EditText edtUsuario, edtSenha, edtCSenha, edtNome, edtEmail, edtTelefone;
    private String srtLista;
    private UsuarioDAO usuarioDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_cad);

//        Intent it = getIntent();
//        Bundle bundle = it.getExtras();
//        String txt = bundle.getString("txt");
//        srtLista = txt;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtUsuario = (EditText) findViewById(R.id.activity_usuario_cad_edtUsuario);
        edtSenha = (EditText) findViewById(R.id.activity_usuario_cad_edtSenha);
        edtCSenha = (EditText) findViewById(R.id.activity_usuario_cad_edtCSenha);
        edtNome = (EditText) findViewById(R.id.activity_usuario_cad_edtNome);
        edtEmail = (EditText) findViewById(R.id.activity_usuario_cad_edtEmail);
        edtTelefone = (EditText) findViewById(R.id.activity_usuario_cad_edtTelefone);

        usuarioDAO = new UsuarioDAO(this);
    }

    public void salvarUsuario(View v) {
        Usuario usuario = new Usuario();

        if (edtUsuario.getText().toString().length() < 1 || edtUsuario.equals("")) {
            edtUsuario.setError("Você precisa inserir seu nome completo");
        } else {
            usuario.setUsuario(edtUsuario.getText().toString());
        }
        if (edtSenha.getText().toString().equals(edtCSenha.getText().toString())) {
            if (edtSenha.getText().toString().length() < 1 || edtSenha.equals("")) {
                edtSenha.setError("Você precisa inserir uma senha");
            } else {
                usuario.setSenha(edtSenha.getText().toString());
            }
        } else {
            edtSenha.setError("As senhas não conferem");
        }
        if (edtNome.getText().toString().length() < 1 || edtNome.equals("")) {
            edtNome.setError("Você precisa inserir seu nome completo");
        } else {
            usuario.setNome(edtNome.getText().toString());
        }

        if (edtEmail.getText().toString().length() < 1 || edtEmail.equals("")) {
            edtEmail.setError("Você precisa um email");
        } else {
            usuario.setEmail(edtEmail.getText().toString());
        }
        if (edtTelefone.getText().toString().length() < 1 || edtTelefone.equals("")) {
            edtTelefone.setError("Você precisa inserir um telefone");
        } else {
            usuario.setTelefone(Integer.valueOf(edtTelefone.getText().toString()));
        }

        if (usuario.getUsuario() != null && usuario.getSenha() != null && usuario.getNome() != null && usuario.getEmail() != null && usuario.getTelefone() != null) {
            usuarioDAO.salvar(usuario);
            Log.i("Usuario", "Salvo com sucesso");
            finish();
        }
    }

    public void atualizar(List<Usuario> usuarios) {

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
        outState.putString("Usuario", edtUsuario.getText().toString());
        outState.putString("Senha", edtSenha.getText().toString());
        outState.putString("CSenha", edtCSenha.getText().toString());
        outState.putString("Nome", edtNome.getText().toString());
        outState.putString("Email", edtEmail.getText().toString());
        outState.putString("Telefone", edtTelefone.getText().toString());
        Log.i("bundle", "save");
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        edtUsuario.setText(bundle.getString("Usuario"));
        edtSenha.setText(bundle.getString("Senha"));
        edtCSenha.setText(bundle.getString("CSenha"));
        edtNome.setText(bundle.getString("Nome"));
        edtEmail.setText(bundle.getString("Email"));
        edtTelefone.setText(bundle.getString("Telefone"));
        Log.i("bundle", "restore");
    }

}
