package com.example.lucas.controlcar.config;

/**
 * Created by Mesa on 15/11/2017.
 */

public interface IObdConexao {
    void iniciaConexaoObd() throws Exception;

    void pararConexaoObd();

    void atualizaDados(DadosVeiculo m_ObdData);

}
