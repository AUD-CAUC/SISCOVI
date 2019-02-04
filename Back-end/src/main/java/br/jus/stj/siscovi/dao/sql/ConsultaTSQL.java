package br.jus.stj.siscovi.dao.sql;

import br.jus.stj.siscovi.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ConsultaTSQL {

    private Connection connection;

    public ConsultaTSQL(Connection connection) {

        this.connection = connection;

    }

    /**
     *Função que retorna o código de um contrato aleatório no banco de dados.
     *
     * @return Um código (cod) de contrato aleatório.
     */

    public int RetornaCodContratoAleatorio () {

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        int vCodContrato = 0;

        //Carregamento do código do contrato.

        try {

            preparedStatement = connection.prepareStatement("SELECT TOP 1 cod\n" +
                    " FROM tb_contrato;");

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vCodContrato = resultSet.getInt(1);

            }

        } catch (SQLException sqle) {

            throw new NullPointerException("Não foi possível carregar o código do contrato.");

        }

        return vCodContrato;

    }

    /**
     *Função que retorna o código de um registro de terceirizado no contrato aleatório do banco de dados.
     * Esse registro é responsável por "linkar" terceirizado e contrato de alguma prestadora de serviço.
     *
     * @param pCodContrato;
     *
     * @return Um código (cod) de terceirizado_contrato aleatório.
     */

    public int RetornaCodTerceirizadoAleatorio (int pCodContrato) {

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        int vCodTerceirizadoContrato = 0;

        //Carregamento do código do terceirizado no contrato.

        try {

            preparedStatement = connection.prepareStatement("SELECT cod\n" +
                    " FROM tb_terceirizado_contrato\n" +
                    " WHERE cod_contrato = ?;");

            preparedStatement.setInt(1, pCodContrato);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vCodTerceirizadoContrato = resultSet.getInt(1);

            }

        } catch (SQLException sqle) {

            throw new NullPointerException("Não foi possível carregar o código do terceirizado.");

        }

        return vCodTerceirizadoContrato;

    }

    /**
     *Função que retorna a data de disponibilização de um terceirizado em determinado contrato.
     *
     * @param pCodTerceirizadoContrato;
     *
     * @return A data de disponibilização do terceirizado em um contrato.
     */

    public Date RetornaDataDisponibilizacaoTerceirizado (int pCodTerceirizadoContrato) {

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        Date vDataDisponibilizacao = null;

        //Carregamento da data de disponibilização do terceirizado.

        try {

            preparedStatement = connection.prepareStatement("SELECT DATA_DISPONIBILIZACAO\n" +
                    " FROM tb_terceirizado_contrato\n" +
                    " WHERE cod = ?;");

            preparedStatement.setInt(1, pCodTerceirizadoContrato);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vDataDisponibilizacao = resultSet.getDate(1);

            }

        } catch (SQLException sqle) {

            throw new NullPointerException("Não foi possível carregar a data de disponibilização do terceirizado.");

        }

        return vDataDisponibilizacao;

    }

    /**
     *Função que retorna a data de início de um contrato registrado no banco de dados.
     *
     * @param pCodContrato;
     *
     * @return A data de início de um contrato.
     */

    public Date RetornaDataInicioContrato (int pCodContrato) {

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        Date dataInicioContrato = null;

        //Carregamento da data início do contrato.

        try {

            preparedStatement = connection.prepareStatement("SELECT MIN(data_inicio_vigencia)\n" +
                    " FROM tb_evento_contratual\n" +
                    " WHERE cod_contrato = ?");

            preparedStatement.setInt(1, pCodContrato);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                dataInicioContrato = resultSet.getDate(1);

            }

        } catch (SQLException sqle) {

            throw new NullPointerException("Não foi possível carregar o código do contrato.");

        }

        return dataInicioContrato;

    }

    /**
     *Função que retorna o código de um tipo de restituição.
     *
     * @param pTipoRestituicao;
     *
     * @return O código (cod) do registro correspondente a um tipo de restituição.
     */

    public int RetornaCodTipoRestituicao (String pTipoRestituicao) {

        PreparedStatement preparedStatement;
        ResultSet resultSet;
        int vCodTipoRestituicao = 0;

        /*Atribuição do cod do tipo de restituição.*/

        try {

            preparedStatement = connection.prepareStatement("SELECT COD" + " FROM TB_TIPO_RESTITUICAO" + " WHERE UPPER(nome) = UPPER(?)");

            preparedStatement.setString(1, pTipoRestituicao);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vCodTipoRestituicao = resultSet.getInt(1);

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        if (vCodTipoRestituicao == 0) {

            throw new NullPointerException("Tipo de restituição não encontrada.");

        }

        return vCodTipoRestituicao;

    }


/*
    /**
     *Função que retorna o tipo de restituição correspondente a um código.
     *
     * @param pCodTipoRestituicao;
     *
     * @return O tipo (string) do registro correspondente a um cod de tipo de restituição.
     */
/*
    public String RetornaTipoRestituicao (int pCodTipoRestituicao) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        String vTipoRestituicao = null;
        /*Atribuição do tipo de restituição.*/
/*
        try {
            preparedStatement = connection.prepareStatement("SELECT NOME" + " FROM TB_TIPO_RESTITUICAO" + " WHERE cod = ?");
            preparedStatement.setInt(1, pCodTipoRestituicao);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                vTipoRestituicao = resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (vTipoRestituicao == null) {
            throw new NullPointerException("Tipo de restituição não encontrada.");
        }
        return vTipoRestituicao;
    }
*/
    /**
     *Função que retorna o código de um tipo de rescisão.
     *
     * @param pTipoRescisao;
     *
     * @return O código (cod) do registro correspondente a um tipo de rescisão.
     */

    public int RetornaCodTipoRescisao (String pTipoRescisao) {

        PreparedStatement preparedStatement;
        ResultSet resultSet;
        int vCodTipoRescisao = 0;

        /*Atribuição do cod do tipo de rescisão.*/

        try {

            preparedStatement = connection.prepareStatement("SELECT COD" + " FROM tb_tipo_rescisao" + " WHERE UPPER(TIPO_RESCISAO) = UPPER(?)");

            preparedStatement.setString(1, pTipoRescisao);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vCodTipoRescisao = resultSet.getInt(1);

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        if (vCodTipoRescisao == 0) {

            throw new NullPointerException("Tipo de rescisão não encontrada.");

        }

        return vCodTipoRescisao;

    }

    /**
     *Função que retorna a lista de código "função terceirizado" e o códgigo de sua "função" de um
     * terceirizado em uma determinada data.
     *
     * @param pCodTerceirizadoContrato;
     * @param pDataReferencia;
     *
     * @return Lista contendo o código "função terceirizado" (cod) e o código de "função" (cod_funcao) de um terceirizado.
     */

    public ArrayList<CodFuncaoContratoECodFuncaoTerceirizadoModel> SelecionaFuncaoContratoEFuncaoTerceirizado (int pCodTerceirizadoContrato, Date pDataReferencia) {

        /*Busca as funções que um funcionário exerceu no mês de cálculo.*/

        ArrayList<CodFuncaoContratoECodFuncaoTerceirizadoModel> tuplas = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT ft.cod_funcao_contrato, " +
                "ft.cod" +
                " FROM tb_funcao_terceirizado ft" +
                " WHERE ft.cod_terceirizado_contrato = ?" +
                " AND ((((CONVERT(date, CONVERT(varchar, year(ft.data_inicio)) + '-' + CONVERT(varchar, month(ft.data_inicio)) + '-01')) <= ?)" +
                " AND" +
                " (ft.data_fim >= ?))" +
                " OR" +
                " (((CONVERT(date, CONVERT(varchar, year(ft.data_inicio)) + '-' + CONVERT(varchar, month(ft.data_inicio)) + '-01')) <= ?) " +
                "AND" +
                " (ft.data_fim IS NULL)))")){

            preparedStatement.setInt(1, pCodTerceirizadoContrato);
            preparedStatement.setDate(2, pDataReferencia);
            preparedStatement.setDate(3, pDataReferencia);
            preparedStatement.setDate(4, pDataReferencia);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {

                    CodFuncaoContratoECodFuncaoTerceirizadoModel tupla = new CodFuncaoContratoECodFuncaoTerceirizadoModel(resultSet.getInt("COD"), resultSet.getInt("COD_FUNCAO_CONTRATO"));

                    tuplas.add(tupla);

                }

            }

        } catch(SQLException slqe) {
            //slqe.printStackTrace();
            throw new NullPointerException("Problemas durante a consulta ao banco em relação ao terceirizado: " + pCodTerceirizadoContrato);

        }

        return tuplas;

    }

    /**
     *Função que retorna o código do contrato de um terceirizado alocado em um contrato específico.
     *
     * @param pCodTerceirizadoContrato;
     *
     * @return O cod_contrato contido no registro da tabela tb_terceirizado_contrato.
     */

    public int RetornaContratoTerceirizado (int pCodTerceirizadoContrato) {

        PreparedStatement preparedStatement;
        ResultSet resultSet;
        int vCodContrato = 0;

        try {

            preparedStatement = connection.prepareStatement("SELECT tc.cod_contrato FROM tb_terceirizado_contrato tc WHERE tc.cod = ?");

            preparedStatement.setInt(1, pCodTerceirizadoContrato);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vCodContrato = resultSet.getInt(1);

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return vCodContrato;

    }

    /**
     * Recuparação do próximo valor da sequência da chave primária da tabela tb_restituicao_ferias.
     *
     * @return Próximo valor de sequência da chave primária da tabela.
     */

    int RetornaCodSequenceTbRestituicaoFerias() {

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        int vCodTbRestituicaoFerias = 0;

        try {

            preparedStatement = connection.prepareStatement("SELECT ident_current ('TB_RESTITUICAO_FERIAS')");

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vCodTbRestituicaoFerias = resultSet.getInt(1);
                vCodTbRestituicaoFerias = vCodTbRestituicaoFerias + 1;

            }

        } catch (SQLException sqle) {

            throw new NullPointerException("Não foi possível recuperar o número de sequência da chave primária da tabela de restituição de férias.");

        }

        if (vCodTbRestituicaoFerias == 0) {

            throw new NullPointerException("Não foi possível recuperar o número de sequência da chave primária da tabela de restituição de férias.");

        }

        return vCodTbRestituicaoFerias;

    }

    /**
     * Recuparação do próximo valor da sequência da chave primária da tabela tb_restituicao_decimo_terceiro.
     *
     * @return Próximo valor de sequência da chave primária da tabela.
     */

    int RetornaCodSequenceTbRestituicaoDecimoTerceiro () {

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        int vCodTbRestituicaoDecimoTerceiro = 0;

        try {

            preparedStatement = connection.prepareStatement("SELECT ident_current ('TB_RESTITUICAO_DECIMO_TERCEIRO')");

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vCodTbRestituicaoDecimoTerceiro = resultSet.getInt(1);
                vCodTbRestituicaoDecimoTerceiro = vCodTbRestituicaoDecimoTerceiro + 1;

            }

        } catch (SQLException sqle) {

            throw new NullPointerException("Não foi possível recuperar o número de sequência da chave primária da tabela de restituição de décimo terceiro.");

        }

        return vCodTbRestituicaoDecimoTerceiro;

    }

    /**
     * Recuparação do próximo valor da sequência da chave primária da tabela tb_restituicao_rescisao.
     *
     * @return Próximo valor de sequência da chave primária da tabela.
     */

    int RetornaCodSequenceTbRestituicaoRescisao () {

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        int vCodTbRestituicaoRescisao = 0;

        try {

            preparedStatement = connection.prepareStatement("SELECT ident_current ('TB_RESTITUICAO_RESCISAO')");

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vCodTbRestituicaoRescisao = resultSet.getInt(1);
                vCodTbRestituicaoRescisao = vCodTbRestituicaoRescisao + 1;

            }

        } catch (SQLException sqle) {

            throw new NullPointerException("Não foi possível recuperar o número de sequência da chave primária da tabela de restituição de férias.");

        }

        return vCodTbRestituicaoRescisao;

    }

    /**
     * Recuparação do próximo valor da sequência da chave primária da tabela tb_hist_restituicao_ferias.
     *
     * @return Próximo valor de sequência da chave primária da tabela.
     */

    int RetornaCodSequenceTbHistRestituicaoFerias () {

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        int vCodTbHistRestituicaoFerias = 0;

        try {

            preparedStatement = connection.prepareStatement("SELECT ident_current ('TB_HIST_RESTITUICAO_FERIAS')");

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vCodTbHistRestituicaoFerias = resultSet.getInt(1);
                vCodTbHistRestituicaoFerias = vCodTbHistRestituicaoFerias + 1;

            }

        } catch (SQLException sqle) {

            throw new NullPointerException("Não foi possível recuperar o número de sequência da chave primária da tabela de histórico restituição de férias.");

        }

        return vCodTbHistRestituicaoFerias;

    }

    /**
     * Recuparação do próximo valor da sequência da chave primária da tabela tb_hist_restituicao_dec_ter.
     *
     * @return Próximo valor de sequência da chave primária da tabela.
     */

    int RetornaCodSequenceTbHistRestituicaoDecTer () {

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        int vCodTbHistRestituicaoDecTer = 0;

        try {

            preparedStatement = connection.prepareStatement("SELECT ident_current ('TB_HIST_RESTITUICAO_DEC_TER')");

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vCodTbHistRestituicaoDecTer = resultSet.getInt(1);
                vCodTbHistRestituicaoDecTer = vCodTbHistRestituicaoDecTer + 1;

            }

        } catch (SQLException sqle) {

            throw new NullPointerException("Não foi possível recuperar o número de sequência da chave primária da tabela de histórico de restituição de décimo terceiro.");

        }

        return vCodTbHistRestituicaoDecTer;

    }

    /**
     * Recuparação do próximo valor da sequência da chave primária da tabela tb_hist_restituicao_rescisao.
     *
     * @return Próximo valor de sequência da chave primária da tabela.
     */

    int RetornaCodSequenceTbHistRestituicaoRescisao () {

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        int vCodTbHistRestituicaoRescisao = 0;

        try {

            preparedStatement = connection.prepareStatement("SELECT ident_current ('TB_HIST_RESTITUICAO_RESCISAO')");

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vCodTbHistRestituicaoRescisao = resultSet.getInt(1);
                vCodTbHistRestituicaoRescisao = vCodTbHistRestituicaoRescisao + 1;

            }

        } catch (SQLException sqle) {

            throw new NullPointerException("Não foi possível recuperar o número de sequência da chave primária da tabela de histórico de restituição de rescisão.");

        }

        return vCodTbHistRestituicaoRescisao;

    }

    /**
     * Recuparação do próximo valor da sequência da chave primária da tabela requerida.
     *
     * @return Próximo valor de sequência da chave primária da tabela.
     */

    public int RetornaCodSequenceTable(String pNomeTabela) {

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        int vCodSequence = 0;

        try {

            preparedStatement = connection.prepareStatement("SELECT ident_current ('" + pNomeTabela + "')");

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vCodSequence = resultSet.getInt(1);
                vCodSequence = vCodSequence + 1;

            }

        } catch (SQLException sqle) {

            throw new NullPointerException("Não foi possível recuperar o número de sequência da chave primária da tabela " + pNomeTabela +".");

        }

        if (vCodSequence == 0) {

            throw new NullPointerException("Não foi possível recuperar o número de sequência da chave primária da tabela " + pNomeTabela +".");

        }

        return vCodSequence;

    }

    /**
     * Retorna as datas que compõem os subperíodos gerados pelas alterações de percentual no mês.
     *
     * @param pCodContrato;
     * @param pMes;
     * @param pAno;
     * @param pDataReferencia;
     *
     * @return Lista de datas.
     */

    public List<Date> RetornaSubperiodosMesPercentual (int pCodContrato,
                                                       int pMes,
                                                       int pAno,
                                                       Date pDataReferencia) {

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        List<Date> datas = new ArrayList<>();

        try {

            preparedStatement = connection.prepareStatement("SELECT data_inicio AS data" +
                    " FROM tb_percentual_contrato" +
                    " WHERE cod_contrato = ?" +
                    " AND (MONTH(DATA_INICIO) = ?" +
                    " AND \n" +
                    " YEAR(DATA_INICIO) = ?)" +
                    " UNION" +
                    " SELECT data_fim AS data" +
                    " FROM tb_percentual_contrato" +
                    " WHERE cod_contrato = ?" +
                    " AND (MONTH(DATA_FIM)=?" +
                    " AND" + " YEAR(DATA_FIM) = ?)" +
                    " UNION" +
                    " SELECT data_inicio AS data" +
                    " FROM tb_percentual_estatico" +
                    " WHERE (MONTH(DATA_INICIO) = ?" +
                    " AND " + " YEAR(DATA_INICIO) = ?)" +
                    " UNION" +
                    " SELECT data_fim AS data" +
                    " FROM tb_percentual_estatico" +
                    " WHERE (MONTH(DATA_FIM)=?" +
                    " AND" + " YEAR(DATA_FIM)=?)" +
                    " UNION" +
                    " SELECT CASE WHEN ? = 2 THEN" +
                    " EOMONTH(CONVERT(DATE, CONCAT('28/' , ? , '/' ,?), 103))" +
                    " ELSE" + " CONVERT(DATE, CONCAT('30/' , ? , '/' ,?), 103) END AS data" +
                    " EXCEPT" +
                    " SELECT CASE WHEN DAY(EOMONTH(CONVERT(DATE, CONCAT('30/' , ? , '/' ,?), 103))) = 31 THEN" +
                    " CONVERT(DATE, CONCAT('31/' , ? , '/' ,?), 103)" +
                    " ELSE" + " NULL END AS data" +
                    " ORDER BY data ASC");

            preparedStatement.setInt(1, pCodContrato);
            preparedStatement.setInt(2, pMes);
            preparedStatement.setInt(3, pAno);
            preparedStatement.setInt(4, pCodContrato);
            preparedStatement.setInt(5, pMes);
            preparedStatement.setInt(6, pAno);
            preparedStatement.setInt(7, pMes);
            preparedStatement.setInt(8, pAno);
            preparedStatement.setInt(9, pMes);
            preparedStatement.setInt(10, pAno);
            preparedStatement.setInt(11, pMes);
            preparedStatement.setInt(12, pMes);
            preparedStatement.setInt(13, pAno);
            preparedStatement.setInt(14, pMes);
            preparedStatement.setInt(15, pAno);
            preparedStatement.setInt(16, pMes);
            preparedStatement.setInt(17, pAno);
            preparedStatement.setInt(18, pMes);
            preparedStatement.setInt(19, pAno);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                datas.add(resultSet.getDate("data"));

            }

        } catch (SQLException e) {

            throw new NullPointerException("Erro ao tentar carregar as datas referentes ao percentuais. " + " Contrato: " + pCodContrato + ". No perídodo: " + pDataReferencia.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        }

        return datas;

    }

    /**
     * Retorna as datas que compõem os subperíodos gerados pelas alterações de remuneração no mês.
     *
     * @param pCodContrato;
     * @param pMes;
     * @param pAno;
     * @param pCodFuncaoContrato;
     * @param pDataReferencia;
     *
     * @return Lista de datas.
     */

    public List<Date> RetornaSubperiodosMesRemuneracao (int pCodContrato,
                                                        int pMes,
                                                        int pAno,
                                                        int pCodFuncaoContrato,
                                                        Date pDataReferencia) {

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        List<Date> datas = new ArrayList<>();

        try {

            preparedStatement = connection.prepareStatement("SELECT rfc.data_inicio AS data" +
                    " FROM tb_remuneracao_fun_con rfc\n" +
                    " JOIN tb_funcao_contrato fc ON fc.cod = rfc.cod_funcao_contrato" +
                    " WHERE fc.cod_contrato = ?" +
                    " AND fc.cod = ?" +
                    " AND (MONTH(rfc.data_inicio) = ?" +
                    " AND" +
                    " YEAR(rfc.data_inicio) = ?)" +
                    " UNION" +
                    " SELECT rfc.data_fim AS data " +
                    " FROM tb_remuneracao_fun_con rfc" +
                    " JOIN tb_funcao_contrato fc ON fc.cod = rfc.cod_funcao_contrato" +
                    " WHERE fc.cod_contrato = ?" +
                    " AND fc.cod = ?" +
                    " AND (MONTH(rfc.data_fim) = ?" +
                    " AND " +
                    " YEAR(rfc.data_fim) = ?)" +
                    " UNION" +
                    " SELECT CASE WHEN ? = 2 THEN" +
                    " EOMONTH(CONVERT(DATE, CONCAT('28/' , ? , '/' ,?), 103))" +
                    " ELSE" +
                    " CONVERT(DATE, CONCAT('30/' , ? , '/' ,?), 103) END AS data" +
                    " EXCEPT" +
                    " SELECT CASE WHEN DAY(EOMONTH(CONVERT(DATE, CONCAT('30/' , ? , '/' ,?), 103))) = 31 THEN" +
                    " CONVERT(DATE, CONCAT('31/' , ? , '/' ,?), 103)" +
                    " ELSE" +
                    " NULL END AS data" +
                    " ORDER BY DATA ASC");

            preparedStatement.setInt(1, pCodContrato);
            preparedStatement.setInt(2, pCodFuncaoContrato);
            preparedStatement.setInt(3, pMes);
            preparedStatement.setInt(4, pAno);
            preparedStatement.setInt(5, pCodContrato);
            preparedStatement.setInt(6, pCodFuncaoContrato);
            preparedStatement.setInt(7, pMes);
            preparedStatement.setInt(8, pAno);
            preparedStatement.setInt(9, pMes);
            preparedStatement.setInt(10, pMes);
            preparedStatement.setInt(11, pAno);
            preparedStatement.setInt(12, pMes);
            preparedStatement.setInt(13, pAno);
            preparedStatement.setInt(14, pMes);
            preparedStatement.setInt(15, pAno);
            preparedStatement.setInt(16, pMes);
            preparedStatement.setInt(17, pAno);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                datas.add(resultSet.getDate("data"));

            }

        } catch (SQLException e) {

            throw new NullPointerException("Não foi possível determinar os subperíodos do mês provenientes da alteração de remuneração da função: " + pCodFuncaoContrato + " na data referência: " + pDataReferencia);

        }

        return datas;

    }

    /**
     * Retorna as datas que compõem os subperíodos gerados pelas alterações de percentual e remuneração no mês.
     *
     * @param pCodContrato;
     * @param pMes;
     * @param pAno;
     * @param pCodFuncaoContrato;
     * @param pDataReferencia;
     *
     * @return Lista de datas.
     */

    public List<Date> RetornaSubperiodosMesPercentualRemuneracao (int pCodContrato,
                                                                  int pMes,
                                                                  int pAno,
                                                                  int pCodFuncaoContrato,
                                                                  Date pDataReferencia) {

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        List<Date> datas = new ArrayList<>();

        try {

            preparedStatement = connection.prepareStatement("SELECT data_inicio AS data" +
                    " FROM tb_percentual_contrato" +
                    " WHERE cod_contrato = ?" +
                    " AND (MONTH(DATA_INICIO) = ?" +
                    " AND " +
                    " YEAR(DATA_INICIO) = ?)" +
                    " UNION" +
                    " SELECT data_fim AS data" +
                    " FROM tb_percentual_contrato" +
                    " WHERE cod_contrato = ?" +
                    " AND (MONTH(DATA_FIM)=?" +
                    " AND" +
                    " YEAR(DATA_FIM) = ?)" +
                    " UNION" +
                    " SELECT data_inicio AS data" +
                    " FROM tb_percentual_estatico" +
                    " WHERE (MONTH(DATA_INICIO) = ?" +
                    " AND " +
                    " YEAR(DATA_INICIO) = ?)" +
                    " UNION" +
                    " SELECT data_fim AS data" +
                    " FROM tb_percentual_estatico" +
                    " WHERE (MONTH(DATA_FIM)=?" +
                    " AND" +
                    " YEAR(DATA_FIM)=?)" +
                    " UNION" +
                    " SELECT rfc.data_inicio AS data" +
                    " FROM tb_remuneracao_fun_con rfc" +
                    " JOIN tb_funcao_contrato fc ON fc.cod = rfc.cod_funcao_contrato" +
                    " WHERE fc.cod_contrato = ?" +
                    " AND fc.cod = ?" +
                    " AND (MONTH(rfc.data_inicio) = ?" +
                    " AND" +
                    " YEAR(rfc.data_inicio) = ?)" +
                    " UNION" +
                    " SELECT rfc.data_fim AS data " +
                    " FROM tb_remuneracao_fun_con rfc" +
                    " JOIN tb_funcao_contrato fc ON fc.cod = rfc.cod_funcao_contrato" +
                    " WHERE fc.cod_contrato = ?" +
                    " AND fc.cod = ?" +
                    " AND (MONTH(rfc.data_fim) = ?" +
                    " AND " +
                    " YEAR(rfc.data_fim) = ?)" +
                    " UNION" +
                    " SELECT CASE WHEN ? = 2 THEN" +
                    " EOMONTH(CONVERT(DATE, CONCAT('28/' , ? , '/' ,?), 103))" +
                    " ELSE" +
                    " CONVERT(DATE, CONCAT('30/' , ? , '/' ,?), 103) END AS data" +
                    " EXCEPT" +
                    " SELECT CASE WHEN DAY(EOMONTH(CONVERT(DATE, CONCAT('30/' , ? , '/' ,?), 103))) = 31 THEN" +
                    " CONVERT(DATE, CONCAT('31/' , ? , '/' ,?), 103)" +
                    " ELSE" +
                    " NULL END AS data" +
                    " ORDER BY DATA ASC");

            preparedStatement.setInt(1, pCodContrato);
            preparedStatement.setInt(2, pMes);
            preparedStatement.setInt(3, pAno);
            preparedStatement.setInt(4, pCodContrato);
            preparedStatement.setInt(5, pMes);
            preparedStatement.setInt(6, pAno);
            preparedStatement.setInt(7, pMes);
            preparedStatement.setInt(8, pAno);
            preparedStatement.setInt(9, pMes);
            preparedStatement.setInt(10, pAno);
            preparedStatement.setInt(11, pCodContrato);
            preparedStatement.setInt(12, pCodFuncaoContrato);
            preparedStatement.setInt(13, pMes);
            preparedStatement.setInt(14, pAno);
            preparedStatement.setInt(15, pCodContrato);
            preparedStatement.setInt(16, pCodFuncaoContrato);
            preparedStatement.setInt(17, pMes);
            preparedStatement.setInt(18, pAno);
            preparedStatement.setInt(19, pMes);
            preparedStatement.setInt(20, pMes);
            preparedStatement.setInt(21, pAno);
            preparedStatement.setInt(22, pMes);
            preparedStatement.setInt(23, pAno);
            preparedStatement.setInt(24, pMes);
            preparedStatement.setInt(25, pAno);
            preparedStatement.setInt(26, pMes);
            preparedStatement.setInt(27, pAno);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                datas.add(resultSet.getDate("data"));

            }

        } catch (SQLException e) {

            throw new NullPointerException("Não foi possível determinar os subperíodos do mês provenientes da alteração de percentuais e da remuneração da função: " + pCodFuncaoContrato + " na data referência: " + pDataReferencia);

        }

        return datas;

    }

    /**
     * Retorna um registro da tabela tb_restituicao_ferias..
     *
     * @param pCodRestituicaoFerias;
     *
     * @return Um registro de restituição de férias no model.
     */

    public RegistroDeFeriasModel RetornaRegistroRestituicaoFerias (int pCodRestituicaoFerias) {

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        RegistroDeFeriasModel registro = null;

        try {

            String sql = "SELECT COD," +
                    " COD_TERCEIRIZADO_CONTRATO," +
                    " COD_TIPO_RESTITUICAO," +
                    " DATA_INICIO_PERIODO_AQUISITIVO," +
                    " DATA_FIM_PERIODO_AQUISITIVO," +
                    " DATA_INICIO_USUFRUTO," +
                    " DATA_FIM_USUFRUTO," +
                    " DIAS_VENDIDOS," +
                    " VALOR_FERIAS," +
                    " VALOR_TERCO_CONSTITUCIONAL," +
                    " INCID_SUBMOD_4_1_FERIAS," +
                    " INCID_SUBMOD_4_1_TERCO," +
                    " PARCELA," +
                    " DATA_REFERENCIA," +
                    " AUTORIZADO," +
                    " RESTITUIDO," +
                    " OBSERVACAO," +
                    " LOGIN_ATUALIZACAO," +
                    " DATA_ATUALIZACAO" +
                    " FROM TB_RESTITUICAO_FERIAS" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pCodRestituicaoFerias);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                registro = new RegistroDeFeriasModel(resultSet.getInt(1),
                        resultSet.getInt(2),
                        resultSet.getInt(3),
                        resultSet.getDate(4),
                        resultSet.getDate(5),
                        resultSet.getDate(6),
                        resultSet.getDate(7),
                        resultSet.getInt(8),
                        resultSet.getFloat(9),
                        resultSet.getFloat(10),
                        resultSet.getFloat(11),
                        resultSet.getFloat(12),
                        resultSet.getInt(13),
                        resultSet.getDate(14),
                        resultSet.getString(15),
                        resultSet.getString(16),
                        resultSet.getString(17),
                        resultSet.getString(18),
                        resultSet.getTimestamp(19));

            }

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível recuperar o registro de restituição de férias.");

        }

        return registro;

    }

    /**
     * Retorna um registro da tabela tb_restituicao_decimo_terceiro.
     *
     * @param pCodRestituicaoDecimoTerceiro;
     *
     * @return Um registro de restituição de décimo terceiro no model.
     */

    public RegistroDeDecimoTerceiroModel RetornaRegistroRestituicaoDecimoTerceiro (int pCodRestituicaoDecimoTerceiro) {

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        RegistroDeDecimoTerceiroModel registro = null;

        try {

            String sql = "SELECT COD," +
                    " COD_TERCEIRIZADO_CONTRATO," +
                    " COD_TIPO_RESTITUICAO," +
                    " PARCELA," +
                    " DATA_INICIO_CONTAGEM," +
                    " VALOR," +
                    " INCIDENCIA_SUBMODULO_4_1," +
                    " DATA_REFERENCIA," +
                    " AUTORIZADO," +
                    " RESTITUIDO," +
                    " OBSERVACAO," +
                    " LOGIN_ATUALIZACAO," +
                    " DATA_ATUALIZACAO" +
                    " FROM TB_RESTITUICAO_DECIMO_TERCEIRO" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pCodRestituicaoDecimoTerceiro);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                registro = new RegistroDeDecimoTerceiroModel(resultSet.getInt(1),
                        resultSet.getInt(2),
                        resultSet.getInt(3),
                        resultSet.getInt(4),
                        resultSet.getDate(5),
                        resultSet.getFloat(6),
                        resultSet.getFloat(7),
                        resultSet.getDate(8),
                        resultSet.getString(9),
                        resultSet.getString(10),
                        resultSet.getString(11),
                        resultSet.getString(12),
                        resultSet.getTimestamp(13));

            }

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível recuperar o registro de restituição de férias.");

        }

        return registro;

    }

    /**
     * Retorna um registro da tabela tb_restituicao_rescisao.
     *
     * @param pCodRestituicaoRescisao;
     *
     * @return Um registro de restituição de rescisão no model.
     */

    public RegistroRestituicaoRescisao RetornaRegistroRestituicaoRescisao (int pCodRestituicaoRescisao) {

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        RegistroRestituicaoRescisao registro = null;

        try {

            String sql = "SELECT COD," +
                    " COD_TERCEIRIZADO_CONTRATO," +
                    " COD_TIPO_RESTITUICAO," +
                    " COD_TIPO_RESCISAO," +
                    " DATA_DESLIGAMENTO," +
                    " DATA_INICIO_FERIAS," +
                    " VALOR_DECIMO_TERCEIRO," +
                    " INCID_SUBMOD_4_1_DEC_TERCEIRO," +
                    " INCID_MULTA_FGTS_DEC_TERCEIRO," +
                    " VALOR_FERIAS," +
                    " VALOR_TERCO," +
                    " INCID_SUBMOD_4_1_FERIAS," +
                    " INCID_SUBMOD_4_1_DEC_TERCEIRO," +
                    " INCID_MULTA_FGTS_FERIAS," +
                    " INCID_MULTA_FGTS_TERCO," +
                    " VALOR_FERIAS_PROP," +
                    " VALOR_TERCO_PROP," +
                    " INCID_SUBMOD_4_1_FERIAS_PROP," +
                    " INCID_SUBMOD_4_1_DEC_TERCEIRO_PROP," +
                    " INCID_MULTA_FGTS_FERIAS_PROP," +
                    " INCID_MULTA_FGTS_TERCO_PROP," +
                    " MULTA_FGTS_SALARIO," +
                    " DATA_REFERENCIA," +
                    " AUTORIZADO," +
                    " RESTITUIDO," +
                    " OBSERVACAO," +
                    " LOGIN_ATUALIZACAO," +
                    " DATA_ATUALIZACAO" +
                    " FROM TB_RESTITUICAO_RESCISAO" +
                    " WHERE COD = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pCodRestituicaoRescisao);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                registro = new RegistroRestituicaoRescisao(resultSet.getInt(1),
                        resultSet.getInt(2),
                        resultSet.getInt(3),
                        resultSet.getInt(4),
                        resultSet.getDate(5),
                        resultSet.getDate(6),
                        resultSet.getFloat(7),
                        resultSet.getFloat(8),
                        resultSet.getFloat(9),
                        resultSet.getFloat(10),
                        resultSet.getFloat(11),
                        resultSet.getFloat(12),
                        resultSet.getFloat(13),
                        resultSet.getFloat(14),
                        resultSet.getFloat(15),
                        resultSet.getFloat(16),
                        resultSet.getFloat(17),
                        resultSet.getFloat(18),
                        resultSet.getFloat(19),
                        resultSet.getFloat(20),
                        resultSet.getFloat(21),
                        resultSet.getFloat(22),
                        resultSet.getDate(23),
                        resultSet.getString(24),
                        resultSet.getString(25),
                        resultSet.getString(26),
                        resultSet.getString(27),
                        resultSet.getTimestamp(28));

            }

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível recuperar o registro de restituição de rescisão.");

        }

        return registro;

    }

    /**
     * Retorna um registro da tabela tb_rubrica.
     *
     * @param pCodRubrica;
     *
     * @return Um registro de rubrica no model.
     */

    public RegistroRubricaModel RetornaRegistroRubrica (int pCodRubrica) {

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        RegistroRubricaModel registro = null;

        try {

            String sql = "SELECT nome, " +
                    "sigla, " +
                    "descricao, " +
                    "login_atualizacao, " +
                    "data_atualizacao " +
                    "FROM tb_rubrica " +
                    "WHERE cod = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pCodRubrica);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                registro = new RegistroRubricaModel(pCodRubrica,
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getTimestamp(5));

            }

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível recuperar o registro da rubrica requisitada.");

        }

        return registro;

    }

    /**
     * Retorna um registro da tabela tb_hist_restituicao_rescisao.
     *
     * @param pCodHistRestituicaoRescisao;
     *
     * @return Um registro ddo histórico de restituições rejeitadas de rescisao no model.
     */

    public RegistroHistRestituicaoRescisao RetornaRegistroHistRestituicaoRescisao (int pCodHistRestituicaoRescisao) {

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        RegistroHistRestituicaoRescisao registro = null;

        try {

            String sql = "SELECT cod_restituicao_rescisao, " +
                    "cod_tipo_restituicao, " +
                    "COD_TIPO_RESCISAO, " +
                    "DATA_DESLIGAMENTO, " +
                    "DATA_INICIO_FERIAS, " +
                    "VALOR_DECIMO_TERCEIRO, " +
                    "INCID_SUBMOD_4_1_DEC_TERCEIRO, " +
                    "INCID_MULTA_FGTS_DEC_TERCEIRO, " +
                    "valor_ferias, " +
                    "valor_terco, " +
                    "incid_submod_4_1_ferias, " +
                    "incid_submod_4_1_terco, " +
                    "INCID_MULTA_FGTS_FERIAS, " +
                    "INCID_MULTA_FGTS_TERCO, " +
                    "valor_ferias_prop, " +
                    "valor_terco_prop, " +
                    "incid_submod_4_1_ferias_prop, " +
                    "incid_submod_4_1_terco_prop, " +
                    "incid_multa_fgts_ferias_prop, " +
                    "incid_multa_fgts_terco_prop, " +
                    "MULTA_FGTS_SALARIO, " +
                    "data_referencia, " +
                    "autorizado, " +
                    "restituido, " +
                    "observacao, " +
                    "login_atualizacao, " +
                    "data_atualizacao " +
                    "FROM tb_hist_restituicao_rescisao " +
                    "WHERE cod = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pCodHistRestituicaoRescisao);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                registro = new RegistroHistRestituicaoRescisao(pCodHistRestituicaoRescisao,
                        resultSet.getInt(1),
                        resultSet.getInt(2),
                        resultSet.getInt(3),
                        resultSet.getDate(4),
                        resultSet.getDate(5),
                        resultSet.getFloat(6),
                        resultSet.getFloat(7),
                        resultSet.getFloat(8),
                        resultSet.getFloat(9),
                        resultSet.getFloat(10),
                        resultSet.getFloat(11),
                        resultSet.getFloat(12),
                        resultSet.getFloat(13),
                        resultSet.getFloat(14),
                        resultSet.getFloat(15),
                        resultSet.getFloat(16),
                        resultSet.getFloat(17),
                        resultSet.getFloat(18),
                        resultSet.getFloat(19),
                        resultSet.getFloat(20),
                        resultSet.getFloat(21),
                        resultSet.getDate(22),
                        resultSet.getString(23),
                        resultSet.getString(24),
                        resultSet.getString(25),
                        resultSet.getString(26),
                        resultSet.getTimestamp(27));

            }

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível recuperar o registro de histórico de restituição de rescisão solicitado.");

        }

        return registro;

    }

    /**
     * Retorna um registro da tabela tb_saldo_residual_rescisao.
     *
     * @param pCodRestituicaoRescisao;
     *
     * @return Um registro de saldo residual de rescisão no model.
     */

    public RegistroSaldoResidualRescisao RetornaRegistroSaldoResidualRescisao  (int pCodRestituicaoRescisao) {

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        RegistroSaldoResidualRescisao registro = null;

        try {

            String sql = "SELECT cod, " +
                    "VALOR_DECIMO_TERCEIRO, " +
                    "INCID_SUBMOD_4_1_DEC_TERCEIRO, " +
                    "INCID_MULTA_FGTS_DEC_TERCEIRO, " +
                    "valor_ferias, " +
                    "valor_ferias, " +
                    "INCID_SUBMOD_4_1_FERIAS, " +
                    "INCID_SUBMOD_4_1_TERCO, " +
                    "INCID_MULTA_FGTS_FERIAS, " +
                    "INCID_MULTA_FGTS_TERCO, " +
                    "valor_ferias_prop, " +
                    "valor_terco_prop, " +
                    "incid_submod_4_1_ferias_prop, " +
                    "incid_submod_4_1_terco_prop, " +
                    "incid_multa_fgts_ferias_prop, " +
                    "incid_multa_fgts_terco_prop, " +
                    "MULTA_FGTS_SALARIO, " +
                    "AUTORIZADO, " +
                    "RESTITUIDO, " +
                    "login_atualizacao, " +
                    "data_atualizacao " +
                    "FROM tb_saldo_residual_rescisao " +
                    "WHERE COD_RESTITUICAO_RESCISAO = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pCodRestituicaoRescisao);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                registro = new RegistroSaldoResidualRescisao(resultSet.getInt(1),
                        pCodRestituicaoRescisao,
                        resultSet.getFloat(2),
                        resultSet.getFloat(3),
                        resultSet.getFloat(4),
                        resultSet.getFloat(5),
                        resultSet.getFloat(6),
                        resultSet.getFloat(7),
                        resultSet.getFloat(8),
                        resultSet.getFloat(9),
                        resultSet.getFloat(10),
                        resultSet.getFloat(11),
                        resultSet.getFloat(12),
                        resultSet.getFloat(13),
                        resultSet.getFloat(14),
                        resultSet.getFloat(15),
                        resultSet.getFloat(16),
                        resultSet.getFloat(17),
                        resultSet.getString(18),
                        resultSet.getString(19),
                        resultSet.getString(20),
                        resultSet.getTimestamp(21));

            }

        } catch (SQLException sqle) {

            sqle.printStackTrace();

            throw new NullPointerException("Não foi possível recuperar o registro de saldo residual de férias solicitado.");

        }

        return registro;

    }

}