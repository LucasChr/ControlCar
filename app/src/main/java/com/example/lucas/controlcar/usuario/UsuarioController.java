package com.example.lucas.controlcar.usuario;

import android.content.Context;

import java.util.List;

/**
 * Created by lucas on 18/10/17.
 */

public class UsuarioController {
    private static UsuarioDAO usuarioDAO;
    private static UsuarioController instance;

    public static UsuarioController getInstance(Context context) {
        if (instance == null) {
            instance = new UsuarioController();
            usuarioDAO = new UsuarioDAO(context);
        }
        return instance;
    }

    public void insert(Usuario usuario) throws Exception {
        usuarioDAO.salvar(usuario);
    }

    public void update(Usuario usuario) throws Exception {
        usuarioDAO.alterar(usuario);
    }

    public List<Usuario> listar() throws Exception {
        return usuarioDAO.listar();
    }

    public boolean validaLogin(String usuario, String senha) throws Exception {
        Usuario user = usuarioDAO.buscarUsuario(usuario, senha);
        if (user == null || user.getUsuario() == null || user.getSenha() == null) {
            return false;
        }
        String informado = usuario + senha;
        String esperado = user.getUsuario() + user.getSenha();
        if (informado.equals(esperado)) {
            return true;
        }
        return false;
    }
}
