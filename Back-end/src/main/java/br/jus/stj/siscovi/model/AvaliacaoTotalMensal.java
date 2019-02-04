package br.jus.stj.siscovi.model;

import java.util.List;

public class AvaliacaoTotalMensal {
    private List<TotalMensalPendenteModel> totalMensalPendenteModels;
    private int codigoContrato;
    private User user;

    public List<TotalMensalPendenteModel> getTotalMensalPendenteModels() {
        return totalMensalPendenteModels;
    }

    public int getCodigoContrato() {
        return codigoContrato;
    }

    public User getUser() {
        return user;
    }
}
