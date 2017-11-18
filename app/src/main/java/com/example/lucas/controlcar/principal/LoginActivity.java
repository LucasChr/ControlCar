package com.example.lucas.controlcar.principal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lucas.controlcar.R;
import com.example.lucas.controlcar.usuario.Usuario;
import com.example.lucas.controlcar.usuario.UsuarioCadActivity;
import com.example.lucas.controlcar.usuario.UsuarioController;

public class LoginActivity extends Activity {
    private EditText edtUsuario, edtSenha;
    private Context context;
    private UsuarioController usuarioController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        usuarioController = UsuarioController.getInstance(context);
        edtUsuario = (EditText) findViewById(R.id.edtLogin);
        edtSenha = (EditText) findViewById(R.id.edtSenha);
        try {
            testaInicializacao();
        } catch (Exception e) {
            exibeToast("Erro inicializando banco de dados");
            e.printStackTrace();
        }
    }

    public void testaInicializacao() throws Exception {
        if (usuarioController.listar().isEmpty()) {
            Usuario usuario = new Usuario(null, "admin", "123");
            usuarioController.insert(usuario);
        }
    }

    public void Validar(View view) {
        String usuario = edtUsuario.getText().toString();
        String senha = edtSenha.getText().toString();
        try {
            boolean isValid = usuarioController.validaLogin(usuario, senha);
            if (isValid) {
                exibeToast("Usuario e senha verificados com sucesso!");
                Log.d("Login", "passou login");
                Intent it = new Intent(getApplicationContext(), PrincipalActivity.class);
                startActivityForResult(it, 1);
            } else {
                exibeToast("Usuario ou senha incorretos!");
            }
        } catch (Exception e) {
            exibeToast("Erro ao verificar usuario e senha");
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    public void Registar(View v) {
        Intent it = new Intent(this, UsuarioCadActivity.class);
        startActivity(it);
    }

    public void exibeToast(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }
}

