package br.jus.stj.siscovi.model;

public class CalculoPendenteModel {
    private final int cod;
    private final CalcularFeriasModel calcularFeriasModel;
    private final String nomeTerceirizado;
    private final String nomeCargo;
    private final String status;
    private final float total;
    private String observacoes;
    private TipoRestituicao tipoRestituicao;

    public CalculoPendenteModel(int cod, CalcularFeriasModel calcularFeriasModel, String nomeTerceirizado, String nomeCargo, String status, float total) {
        this.cod = cod;
        this.calcularFeriasModel = calcularFeriasModel;
        this.nomeTerceirizado = nomeTerceirizado;
        this.nomeCargo = nomeCargo;
        this.status = status;
        this.total = total;
    }

    public CalcularFeriasModel getCalcularFeriasModel() {
        return calcularFeriasModel;
    }

    public String getNomeTerceirizado() {
        return nomeTerceirizado;
    }

    public String getNomeCargo() {
        return nomeCargo;
    }

    public String getStatus() {
        return status;
    }

    public float getTotal() {
        return total;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public int getCod() {
        return cod;
    }

    @Override
    public String toString() {
        return "CalculoPendenteModel{" +
                "calcularFeriasModel=" + calcularFeriasModel +
                ", nomeTerceirizado='" + nomeTerceirizado + '\'' +
                ", nomeCargo='" + nomeCargo + '\'' +
                ", status='" + status + '\'' +
                ", total=" + total +
                ", observacoes='" + observacoes + '\'' +
                '}';
    }
}
