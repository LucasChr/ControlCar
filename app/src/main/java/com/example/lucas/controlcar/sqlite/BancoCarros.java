package com.example.lucas.controlcar.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by lucas on 31/07/17.
 */

public class BancoCarros {


    private static final String NOME_BANCO = "carros";
    private static final int VERSAO_BANCO = 1;

    private static final String[] SCRIPT_DATABASE_DELETE = new String[]{"DROP TABLE IF EXISTS carros;"};


    private static final String[] SCRIPT_DATABASE_CREATE = new String[]{
            "create table carros(_id integer primary key, cr_nome text, cr_montadora text, cr_modelo text, cr_placa text, cr_ano text, cr_cor text, cr_foto)"};

    private static SQLiteDatabase db;

    public static SQLiteDatabase getDB(Context ctx) {
        if (db == null) {
            SQLiteHelper dbHelper = new SQLiteHelper(ctx, NOME_BANCO, VERSAO_BANCO, SCRIPT_DATABASE_CREATE, SCRIPT_DATABASE_DELETE);
            db = dbHelper.getWritableDatabase();
        }
        return db;
    }

}
