package br.jus.stj.siscovi.model;

import java.sql.Date;
import java.util.List;

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
    private List<HistoricoGestorModel> historicoGestao;
    private List<CargoModel> funcoes;
    private Date dataAssinatura;
    private List<PercentualModel> percentuais;
    private String numeroProcessoSTJ;
    private String loginAtualizacao;
    private Date dataAtualizacao;
    private EventoContratualModel eventoContratual;

    public EventoContratualModel getEventoContratual() {
        return eventoContratual;
    }

    public void setEventoContratual(EventoContratualModel eventoContratual) {
        this.eventoContratual = eventoContratual;
    }

    public String getNumeroProcessoSTJ() {
        return numeroProcessoSTJ;
    }

    public void setNumeroProcessoSTJ(String numeroProcessoSTJ) {
        this.numeroProcessoSTJ = numeroProcessoSTJ;
    }

    public List<PercentualModel> getPercentuais() {
        return percentuais;
    }

    public void setPercentuais(List<PercentualModel> percentuais) {
        this.percentuais = percentuais;
    }

    public List<HistoricoGestorModel> getHistoricoGestao() {
        return historicoGestao;
    }

    public void setHistoricoGestao(List<HistoricoGestorModel> historicoGestao) {
        this.historicoGestao = historicoGestao;
    }

    public List<CargoModel> getFuncoes() {
        return funcoes;
    }

    public void setFuncoes(List<CargoModel> funcoes) {
        this.funcoes = funcoes;
    }

    public Date getDataAssinatura() {
        return dataAssinatura;
    }

    public void setDataAssinatura(Date dataAssinatura) {
        this.dataAssinatura = dataAssinatura;
    }

    public ContratoModel(int codigo , String nomeDaEmpresa, String cnpj){
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

    public void setLoginAtualizacao(String loginAtualizacao) {
        this.loginAtualizacao = loginAtualizacao;
    }

    public void setDataAtualizacao(Date dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    @Override
    public String toString() {
        return "ContratoModel{" +
                "nomeDaEmpresa='" + nomeDaEmpresa + '\'' +
                ", cnpj='" + cnpj + '\'' +
                ", codigo=" + codigo +
                ", numeroDoContrato='" + numeroDoContrato + '\'' +
                ", anoDoContrato=" + anoDoContrato +
                ", dataInicio=" + dataInicio +
                ", dataFim=" + dataFim +
                ", objeto='" + objeto + '\'' +
                ", seAtivo='" + seAtivo + '\'' +
                ", historicoGestao=" + historicoGestao +
                ", funcoes=" + funcoes +
                ", dataAssinatura=" + dataAssinatura +
                ", percentuais=" + percentuais +
                ", numeroProcessoSTJ='" + numeroProcessoSTJ + '\'' +
                ", loginAtualizacao='" + loginAtualizacao + '\'' +
                ", dataAtualizacao=" + dataAtualizacao +
                ", eventoContratual=" + eventoContratual +
                '}';
    }
}
