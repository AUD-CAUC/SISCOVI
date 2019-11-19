package br.jus.stj.siscovi.model;

public class AvaliacaoResiduais {
    private int codTerceirizadoContrato;
    private User user;

    public AvaliacaoResiduais(int codTerceirizadoContrato, User user) {
        this.codTerceirizadoContrato = codTerceirizadoContrato;
        this.user = user;
    }

    public int getCodTerceirizadoContrato() {
        return this.codTerceirizadoContrato;
    }

    public User getUser() {
        return this.user;
    }
}
