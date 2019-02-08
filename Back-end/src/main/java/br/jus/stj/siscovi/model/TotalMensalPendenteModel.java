package br.jus.stj.siscovi.model;

public class TotalMensalPendenteModel {
    private final ListaTotalMensalData totaisMensais;
    private final String status;
    private String observacoes;
    private int codigoContrato;
    public TotalMensalPendenteModel(ListaTotalMensalData totaisMensais, String status) {
        this.totaisMensais = totaisMensais;
        this.status = status;
    }
    public String getStatus() {
        return status;
    }
    public ListaTotalMensalData getTotaisMensais() {
        return totaisMensais;
    }
    public String getObservacoes() {
        return observacoes;
    }
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
    @Override
    public String toString() {
        return "TotalMensalPendenteModel{" +
                "totaisMensais=" + totaisMensais +
                ", status='" + status + '\'' +
                ", observacoes='" + observacoes + '\'' +
                ", codigoContrato='" + '\'' +
                '}';
    }
}
