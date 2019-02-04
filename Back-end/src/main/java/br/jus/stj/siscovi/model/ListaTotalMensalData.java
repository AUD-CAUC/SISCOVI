package br.jus.stj.siscovi.model;

import java.sql.Date;
import java.util.ArrayList;

public class ListaTotalMensalData {
    private Date dataReferencia;
    private ArrayList<TotalMensal> totais;

    public ListaTotalMensalData(Date dataReferencia, ArrayList<TotalMensal> totais) {
        this.dataReferencia = dataReferencia;
        this.totais = totais;
    }

}
