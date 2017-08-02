package com.example.lucas.controlcar.usuario;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.lucas.controlcar.sqlite.BancoUsuarios;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucas on 01/08/17.
 */

public class UsuarioDAO {

    SQLiteDatabase db;

    public UsuarioDAO(Context context) {
        db = BancoUsuarios.getDB(context);
    }

    public void salvar(Usuario usuario) {
        ContentValues values = new ContentValues();
        values.put(Usuario.USUARIO, usuario.getUsuario());
        values.put(Usuario.SENHA, usuario.getSenha());
        values.put(Usuario.NOME, usuario.getNome());
        values.put(Usuario.EMAIL, usuario.getEmail());
        values.put(Usuario.TELEFONE, usuario.getTelefone());

        db.insert(Usuario.TABELA, null, values);
    }

    public void alterar(Usuario usuario) {
        ContentValues values = new ContentValues();
        values.put(Usuario.USUARIO, usuario.getUsuario());
        values.put(Usuario.SENHA, usuario.getSenha());
        values.put(Usuario.NOME, usuario.getNome());
        values.put(Usuario.EMAIL, usuario.getEmail());
        values.put(Usuario.TELEFONE, usuario.getTelefone());

        String id = String.valueOf(usuario.getId());

        String[] whereArgs = new String[]{id};
        db.update(Usuario.TABELA, values, Usuario.ID + " = ?", whereArgs);
    }

    public Usuario buscar(String id) {
        String[] colunas = Usuario.COLUNAS;
        String[] whereArgs = new String[]{id};

        Cursor c = db.query(Usuario.TABELA, colunas, Usuario.ID + " = ?", whereArgs, null, null, null);
        c.moveToFirst();

        Usuario usuario = new Usuario();
        usuario.setId(c.getLong(c.getColumnIndex(Usuario.ID)));
        usuario.setUsuario(c.getString(c.getColumnIndex(Usuario.USUARIO)));
        usuario.setSenha(c.getString(c.getColumnIndex(Usuario.SENHA)));
        usuario.setNome(c.getString(c.getColumnIndex(Usuario.NOME)));
        usuario.setEmail(c.getString(c.getColumnIndex(Usuario.EMAIL)));
        usuario.setTelefone(c.getInt(c.getColumnIndex(Usuario.TELEFONE)));

        return usuario;
    }

    public Usuario buscarUsuario(String usuario) {
        String[] colunas = Usuario.COLUNAS;
        String[] whereArgs = new String[]{usuario};

        Cursor c = db.query(Usuario.TABELA, colunas, Usuario.USUARIO + " = ?", whereArgs, null, null, null);
        c.moveToFirst();

        Usuario user = new Usuario();
        user.setId(c.getLong(c.getColumnIndex(Usuario.ID)));
        user.setUsuario(c.getString(c.getColumnIndex(Usuario.USUARIO)));
        user.setSenha(c.getString(c.getColumnIndex(Usuario.SENHA)));
        user.setNome(c.getString(c.getColumnIndex(Usuario.NOME)));
        user.setEmail(c.getString(c.getColumnIndex(Usuario.EMAIL)));
        user.setTelefone(c.getInt(c.getColumnIndex(Usuario.TELEFONE)));

        return user;
    }

    public List<Usuario> listar() {
        String[] colunas = Usuario.COLUNAS;
        Cursor c = db.query(Usuario.TABELA, colunas, null, null, null, null, null);

        List<Usuario> usuarioList = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                Usuario usuario = new Usuario();
                usuario.setId(c.getLong(c.getColumnIndex(Usuario.ID)));
                usuario.setUsuario(c.getString(c.getColumnIndex(Usuario.USUARIO)));
                usuario.setSenha(c.getString(c.getColumnIndex(Usuario.SENHA)));
                usuario.setNome(c.getString(c.getColumnIndex(Usuario.NOME)));
                usuario.setEmail(c.getString(c.getColumnIndex(Usuario.EMAIL)));
                usuario.setTelefone(c.getInt(c.getColumnIndex(Usuario.TELEFONE)));

                usuarioList.add(usuario);
                Log.i("lista", usuario.getUsuario());
            } while (c.moveToNext());
        }
        return usuarioList;
    }

    public void excluir(String id) {
        String[] whereArgs = new String[]{id};
        db.delete(Usuario.TABELA, Usuario.ID + " = ?", whereArgs);
    }

}
