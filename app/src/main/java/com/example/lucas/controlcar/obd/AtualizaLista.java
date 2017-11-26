package com.example.lucas.controlcar.obd;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.github.pires.obd.commands.ObdCommand;

import java.util.ArrayList;

/**
 * Created by lucas on 15/11/17.
 */

public class AtualizaLista extends AsyncTask<Object, Object, ArrayList<String>> {
    private Context context;
    private ProgressDialog dialog;


    public AtualizaLista(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<String> doInBackground(Object... voids) {
        ArrayList<String> retornoComandos = new ArrayList<>();
        try {
            for (ObdCommand command : ComandosObd.getComandos()) {
                retornoComandos.add(command.getFormattedResult());
            }

        } catch (Exception e) {

        }
        return retornoComandos;
    }

    @Override
    protected void onPostExecute(ArrayList<String> aVoid) {
        super.onPostExecute(aVoid);
    }
}
