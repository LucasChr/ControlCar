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
 * Created by lucas on 19/10/17.
 */
public class UsuarioIncTask extends AsyncTask<Usuario, Void, Void> {

    ProgressDialog dialog;
    UsuarioCadActivity activity;

    public UsuarioIncTask(UsuarioCadActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        dialog = new ProgressDialog(this.activity);
        dialog.setTitle("TESTE");
        dialog.setMessage("SINCRONIZANDO!");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();

    }

    @Override
    protected Void doInBackground(Usuario... usuario) {
        String url = "http://192.168.0.33/controlcar/ws/incluiusuario.php";
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        Gson gs = new Gson();
        String objJson = gs.toJson(usuario[0]);

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

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        activity.finalizar();
    }
}
