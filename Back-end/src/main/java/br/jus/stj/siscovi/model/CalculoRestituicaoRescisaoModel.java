package br.jus.stj.siscovi.model;

public class CalculoRestituicaoRescisaoModel {
    private TerceirizadoRescisao terceirizadoRescisao;
    private ValorRestituicaoRescisaoModel valorRestituicaoRescisao;

    public CalculoRestituicaoRescisaoModel(TerceirizadoRescisao terceirizadoRescisao, ValorRestituicaoRescisaoModel valorRestituicaoRescisao) {
        this.terceirizadoRescisao = terceirizadoRescisao;
        this.valorRestituicaoRescisao = valorRestituicaoRescisao;
    }
}
