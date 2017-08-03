package com.example.lucas.controlcar.principal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.example.lucas.controlcar.R;
import com.example.lucas.controlcar.usuario.Usuario;
import com.example.lucas.controlcar.usuario.UsuarioCadActivity;
import com.example.lucas.controlcar.usuario.UsuarioDAO;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends AppCompatActivity {

    private EditText edtSenha, edtLogin;
    private UsuarioDAO usuarioDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtLogin = (EditText) findViewById(R.id.login_usuario);
        edtSenha = (EditText) findViewById(R.id.login_senha);
    }

    public void Logar(View v) {
        login(edtLogin.getText().toString(), edtSenha.getText().toString());
    }

    public void login(String login, String senha) {
        Usuario user = usuarioDAO.buscarUsuario(login);

        if (user.getUsuario() != null && user.getUsuario() == login) {
            if (user.getSenha() != null && user.getSenha() == senha) {
                Intent it = new Intent(this, PrincipalActivity.class);
                startActivity(it);
            } else {
                edtSenha.setError("Senha incorreta!");
            }
        } else {
            edtLogin.setError("Login incorreto!");
        }
    }

    public void criarConta(View v) {
        Intent it = new Intent(this, UsuarioCadActivity.class);
        startActivity(it);
    }
}

