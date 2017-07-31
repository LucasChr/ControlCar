package com.example.lucas.controlcar.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lucas on 31/07/17.
 */
public class
SQLiteHelper extends SQLiteOpenHelper {

    private String[] scriptSQLCreate;
    private String[] scriptSQLDelete;

    public SQLiteHelper(Context context, String name, int version, String[] scriptSQLCreate, String[] scriptSQLDelete){
        super(context, name, null, version);

        this.scriptSQLCreate = scriptSQLCreate;
        this.scriptSQLDelete = scriptSQLDelete;
    }

    //cria banco
    @Override
    public void onCreate(SQLiteDatabase db) {
        int qntScripts = scriptSQLCreate.length;
        for(int i = 0; i <qntScripts; i++){
            db.execSQL(scriptSQLCreate[i]);
        }
    }

    //atualiza banco
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        int qntScripts = scriptSQLDelete.length;
        for(int i=0; i <qntScripts; i++){
            db.execSQL(scriptSQLDelete[i]);
        }
        onCreate(db);
    }
}
