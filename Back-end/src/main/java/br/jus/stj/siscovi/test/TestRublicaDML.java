package br.jus.stj.siscovi.test;

import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.sql.ConsultaTSQL;
import br.jus.stj.siscovi.dao.sql.DeleteTSQL;
import br.jus.stj.siscovi.dao.sql.InsertTSQL;
import br.jus.stj.siscovi.model.RegistroRubricaModel;

public class TestRublicaDML {

    public static void main (String[] args) {

        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        InsertTSQL insert = new InsertTSQL(connectSQLServer.dbConnect());
        ConsultaTSQL consulta = new ConsultaTSQL(connectSQLServer.dbConnect());
        DeleteTSQL delete = new DeleteTSQL(connectSQLServer.dbConnect());

        String nome = "TESTE";
        String sigla = "TEST";
        String descricao = "Teste de inserção de rubrica.";
        String loginAtualizacao = "VSSOUSA";

        int retornoCod = insert.InsertRubrica(nome, sigla, descricao, loginAtualizacao);
        RegistroRubricaModel registro = consulta.RetornaRegistroRubrica(retornoCod);

        System.out.print(registro.getpCod() + "\n");
        System.out.print(registro.getpNome() + "\n");
        System.out.print(registro.getpSigla() + "\n");
        System.out.print(registro.getpDescricao() + "\n");
        System.out.print(registro.getpLoginAtualizacao() + "\n");
        System.out.print(registro.getpDataAtualizacao() + "\n");

        delete.DeleteRegistro(retornoCod, "TB_RUBRICA");

    }

}
