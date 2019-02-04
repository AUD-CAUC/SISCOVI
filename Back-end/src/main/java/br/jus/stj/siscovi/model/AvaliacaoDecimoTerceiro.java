package br.jus.stj.siscovi.model;

import java.util.List;

public class AvaliacaoDecimoTerceiro {

    private final List<DecimoTerceiroPendenteModel>  decimosTerceirosPendentes;
    private final User user;
    private final int codigoContrato;

    public AvaliacaoDecimoTerceiro(List<DecimoTerceiroPendenteModel> decimosTerceirosPendentes, User user, int codigoContrato) {
        this.decimosTerceirosPendentes = decimosTerceirosPendentes;
        this.user = user;
        this.codigoContrato = codigoContrato;
    }

    public List<DecimoTerceiroPendenteModel> getDecimosTerceirosPendentes() {
        return decimosTerceirosPendentes;
    }

    public User getUser() {
        return user;
    }

    public int getCodigoContrato() {
        return codigoContrato;
    }

    @Override
    public String toString() {
        return "AvaliacaoDecimoTerceiro{" +
                "decimosTerceirosPendentes=" + decimosTerceirosPendentes +
                ", user=" + user +
                ", codigoContrato=" + codigoContrato +
                '}';
    }
}