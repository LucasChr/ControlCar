package com.example.lucas.controlcar.ws;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.lucas.controlcar.carro.Carro;
import com.example.lucas.controlcar.carro.CarroCadActivity;
import com.google.gson.Gson;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by lucas on 19/10/17.
 */

public class CarroIncTask extends AsyncTask<Carro, Void, Void> {

    ProgressDialog dialog;
    CarroCadActivity activity;

    public CarroIncTask(CarroCadActivity activity) {
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
    protected Void doInBackground(Carro... carro) {
        String url = "http://192.168.0.33/controlcar/ws/incluicarro.php";
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        Gson gs = new Gson();
        String objJson = gs.toJson(carro[0]);

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
