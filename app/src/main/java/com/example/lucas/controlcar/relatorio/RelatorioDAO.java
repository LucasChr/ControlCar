package com.example.lucas.controlcar.relatorio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.lucas.controlcar.sqlite.BancoRelatorios;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucas on 26/08/17.
 */

public class RelatorioDAO {

    SQLiteDatabase db;

    public RelatorioDAO(Context context) {
        db = BancoRelatorios.getDB(context);
    }

    public void salvar(Relatorio relatorio) {
        ContentValues values = new ContentValues();
        values.put(Relatorio.DATA, relatorio.getData().toString());
        values.put(Relatorio.MEDIAKM, relatorio.getMediaKm());
        values.put(Relatorio.MEDIARPM, relatorio.getMediaRpm());
        values.put(Relatorio.LATITUDE, relatorio.getLatitude());
        values.put(Relatorio.LONGITUDE, relatorio.getLongitude());
        values.put(Relatorio.KMDIA, relatorio.getKmdia());
        values.put(Relatorio.KMMAX, relatorio.getKmmax());
        values.put(Relatorio.KMMIN, relatorio.getKmmin());
        db.insert(Relatorio.TABELA, null, values);
    }

    public void alterar(Relatorio relatorio) {
        ContentValues values = new ContentValues();
        values.put(Relatorio.DATA, relatorio.getData().toString());
        values.put(Relatorio.MEDIAKM, relatorio.getMediaKm());
        values.put(Relatorio.MEDIARPM, relatorio.getMediaRpm());
        values.put(Relatorio.LATITUDE, relatorio.getLatitude());
        values.put(Relatorio.LONGITUDE, relatorio.getLongitude());
        values.put(Relatorio.KMDIA, relatorio.getKmdia());
        values.put(Relatorio.KMMAX, relatorio.getKmmax());
        values.put(Relatorio.KMMIN, relatorio.getKmmin());

        String id = String.valueOf(relatorio.getId());
        String[] whereArgs = new String[]{id};

        db.update(Relatorio.TABELA, values, Relatorio.ID + " = ?", whereArgs);
    }

    public Relatorio buscar(String id) {
        String[] colunas = Relatorio.COLUNAS;
        String[] whereArgs = new String[]{id};

        Cursor c = db.query(Relatorio.TABELA, colunas, Relatorio.ID + " = ?", whereArgs, null, null, null);

        c.moveToFirst();

        Relatorio relatorio = new Relatorio();
        relatorio.setId(c.getLong(c.getColumnIndex(Relatorio.ID)));
        relatorio.setData(c.getString(c.getColumnIndex(Relatorio.DATA)));
        relatorio.setMediaKm(c.getDouble(c.getColumnIndex(Relatorio.MEDIAKM)));
        relatorio.setMediaRpm(c.getDouble(c.getColumnIndex(Relatorio.MEDIARPM)));
        relatorio.setLatitude(c.getString(c.getColumnIndex(Relatorio.LATITUDE)));
        relatorio.setLongitude(c.getString(c.getColumnIndex(Relatorio.LONGITUDE)));
        relatorio.setKmdia(c.getDouble(c.getColumnIndex(Relatorio.KMDIA)));
        relatorio.setKmmax(c.getDouble(c.getColumnIndex(Relatorio.KMMAX)));
        relatorio.setKmmin(c.getDouble(c.getColumnIndex(Relatorio.KMMIN)));

        return relatorio;
    }

    public List<Relatorio> listar() {

        String[] colunas = Relatorio.COLUNAS;
        Cursor c = db.query(Relatorio.TABELA, colunas, null, null, null, null, null);

        List<Relatorio> relatorioList = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                Relatorio relatorio = new Relatorio();
                relatorio.setId(c.getLong(c.getColumnIndex(Relatorio.ID)));
                relatorio.setData(c.getString(c.getColumnIndex(Relatorio.DATA)));
                relatorio.setMediaKm(c.getDouble(c.getColumnIndex(Relatorio.MEDIAKM)));
                relatorio.setMediaRpm(c.getDouble(c.getColumnIndex(Relatorio.MEDIARPM)));
                relatorio.setLatitude(c.getString(c.getColumnIndex(Relatorio.LATITUDE)));
                relatorio.setLongitude(c.getString(c.getColumnIndex(Relatorio.LONGITUDE)));
                relatorio.setKmdia(c.getDouble(c.getColumnIndex(Relatorio.KMDIA)));
                relatorio.setKmmax(c.getDouble(c.getColumnIndex(Relatorio.KMMAX)));
                relatorio.setKmmin(c.getDouble(c.getColumnIndex(Relatorio.KMMIN)));

                relatorioList.add(relatorio);

                Log.i("lista", relatorio.getData().toString());
            } while (c.moveToNext());
        }
        return relatorioList;
    }

    public void excluir(String id) {
        String[] whereArgs = new String[]{id};
        db.delete(Relatorio.TABELA, Relatorio.ID + " = ?", whereArgs);
    }

}
