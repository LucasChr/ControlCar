package com.example.lucas.controlcar.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by lucas on 31/07/17.
 */

public class BancoDados {


    private static final String NOME_BANCO = "control";
    private static final int VERSAO_BANCO = 1;

    private static final String[] SCRIPT_DATABASE_DELETE = new String[]{"DROP TABLE IF EXISTS carros;" +" DROP TABLE IF EXISTS usuairos"};


    private static final String[] SCRIPT_DATABASE_CREATE = new String[]{
            "create table carros(_id integer primary key, cr_nome text, cr_montadora text, cr_modelo text, cr_placa text, cr_ano text, cr_cor text, cr_foto)" +
            "create table usuarios(_id integer primary key, u_usuario text, u_senha text, u_nome text, u_email text, u_telefone text, u_foto text)"};

    private static SQLiteDatabase db;

    public static SQLiteDatabase getDB(Context ctx) {
        if (db == null) {
            SQLiteHelper dbHelper = new SQLiteHelper(ctx, NOME_BANCO, VERSAO_BANCO, SCRIPT_DATABASE_CREATE, SCRIPT_DATABASE_DELETE);
            db = dbHelper.getWritableDatabase();
        }
        return db;
    }

}
