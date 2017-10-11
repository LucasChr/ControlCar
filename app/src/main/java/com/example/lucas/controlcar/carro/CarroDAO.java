package com.example.lucas.controlcar.carro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.lucas.controlcar.sqlite.BancoCarros;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucas on 31/07/17.
 */

public class CarroDAO {

    SQLiteDatabase db;

    public CarroDAO(Context context) {
        db = BancoCarros.getDB(context);
    }

    public void salvar(Carro carro) {
        ContentValues values = new ContentValues();
        values.put(Carro.NOME, carro.getNome());
        values.put(Carro.MONTADORA, carro.getMontadora());
        values.put(Carro.MODELO, carro.getModelo());
        values.put(Carro.PLACA, carro.getPlaca());
        values.put(Carro.ANO, carro.getAno());
        values.put(Carro.COR, carro.getCor());
        values.put(Carro.FOTO, carro.getFoto());
        db.insert(Carro.TABELA, null, values);
    }

    public void alterar(Carro carro) {
        ContentValues values = new ContentValues();
        values.put(Carro.NOME, carro.getNome());
        values.put(Carro.MONTADORA, carro.getMontadora());
        values.put(Carro.MODELO, carro.getModelo());
        values.put(Carro.PLACA, carro.getPlaca());
        values.put(Carro.ANO, carro.getAno());
        values.put(Carro.COR, carro.getCor());
        values.put(Carro.FOTO, carro.getFoto());
        String id = String.valueOf(carro.getId());

        String[] whereArgs = new String[]{id};
        db.update(Carro.TABELA, values, Carro.ID + " = ?", whereArgs);
    }

    public Carro buscar(String id) {

        String[] colunas = Carro.COLUNAS;
        String[] whereArgs = new String[]{id};

        Cursor c = db.query(Carro.TABELA, colunas, Carro.ID + " = ?", whereArgs, null, null, null);

        c.moveToFirst();

        Carro carro = new Carro();
        carro.setId(c.getLong(c.getColumnIndex(Carro.ID)));
        carro.setNome(c.getString(c.getColumnIndex(Carro.NOME)));
        carro.setMontadora(c.getString(c.getColumnIndex(Carro.MONTADORA)));
        carro.setModelo(c.getString(c.getColumnIndex(Carro.MODELO)));
        carro.setPlaca(c.getString(c.getColumnIndex(Carro.PLACA)));
        carro.setAno(c.getInt(c.getColumnIndex(Carro.ANO)));
        carro.setCor(c.getString(c.getColumnIndex(Carro.COR)));
        carro.setFoto(c.getString(c.getColumnIndex(Carro.FOTO)));

        return carro;
    }

    public List<Carro> listar() {

        String[] colunas = Carro.COLUNAS;
        Cursor c = db.query(Carro.TABELA, colunas, null, null, null, null, null);

        List<Carro> carrosList = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                Carro carro = new Carro();
                carro.setId(c.getLong(c.getColumnIndex(Carro.ID)));
                carro.setNome(c.getString(c.getColumnIndex(Carro.NOME)));
                carro.setMontadora(c.getString(c.getColumnIndex(Carro.MONTADORA)));
                carro.setModelo(c.getString(c.getColumnIndex(Carro.MODELO)));
                carro.setPlaca(c.getString(c.getColumnIndex(Carro.PLACA)));
                carro.setAno(c.getInt(c.getColumnIndex(Carro.ANO)));
                carro.setCor(c.getString(c.getColumnIndex(Carro.COR)));
                carro.setFoto(c.getString(c.getColumnIndex(Carro.FOTO)));

                carrosList.add(carro);

                Log.i("lista", carro.getPlaca());
            } while (c.moveToNext());
        }
        return carrosList;
    }

    public void excluir(String id) {
        String[] whereArgs = new String[]{id};
        db.delete(Carro.TABELA, Carro.ID + " = ?", whereArgs);
    }
}
