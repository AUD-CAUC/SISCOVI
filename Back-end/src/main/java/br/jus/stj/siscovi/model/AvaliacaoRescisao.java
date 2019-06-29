package br.jus.stj.siscovi.model;

import java.util.List;

public class AvaliacaoRescisao {

    int codContrato;
    User user;
    List<CalculoPendenteRescisaoModel> calculosAvaliados;

    public AvaliacaoRescisao(int codContrato, User user, List<CalculoPendenteRescisaoModel> calculosAvaliados) {
        this.codContrato = codContrato;
        this.user = user;
        this.calculosAvaliados = calculosAvaliados;
    }

    public int getCodContrato() {
        return codContrato;
    }

    public User getUser() {
        return user;
    }

    public List<CalculoPendenteRescisaoModel> getCalculosAvaliados() {
        return calculosAvaliados;
    }

}
