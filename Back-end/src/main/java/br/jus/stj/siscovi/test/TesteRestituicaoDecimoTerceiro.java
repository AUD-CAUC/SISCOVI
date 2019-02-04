package br.jus.stj.siscovi.test;

import br.jus.stj.siscovi.calculos.DecimoTerceiro;
import br.jus.stj.siscovi.calculos.RestituicaoDecimoTerceiro;
import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.sql.ConsultaTSQL;
import br.jus.stj.siscovi.dao.sql.DeleteTSQL;
import br.jus.stj.siscovi.model.ValorRestituicaoDecimoTerceiroModel;

import java.sql.*;

public class TesteRestituicaoDecimoTerceiro {

    public static void main(String[] args){

        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        PreparedStatement preparedStatement;
        ResultSet resultSet;

        RestituicaoDecimoTerceiro restituicaoDecimoTerceiro = new RestituicaoDecimoTerceiro(connectSQLServer.dbConnect());
        ConsultaTSQL consulta = new ConsultaTSQL(connectSQLServer.dbConnect());
        DecimoTerceiro decimoTerceiro = new DecimoTerceiro(connectSQLServer.dbConnect());
        DeleteTSQL delete = new DeleteTSQL(connectSQLServer.dbConnect());

        int vCodContrato = consulta.RetornaCodContratoAleatorio();
        Integer retorno;
        int vCodTerceirizadoContrato = consulta.RetornaCodTerceirizadoAleatorio(vCodContrato);
        String vTipoRestituicao = String.valueOf("MOVIMENTAÇÃO");
        String vLoginAtualizacao = String.valueOf("VSSOUSA");
        float vValorMovimentado = 1500;
        int vNumeroParcela = 0;
        Date vDataInicioContagem = decimoTerceiro.RetornaDataInicioContagem(vCodTerceirizadoContrato, 2016);
        String vDataFim = String.valueOf(vDataInicioContagem.toLocalDate().getYear()) + "-12-31";

        System.out.print(vDataFim);
        Date vDataFimContagem = Date.valueOf(vDataFim);

        System.out.print("Dados do teste\nCOD_CONTRATO: " + vCodContrato + " COD_TERCEIRIZADO_CONTRATO: " +
                vCodTerceirizadoContrato + "\n");
        System.out.print("Tipo de restituição: " + vTipoRestituicao + "\n" + "Data de início da contagem: " + vDataInicioContagem
        + "\n" + "Data fim da contagem: " + vDataFimContagem + "\n");

        ValorRestituicaoDecimoTerceiroModel restituicao = restituicaoDecimoTerceiro.CalculaRestituicaoDecimoTerceiro(
                vCodTerceirizadoContrato, vNumeroParcela, vDataInicioContagem, vDataFimContagem);

        System.out.println(restituicao.getValorDecimoTerceiro());
        System.out.println(restituicao.getValorIncidenciaDecimoTerceiro());

        retorno = restituicaoDecimoTerceiro.RegistraRestituicaoDecimoTerceiro(vCodTerceirizadoContrato,
                vTipoRestituicao,
                vNumeroParcela,
                vDataInicioContagem,
                restituicao.getValorDecimoTerceiro(),
                restituicao.getValorIncidenciaDecimoTerceiro(),
                vValorMovimentado,
                vLoginAtualizacao);

        //restituicaoDecimoTerceiro.RecalculoRestituicaoDecimoTerceiro(retorno, vTipoRestituicao,0,vDataInicioContagem,0,0,0,"SYSTEM" );

        //delete.DeleteHistRestituicaoDecimoTerceiro(retorno);
        //delete.DeleteSaldoResidualDecimoTerceiro(retorno);
        //delete.DeleteRestituicaoDecimoTerceiro(retorno);

    }

}
