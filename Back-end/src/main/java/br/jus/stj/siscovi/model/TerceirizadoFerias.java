package br.jus.stj.siscovi.model;

import java.sql.Date;

public class TerceirizadoFerias {
    private final int codigoTerceirizadoContrato;
    private final String nomeTerceirizado;
    private final Date inicioPeriodoAquisitivo;
    private final Date fimPeriodoAquisitivo;
    private final int somaDiasVendidos;
    private final int diasUsufruidos;
    private final boolean parcela14Dias;
    private final boolean existeCalculoAnterior;
    private final String parcelaAnterior;
    private final Date ultimoFimUsufruto;

    public TerceirizadoFerias(int codigoTerceirizadoContrato,
                              String nomeTerceirizado,
                              Date inicioPeriodoAquisitivo,
                              Date fimPeriodoAquisitivo,
                              int diasVendidos,
                              int diasUsufruidos,
                              boolean parcela14Dias,
                              boolean existeCalculoAnterior,
                              String parcelaAnterior,
                              Date ultimoFimUsufruto) {
        this.codigoTerceirizadoContrato = codigoTerceirizadoContrato;
        this.nomeTerceirizado = nomeTerceirizado;
        this.inicioPeriodoAquisitivo = inicioPeriodoAquisitivo;
        this.fimPeriodoAquisitivo = fimPeriodoAquisitivo;
        this.somaDiasVendidos = diasVendidos;
        this.diasUsufruidos = diasUsufruidos;
        this.parcela14Dias = parcela14Dias;
        this.existeCalculoAnterior = existeCalculoAnterior;
        this.parcelaAnterior = parcelaAnterior;
        this.ultimoFimUsufruto = ultimoFimUsufruto;
    }
}
