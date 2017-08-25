package com.example.lucas.controlcar.ws;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.lucas.controlcar.usuario.Usuario;
import com.example.lucas.controlcar.usuario.UsuarioCadActivity;
import com.google.gson.Gson;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by lucas on 07/08/17.
 */

public class UsuarioInkTask extends AsyncTask<Usuario, Void, Void> {
    ProgressDialog dialog;
    UsuarioCadActivity activity;

    public UsuarioInkTask(UsuarioCadActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(this.activity);
        dialog.setTitle("Carro");
        dialog.setMessage("SINCRONIZANDO...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
    }

    @Override
    protected Void doInBackground(Usuario... usuarios) {
        String url = "http://10.0.0.9/www/controlcar/ws/incluiusuario.php";
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        Gson gson = new Gson();
        String objJson = gson.toJson(usuarios[0]);

        try {
            StringEntity entity = new StringEntity(objJson);

            post.setEntity(entity);
            post.setHeader("Content-type", "application/json");
            post.setHeader("Accept", "application/json");
            client.execute(post);

            this.publishProgress();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dialog.dismiss();
        }
        return null;
    }


}
