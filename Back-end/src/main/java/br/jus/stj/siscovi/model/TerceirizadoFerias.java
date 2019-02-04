package br.jus.stj.siscovi.model;

import java.sql.Date;

public class TerceirizadoFerias {
    private final int codigoTerceirizadoContrato;
    private final String nomeTerceirizado;
    private final Date inicioPeriodoAquisitivo;
    private final Date fimPeriodoAquisitivo;
    private final boolean existeCalculoAnterior;

    public TerceirizadoFerias(int codigoTerceirizadoContrato, String nomeTerceirizado, Date inicioPeriodoAquisitivo, Date fimPeriodoAquisitivo, boolean existeCalculoAnterior) {
        this.codigoTerceirizadoContrato = codigoTerceirizadoContrato;
        this.nomeTerceirizado = nomeTerceirizado;
        this.inicioPeriodoAquisitivo = inicioPeriodoAquisitivo;
        this.fimPeriodoAquisitivo = fimPeriodoAquisitivo;
        this.existeCalculoAnterior = existeCalculoAnterior;
    }
}
