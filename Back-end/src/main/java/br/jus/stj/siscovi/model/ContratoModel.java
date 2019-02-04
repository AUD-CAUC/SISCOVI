package br.jus.stj.siscovi.model;

import java.sql.Date;

public class ContratoModel {
    private String nomeDaEmpresa;
    private String cnpj;
    private int codigo;
    private String numeroDoContrato;
    private int anoDoContrato;
    private Date dataInicio;
    private Date dataFim;
    private String objeto;
    private String seAtivo;

    public ContratoModel(int codigo ,String nomeDaEmpresa, String cnpj){
        this.codigo = codigo;
        this.nomeDaEmpresa = nomeDaEmpresa;
        this.cnpj = cnpj;
    }
    public Date getDataFim() {
        return dataFim;
    }
    public Date getDataInicio() {
        return dataInicio;
    }
    public int getCodigo() {
        return codigo;
    }
    public String getNumeroDoContrato() {
        return numeroDoContrato;
    }
    public int getAnoDoContrato() {
        return anoDoContrato;
    }
    public String getCnpj() {
        return cnpj;
    }
    public String getNomeDaEmpresa() {
        return nomeDaEmpresa;
    }
    public String getObjeto () {return objeto;}
    public String getSeAtivo () {return seAtivo;}
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }
    public void setAnoDoContrato(int anoDoContrato) {
        this.anoDoContrato = anoDoContrato;
    }
    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }
    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }
    public void setNomeDaEmpresa(String nomeDaEmpresa) {
        this.nomeDaEmpresa = nomeDaEmpresa;
    }
    public void setNumeroDoContrato(String numeroDoContrato) {
        this.numeroDoContrato = numeroDoContrato;
    }
    public void setObjeto (String objeto) {this.objeto = objeto;}
    public void setSeAtivo(String seAtivo) {this.seAtivo = seAtivo;}
}
