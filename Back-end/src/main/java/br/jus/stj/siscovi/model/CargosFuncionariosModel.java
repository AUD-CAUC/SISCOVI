package br.jus.stj.siscovi.model;

import java.sql.Date;

public class CargosFuncionariosModel {
    private CargoModel funcao;
    private FuncionarioModel funcionario;
    private Date dataDisponibilizacao;
    private Date dataDesligamento;

    public void setFuncionario(FuncionarioModel funcionario) {
        this.funcionario = funcionario;
    }

    public void setDataDesligamento(Date dataDesligamento) {
        this.dataDesligamento = dataDesligamento;
    }

    public void setDataDisponibilizacao(Date dataDisponibilizacao) {
        this.dataDisponibilizacao = dataDisponibilizacao;
    }

    public FuncionarioModel getFuncionario() {
        return funcionario;
    }

    public CargoModel getFuncao() {
        return funcao;
    }

    public void setFuncao(CargoModel funcao) {
        this.funcao = funcao;
    }

    public Date getDataDisponibilizacao() {
        return dataDisponibilizacao;
    }

    public Date getDataDesligamento() {
        return dataDesligamento;
    }
}
