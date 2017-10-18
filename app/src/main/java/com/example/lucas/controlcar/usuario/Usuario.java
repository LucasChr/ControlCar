package com.example.lucas.controlcar.usuario;

/**
 * Created by lucas on 31/07/17.
 */

public class Usuario {

    private Long id;
    private String usuario;
    private String senha;
    private String nome;
    private String email;
    private Integer telefone;
    private String foto;

    public static final String ID = "_id";
    public static final String USUARIO = "u_usuario";
    public static final String SENHA = "u_senha";
    public static final String NOME = "u_nome";
    public static final String EMAIL = "u_email";
    public static final String TELEFONE = "u_telefone";
    public static final String FOTO = "u_foto";

    public static final String TABELA = "usuarios";
    public static final String[] COLUNAS = {ID, USUARIO, SENHA, NOME, EMAIL, TELEFONE, FOTO};

    public Usuario(Long id, String usuario, String senha) {
        this.id = id;
        this.usuario = usuario;
        this.senha = senha;
    }

    public Usuario() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getTelefone() {
        return telefone;
    }

    public void setTelefone(Integer telefone) {
        this.telefone = telefone;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
