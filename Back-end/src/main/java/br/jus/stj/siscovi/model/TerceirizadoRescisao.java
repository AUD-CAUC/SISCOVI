package br.jus.stj.siscovi.model;

import java.sql.Date;

public class TerceirizadoRescisao {
    private final int codTerceirizadoContrato;
    private final String nomeTerceirizado;
    private Date dataDesligamento;
    private Date pDataInicioFeriasIntegrais;
    private Date pDataFimFeriasIntegrais;
    private Date pDataInicioFeriasProporcionais;
    private Date pDataFimFeriasProporcionais;
    private String tipoRestituicao;
    private String tipoRescisao;
    private boolean emAnalise;

    public TerceirizadoRescisao (int codTerceirizadoContrato,
                                 String nomeTerceirizado,
                                 Date dataDesligamento,
                                 Date pDataInicioFeriasIntegrais,
                                 Date pDataFimFeriasIntegrais,
                                 Date pDataInicioFeriasProporcionais,
                                 Date pDataFimFeriasProporcionais,
                                 String tipoRestituicao,
                                 String tipoRescisao,
                                 boolean emAnalise) {
        this.codTerceirizadoContrato = codTerceirizadoContrato;
        this.nomeTerceirizado = nomeTerceirizado;
        this.dataDesligamento = dataDesligamento;
        this.pDataInicioFeriasIntegrais = pDataInicioFeriasIntegrais;
        this.pDataFimFeriasIntegrais = pDataFimFeriasIntegrais;
        this.pDataInicioFeriasProporcionais = pDataInicioFeriasProporcionais;
        this.pDataFimFeriasProporcionais = pDataFimFeriasProporcionais;
        this.tipoRestituicao = tipoRestituicao;
        this.tipoRescisao = tipoRescisao;
        this.emAnalise = emAnalise;
    }

    public int getCodTerceirizadoContrato() {
        return codTerceirizadoContrato;
    }

    public String getNomeTerceirizado() {
        return nomeTerceirizado;
    }

    public Date getDataDesligamento() {
        return dataDesligamento;
    }

    public String getTipoRescisao() {
        return tipoRescisao;
    }

    public String getTipoRestituicao() {
        return tipoRestituicao;
    }

    public Date getpDataInicioFeriasIntegrais() { return pDataInicioFeriasIntegrais; }

    public Date getpDataFimFeriasIntegrais() { return pDataFimFeriasIntegrais; }

    public Date getpDataInicioFeriasProporcionais() { return pDataInicioFeriasProporcionais; }

    public Date getpDataFimFeriasProporcionais() { return pDataFimFeriasProporcionais; }

}
