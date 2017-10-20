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
 * Created by lucas on 19/10/17.
 */
public class UsuarioListTask extends AsyncTask<Void, Void, Void> {
    List<Usuario> usuarios;
    UsuarioCadActivity activity;

    public UsuarioListTask(UsuarioCadActivity activity){
        this.activity = activity;
    }


    @Override
    protected Void doInBackground(Void... params) {
        String url = "http://192.168.0.33/controlcar/ws/listausuario.php";

        DefaultHttpClient client = new DefaultHttpClient();//responsavel por executar o metodo
        HttpGet get = new HttpGet(url);//metodo a ser executado
        try {
            HttpResponse resp = client.execute(get);//executa o metodo

            InputStream is = resp.getEntity().getContent();//conteudo do corpo da resposta
            InputStreamReader reader = new InputStreamReader(is);//permite ler a resposta

            Type tipo = new TypeToken<List<Usuario>>(){ }.getType();//converte tipo para pode ler campos, quando nao for lista nao precisa dessa implementacao

            Gson gson = new Gson();
            this.usuarios = gson.fromJson(reader, tipo);//Quando nao for lista passa Contato.class em vez do tipo

            this.publishProgress();//executa o onProgressUpdate

        } catch (Exception e) {
            e.printStackTrace();
        }finally{

        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        this.activity.atualizar(usuarios);
    }

}