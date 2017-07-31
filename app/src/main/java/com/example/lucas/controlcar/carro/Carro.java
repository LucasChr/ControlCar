package com.example.lucas.controlcar.carro;

/**
 * Created by lucas on 31/07/17.
 */

public class Carro {

    private Long id;
    private String nome;
    private String montadora;
    private String modelo;
    private String placa;
    private Integer ano;
    private String cor;
    private String foto;

    public static final String ID = "_id";
    public static final String NOME = "cr_nome";
    public static final String MONTADORA = "cr_montadora";
    public static final String MODELO = "cr_modelo";
    public static final String PLACA = "cr_placa";
    public static final String ANO = "cr_ano";
    public static final String COR = "cr_cor";
    public static final String FOTO = "cr_foto";

    public static final String TABELA = "carros";
    public static final String[] COLUNAS = {ID, NOME, MONTADORA, MODELO, PLACA, ANO, COR, FOTO};

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMontadora() {
        return montadora;
    }

    public void setMontadora(String montadora) {
        this.montadora = montadora;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
