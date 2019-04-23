package br.jus.stj.siscovi.model;

import java.sql.Date;

public class TerceirizadoDecimoTerceiro {
    private int cod;
    private final int codigoTerceirizadoContrato;
    private final String nomeTerceirizado;
    private final Date inicioContagem;
    private final float valorDisponivel;
    private String tipoRestituicao;
    private final float valorMovimentado;
    private int parcelas;
    private Date fimContagem;
    private ValorRestituicaoDecimoTerceiroModel valoresDecimoTerceiro;
    private String id;
    private String nomeCargo;
    private boolean emAnalise;
    private boolean restituidoAnoPassado;

    public TerceirizadoDecimoTerceiro(int codigoTerceirizadoContrato, String nomeTerceirizado, Date inicioContagem, float valorDisponivel, float valorMovimentado) {
        this.codigoTerceirizadoContrato = codigoTerceirizadoContrato;
        this.nomeTerceirizado = nomeTerceirizado;
        this.inicioContagem = inicioContagem;
        this.valorDisponivel = Math.round(valorDisponivel * 100.0f) / 100.0f;
        this.valorMovimentado = valorMovimentado;
    }

    public void setTipoRestituicao(String tipoRestituicao) {
        this.tipoRestituicao = tipoRestituicao;
    }

    public int getParcelas() {
        return parcelas;
    }

    public void setFimContagem(Date fimContagem) {
        this.fimContagem = fimContagem;
    }

    public int getCodigoTerceirizadoContrato() {
        return codigoTerceirizadoContrato;
    }

    public String getNomeTerceirizado() {
        return nomeTerceirizado;
    }

    public Date getInicioContagem() {
        return inicioContagem;
    }

    public float getValorDisponivel() {
        return valorDisponivel;
    }

    public String getTipoRestituicao() {
        return tipoRestituicao;
    }

    public float getValorMovimentado() {
        return valorMovimentado;
    }

    public void setValoresDecimoTerceiro(ValorRestituicaoDecimoTerceiroModel valoresDecimoTerceiro) {
        this.valoresDecimoTerceiro = valoresDecimoTerceiro;
    }

    public ValorRestituicaoDecimoTerceiroModel getValoresDecimoTerceiro() {
        return valoresDecimoTerceiro;
    }

    public String getId() {
        return id;
    }

    public void setParcelas(int parcelas) {
        this.parcelas = parcelas;
    }

    public void setNomeCargo(String nomeCargo) {
        this.nomeCargo = nomeCargo;
    }

    public int getCod() {
        return cod;
    }

    public boolean getEmAnalise() { return emAnalise; }

    public void setEmAnalise(boolean emAnalise) { this.emAnalise = emAnalise; }

    public boolean getRestituidoAnoPassado() { return this.restituidoAnoPassado; }

    public void setRestituidoAnoPassado(boolean restituidoAnoPassado) { this.restituidoAnoPassado = restituidoAnoPassado; }



    @Override
    public String toString() {
        return "TerceirizadoDecimoTerceiro{" +
                "cod=" + cod +
                ", codigoTerceirizadoContrato=" + codigoTerceirizadoContrato +
                ", nomeTerceirizado='" + nomeTerceirizado + '\'' +
                ", inicioContagem=" + inicioContagem +
                ", valorDisponivel=" + valorDisponivel +
                ", tipoRestituicao='" + tipoRestituicao + '\'' +
                ", valorMovimentado=" + valorMovimentado +
                ", parcelas=" + parcelas +
                ", fimContagem=" + fimContagem +
                ", valoresDecimoTerceiro=" + valoresDecimoTerceiro +
                ", id='" + id + '\'' +
                ", nomeCargo='" + nomeCargo + '\'' +
                '}';
    }
}