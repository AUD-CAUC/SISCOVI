package br.jus.stj.siscovi.test;

import br.jus.stj.siscovi.calculos.Ferias;
import br.jus.stj.siscovi.calculos.RestituicaoFerias;
import br.jus.stj.siscovi.dao.sql.*;
import br.jus.stj.siscovi.model.ValorRestituicaoFeriasModel;


import br.jus.stj.siscovi.dao.ConnectSQLServer;

import java.sql.*;

public class TesteRestituicaoFerias {

    public static void main(String[] args){

        ConnectSQLServer connectSQLServer = new ConnectSQLServer();

        RestituicaoFerias restituicaoFerias = new RestituicaoFerias(connectSQLServer.dbConnect());
        ConsultaTSQL consulta = new ConsultaTSQL(connectSQLServer.dbConnect());
        DeleteTSQL delete = new DeleteTSQL(connectSQLServer.dbConnect());
        UpdateTSQL update = new UpdateTSQL(connectSQLServer.dbConnect());

        Ferias ferias = new Ferias(connectSQLServer.dbConnect());

        int vCodContrato = 1;//consulta.RetornaCodContratoAleatorio();
        int retorno;
        int vCodTerceirizadoContrato = 1;//consulta.RetornaCodTerceirizadoAleatorio(vCodContrato);
        String vTipoRestituicao = String.valueOf("RESGATE");
        String vLoginAtualizacao = String.valueOf("VSSOUSA");
        int vParcela = 1;
        int vDiasVendidos = 5;
        float vValorMovimentado = 1000;

        System.out.print("Dados do teste\nCOD_CONTRATO: " + vCodContrato + " COD_TERCEIRIZADO_CONTRATO: " +
                vCodTerceirizadoContrato + "\n");
        System.out.print("Tipo de restituição: " + vTipoRestituicao + "\nDias vendidos: " + vDiasVendidos + "\n");

        Date vInicioFerias = Date.valueOf("2018-09-01");
        Date vFimFerias = Date.valueOf("2018-09-14");
        Date vInicioPeriodoAquisitivo = ferias.DataPeriodoAquisitivo(vCodTerceirizadoContrato, 1);
        Date vFimPeriodoAquisitivo = ferias.DataPeriodoAquisitivo(vCodTerceirizadoContrato, 2);

        ValorRestituicaoFeriasModel restituicao  = restituicaoFerias.CalculaRestituicaoFerias(vCodTerceirizadoContrato,
                vDiasVendidos, vInicioFerias, vFimFerias, vInicioPeriodoAquisitivo, vFimPeriodoAquisitivo);

        System.out.println(restituicao.getValorFerias());
        System.out.println(restituicao.getValorTercoConstitucional());
        System.out.println(restituicao.getValorIncidenciaFerias());
        System.out.println(restituicao.getValorIncidenciaTercoConstitucional());

        retorno = restituicaoFerias.RegistraRestituicaoFerias(vCodTerceirizadoContrato, vTipoRestituicao, vDiasVendidos,
                vInicioFerias, vFimFerias, vInicioPeriodoAquisitivo, vFimPeriodoAquisitivo, vParcela,
                vValorMovimentado, restituicao.getValorFerias(), restituicao.getValorTercoConstitucional(),
                restituicao.getValorIncidenciaFerias(), restituicao.getValorIncidenciaTercoConstitucional(), vLoginAtualizacao);

        //restituicaoFerias.RecalculoRestituicaoFerias(retorno, "MOVIMENTAÇÃO", vDiasVendidos, vInicioFerias, vFimFerias, vInicioPeriodoAquisitivo, vFimPeriodoAquisitivo, vParcela, 0,0,0,0,0, "Teste");

        //restituicaoFerias.RecalculoRestituicaoFerias(retorno, vTipoRestituicao, vDiasVendidos, vInicioFerias, vFimFerias, vInicioPeriodoAquisitivo, vFimPeriodoAquisitivo,
        //         0, 0, 0,
        //       0, 0, 0, "SYSTEM");




        //delete.DeleteSaldoResidualFerias(retorno);
        //delete.DeleteHistRestituicaoFerias(retorno);
        //delete.DeleteRestituicaoFerias(retorno);

    }

}
