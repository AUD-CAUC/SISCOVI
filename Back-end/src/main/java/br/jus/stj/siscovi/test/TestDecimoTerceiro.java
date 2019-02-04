package br.jus.stj.siscovi.test;

import br.jus.stj.siscovi.calculos.DecimoTerceiro;
import br.jus.stj.siscovi.dao.ConnectSQLServer;

import java.sql.*;

public class TestDecimoTerceiro {

    public static void main (String args[]) {

        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        PreparedStatement preparedStatement;
        ResultSet resultSet;

        DecimoTerceiro decimoTerceiro = new DecimoTerceiro(connectSQLServer.dbConnect());

        int vCodTerceirizadoContrato = 0;
        int vCodContrato = 0;
        int vAno = 2017;
        Date vDataInicioContagem = null;

        //Carregamento do código do contrato.

        try {

            preparedStatement = connectSQLServer.dbConnect().prepareStatement("SELECT TOP 1 cod\n" +
                                                                 " FROM tb_contrato;");

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vCodContrato = resultSet.getInt(1);

            }

        } catch (SQLException sqle) {

            throw new NullPointerException("Não foi possível carregar o código do contrato.");

        }

        //Carregamento do código do terceirizado no contrato.

        try {

            preparedStatement = connectSQLServer.dbConnect().prepareStatement("SELECT TOP 1 cod\n" +
                                                                 " FROM tb_terceirizado_contrato" +
                                                                 " WHERE cod_contrato = ?;");

            preparedStatement.setInt(1, vCodContrato);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vCodTerceirizadoContrato = resultSet.getInt(1);

            }

        } catch (SQLException sqle) {

            throw new NullPointerException("Não foi possível carregar o código do terceirizado.");

        }

        vDataInicioContagem = decimoTerceiro.RetornaDataInicioContagem(vCodTerceirizadoContrato, vAno);

        System.out.print("Teste da função RetornaDataInicioContagem\n");
        System.out.print("Código do contrato: " + String.valueOf(vCodContrato) + " | Código do terceirizado no contrato: " + String.valueOf(vCodTerceirizadoContrato) + "\n");
        System.out.print("Data de início da contagem do terceirizado: " + vDataInicioContagem + "\n");

    }

}
