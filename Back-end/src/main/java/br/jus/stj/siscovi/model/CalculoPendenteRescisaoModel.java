package br.jus.stj.siscovi.model;

public class CalculoPendenteRescisaoModel {
    private final int cod;
    private final CalcularRescisaoModel calcularRescisaoModel;
    private final String nomeTerceirizado;
    private final String nomeCargo;
    private final String status;
    private final float total;
    private String observacoes;
    private TipoRestituicao tipoRestituicao;
    private RegistroTipoRescisao tipoRescisao;

    public CalculoPendenteRescisaoModel(int cod, CalcularRescisaoModel calcularRescisaoModel, String nomeTerceirizado, String nomeCargo, String status, float total) {
        this.cod = cod;
        this.calcularRescisaoModel = calcularRescisaoModel;
        this.nomeTerceirizado = nomeTerceirizado;
        this.nomeCargo = nomeCargo;
        this.status = status;
        this.total = total;
    }

    public CalcularRescisaoModel getCalcularFeriasModel() {
        return calcularRescisaoModel;
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
                "calcularFeriasModel=" + calcularRescisaoModel +
                ", nomeTerceirizado='" + nomeTerceirizado + '\'' +
                ", nomeCargo='" + nomeCargo + '\'' +
                ", status='" + status + '\'' +
                ", total=" + total +
                ", observacoes='" + observacoes + '\'' +
                '}';
    }
}
