package br.jus.stj.siscovi.model;

import java.util.List;

public class AvaliacaoFerias {
    int codContrato;
    User user;
    List<CalculoPendenteModel> calculosAvaliados;

    public AvaliacaoFerias(int codContrato, User user, List<CalculoPendenteModel> calculosAvaliados) {
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

    public List<CalculoPendenteModel> getCalculosAvaliados() {
        return calculosAvaliados;
    }
}