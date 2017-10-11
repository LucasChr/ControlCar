package com.example.lucas.controlcar.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by lucas on 05/10/17.
 */

public class BancoRelatorios {

    private static final String NOME_BANCO = "relatorios";
    private static final int VERSAO_BANCO = 1;

    //Script
    private static final String[] SCRIPT_DATABASE_DELETE = new String[]{"DROP TABLE IF EXISTS relatorios;"};

    //Tabela com id sequencial usa-se _id
    private static final String[] SCRIPT_DATABASE_CREATE = new String[]{
            "create table relatorios(_id integer primary key, r_data text, r_mediakm text, r_mediarpm text, r_latitude text, r_longitude text,r_kmdia text, r_kmmax text, r_kmmin text"};

    private static SQLiteDatabase db;

    public static SQLiteDatabase getDB(Context ctx) {
        if (db == null) {
            SQLiteHelper dbHelper = new SQLiteHelper(ctx, NOME_BANCO, VERSAO_BANCO, SCRIPT_DATABASE_CREATE, SCRIPT_DATABASE_DELETE);
            db = dbHelper.getWritableDatabase();
        }
        return db;
    }

}
