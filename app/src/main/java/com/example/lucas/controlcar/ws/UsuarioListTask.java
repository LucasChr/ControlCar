package com.example.lucas.controlcar.ws;

import android.os.AsyncTask;

import com.example.lucas.controlcar.usuario.Usuario;
import com.example.lucas.controlcar.usuario.UsuarioCadActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by lucas on 07/08/17.
 */

public class UsuarioListTask extends AsyncTask<Void, Void, Void> {

    List<Usuario> usuarios;
    UsuarioCadActivity activity;

    public UsuarioListTask(UsuarioCadActivity activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Void... params) {
        String url = "http://10.0.0.9/www/controlcar/ws/listausuario.php";

        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse resp = client.execute(get);

            InputStream is = resp.getEntity().getContent();
            InputStreamReader reader = new InputStreamReader(is);

            Type tipo = new TypeToken<List<Usuario>>() {
            }.getType();
            Gson gson = new Gson();

            this.usuarios = gson.fromJson(reader, tipo);
            this.publishProgress();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        this.activity.atualizar(usuarios);
    }
}
