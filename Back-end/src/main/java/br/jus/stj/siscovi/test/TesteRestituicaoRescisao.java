package br.jus.stj.siscovi.test;

import br.jus.stj.siscovi.calculos.RestituicaoRescisao;
import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.sql.ConsultaTSQL;
import br.jus.stj.siscovi.dao.sql.DeleteTSQL;
import br.jus.stj.siscovi.dao.sql.InsertTSQL;
import br.jus.stj.siscovi.model.ValorRestituicaoRescisaoModel;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TesteRestituicaoRescisao {

    public static void main(String[] args){

        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        PreparedStatement preparedStatement;
        ResultSet resultSet;

        RestituicaoRescisao restituicaoRescisao = new RestituicaoRescisao(connectSQLServer.dbConnect());
        ConsultaTSQL consulta = new ConsultaTSQL(connectSQLServer.dbConnect());
        DeleteTSQL delete = new DeleteTSQL(connectSQLServer.dbConnect());

        int vCodContrato = 1; //consulta.RetornaCodContratoAleatorio();
        int vCodTerceirizadoContrato = 8; //consulta.RetornaCodTerceirizadoAleatorio(vCodContrato);
        int vRetorno;
        String vTipoRestituicao = String.valueOf("RESGATE");
        String vTipoRescisao = String.valueOf("SEM JUSTA CAUSA");
        String vLoginAtualizacao = String.valueOf("VSSOUSA");
        Date vDataInicioContagemDecTer = Date.valueOf("2017-12-31");
        Date vDataDesligamento = Date.valueOf("2017-12-31");
        Date vDataInicioFeriasIntegrais = Date.valueOf("2016-08-05");
        Date vDataFimFeriasIntegrais = Date.valueOf("2017-08-04");
        Date vDataInicioFeriasProporcionais = Date.valueOf("2017-08-05");
        Date vDataFimFeriasProporcionais = Date.valueOf("2017-12-31");


        System.out.print("Dados do teste\nCOD_CONTRATO: " + vCodContrato + " COD_TERCEIRIZADO_CONTRATO: " +
                vCodTerceirizadoContrato + "\n");
        System.out.print("Tipo de restituição: " + vTipoRestituicao + "\n" + "Tipo de rescisão: " + vTipoRescisao
                + "\n" + "Data do desligamento: " + vDataDesligamento + "\n" + "Login usuário: " + vLoginAtualizacao + "\n");

        ValorRestituicaoRescisaoModel restituicao = restituicaoRescisao.CalculaRestituicaoRescisao(vCodTerceirizadoContrato,
                vDataDesligamento,
                vDataInicioFeriasIntegrais,
                vDataFimFeriasIntegrais,
                vDataInicioFeriasProporcionais,
                vDataFimFeriasProporcionais);

        System.out.println("Décimo terceiro: " + restituicao.getValorDecimoTerceiro());
        System.out.println("Incidência de Décimo terceiro: " + restituicao.getValorIncidenciaDecimoTerceiro());
        System.out.println("Multa FGTS do Décimo terceiro: " + restituicao.getValorFGTSDecimoTerceiro());
        System.out.println("Férias integrais: " + restituicao.getValorFeriasIntegral());
        System.out.println("Terço integral: " + restituicao.getValorTercoIntegral());
        System.out.println("Incidência de férias integrais: " + restituicao.getValorIncidenciaFeriasIntegral());
        System.out.println("Incidência de terço integral: " + restituicao.getValorIncidenciaTercoIntegral());
        System.out.println("MULTA FGTS de férias integrais: " + restituicao.getValorFGTSFeriasIntegral());
        System.out.println("MULTA FGTS de terço integral: " + restituicao.getValorFGTSTercoIntegral());
        System.out.println("Férias proporcionais: " + restituicao.getValorFeriasProporcional());
        System.out.println("Terço proporcional: " + restituicao.getValorTercoProporcional());
        System.out.println("Incidência de férias proporcionais: " + restituicao.getValorIncidenciaFeriasProporcional());
        System.out.println("Incidência de terõ proporcional: " + restituicao.getValorIncidenciaTercoProporcional());
        System.out.println("MULTA FGTS de férias proporcionais: " + restituicao.getValorFGTSFeriasProporcional());
        System.out.println("MULTA FGTS de terço proporcional: " + restituicao.getValorFGTSTercoProporcional());
        System.out.println("Décimo terceiro: " + restituicao.getValorFGTSSalario());


        vRetorno = restituicaoRescisao.RegistrarRestituicaoRescisao(vCodTerceirizadoContrato,
                vTipoRestituicao,
                vTipoRescisao,
                vDataDesligamento,
                vDataInicioFeriasIntegrais,
                vDataFimFeriasIntegrais,
                vDataInicioFeriasProporcionais,
                vDataFimFeriasProporcionais,
                vDataInicioContagemDecTer,
                0,
                0,
                0,
                restituicao.getValorDecimoTerceiro(),
                restituicao.getValorIncidenciaDecimoTerceiro(),
                restituicao.getValorFGTSDecimoTerceiro(),
                restituicao.getValorFeriasIntegral(),
                restituicao.getValorTercoIntegral(),
                restituicao.getValorIncidenciaFeriasIntegral(),
                restituicao.getValorIncidenciaTercoIntegral(),
                restituicao.getValorFGTSFeriasIntegral(),
                restituicao.getValorFGTSTercoIntegral(),
                restituicao.getValorFeriasProporcional(),
                restituicao.getValorTercoProporcional(),
                restituicao.getValorIncidenciaFeriasProporcional(),
                restituicao.getValorIncidenciaTercoProporcional(),
                restituicao.getValorFGTSFeriasProporcional(),
                restituicao.getValorFGTSTercoProporcional(),
                restituicao.getValorFGTSSalario(),
                vLoginAtualizacao);

        //restituicaoRescisao.RecalculoRestituicaoRescisao(vRetorno, "RESGATE", vTipoRescisao, vDataDesligamento, vDataInicioFerias, 0, 0, 0, 0, 0,0 ,0,0,0, 0, "SYSTEM");

        //delete.DeleteHistRestituicaoRescisao(vRetorno);
        //delete.DeleteSaldoResidualRescisao(vRetorno);
        //delete.DeleteRestituicaoRescisao(vRetorno);

    }

}
