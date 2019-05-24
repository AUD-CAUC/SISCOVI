package br.jus.stj.siscovi.calculos;

import br.jus.stj.siscovi.model.CodTerceirizadoECodFuncaoTerceirizadoModel;

import javax.validation.constraints.Null;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import br.jus.stj.siscovi.dao.sql.InsertTSQL;

public class TotalMensalAReter {

    private Connection connection;

    public TotalMensalAReter (Connection connection) {

        this.connection = connection;

    }

    /**
     * Método que calcula o total mensal a reter em um determinado mês para
     * um determinado contrato.
     * @param pCodContrato
     * @param pMes
     * @param pAno
     */

    public void CalculaTotalMensal (int pCodContrato, int pMes, int pAno, String pLoginAtualizacao) {

        //Checked.

        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Retencao retencao = new Retencao(connection);
        Percentual percentual = new Percentual(connection);
        Periodos periodo = new Periodos(connection);
        Remuneracao remuneracao = new Remuneracao(connection);
        InsertTSQL insert = new InsertTSQL(connection);

        /*Variáveis totalizadoras de valores.*/

        float vTotalFerias = 0;
        float vTotalTercoConstitucional = 0;
        float vTotalDecimoTerceiro = 0;
        float vTotalIncidencia = 0;
        float vTotalIndenizacao = 0;
        float vTotal = 0;

        /*Variáveis de valores parciais.*/

        float vValorFerias = 0;
        float vValorTercoConstitucional = 0;
        float vValorDecimoTerceiro = 0;
        float vValorIncidencia = 0;
        float vValorIndenizacao = 0 ;

        /*Variáveis de percentuais.*/

        float vPercentualFerias = 0;
        float vPercentualTercoConstitucional = 0;
        float vPercentualDecimoTerceiro = 0;
        float vPercentualIncidencia = 0;
        float vPercentualIndenizacao = 0;
        float vPercentualPenalidadeFGTS = 0;
        float vPercentualMultaFGTS = 0;

        /*Variável da remuneração da função do contrato.*/

        float vRemuneracao = 0;

        /*Variável para a verificação de existência da cálculos realizados.*/

        int vExisteCalculo = 0;

        /*Variáveis de datas.*/

        Date vDataReferencia = Date.valueOf(pAno + "-" + pMes + "-01");
        Date vDataInicio = null;
        Date vDataFim = null;
        Date vDataInicioContrato = null;
        Date vDataFimContrato = null;


        /*Variável de checagem da existência do contrato.*/

        int vCheck = 0;
        int vDiasSubperiodo = 0;

        /**Checagem da validade do contrato passado (existe).*/

        try {

            preparedStatement = connection.prepareStatement("SELECT COUNT(COD) FROM TB_CONTRATO WHERE COD = ?");
            preparedStatement.setInt(1, pCodContrato);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vCheck = resultSet.getInt(1);

            }

        } catch (SQLException e) {

            throw new NullPointerException("Erro ao checar a existência do contrato.");

        }

        if (vCheck == 0) {

            throw new NullPointerException("O contrato passado não existe ou não foi encontrado.");

        }

        /**Se a data passada for anterior ao contrato ou posterior ao seu termino aborta-se.*/

        try {

            preparedStatement = connection.prepareStatement("SELECT MIN(EC.DATA_INICIO_VIGENCIA), MAX(EC.DATA_FIM_VIGENCIA) FROM tb_evento_contratual EC WHERE EC.COD_CONTRATO = ?");
            preparedStatement.setInt(1, pCodContrato);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vDataInicioContrato = resultSet.getDate(1);
                vDataFimContrato = resultSet.getDate(2);

            }

        } catch (SQLException sqle) {

            throw new NullPointerException("A data deve estar dentro do período de validade do contrato.");

        }

        try {
            if (vDataReferencia.before(Date.valueOf(vDataInicioContrato.toLocalDate().minusMonths(1).withDayOfMonth(vDataInicioContrato.toLocalDate().lengthOfMonth()).plusDays(1)))) {

                throw new NullPointerException("O período que se tenta calcular está fora da vigência do contrato.");

            }
        } catch (java.time.DateTimeException dt) {
            /** Para casos que o mês anterior não tem 31 dias. */
            if (vDataReferencia.before(Date.valueOf(vDataInicioContrato.toLocalDate().minusMonths(1).withDayOfMonth(vDataInicioContrato.toLocalDate().lengthOfMonth()-1).plusDays(1)))) {

                throw new NullPointerException("O período que se tenta calcular está fora da vigência do contrato.");

            }
        }

        try {
            if (vDataReferencia.after(Date.valueOf(vDataFimContrato.toLocalDate().minusMonths(1).withDayOfMonth(vDataFimContrato.toLocalDate().lengthOfMonth()).plusDays(1)))) {

                throw new NullPointerException("A data passada deve ser anterior a data de validade do contrato.");

            }
        } catch (java.time.DateTimeException dt) {
            if (vDataReferencia.after(Date.valueOf(vDataFimContrato.toLocalDate().minusMonths(1).withDayOfMonth(vDataFimContrato.toLocalDate().lengthOfMonth()-1).plusDays(1)))) {

                throw new NullPointerException("A data passada deve ser anterior a data de validade do contrato.");

            }
        }


        /**Verificação da existência de cálculo para aquele mês e consequente deleção.*/

        try {

            preparedStatement = connection.prepareStatement("SELECT COUNT (TMR.COD) FROM TB_TOTAL_MENSAL_A_RETER TMR JOIN TB_TERCEIRIZADO_CONTRATO TC ON TC.COD = TMR.COD_TERCEIRIZADO_CONTRATO" +
                    " WHERE MONTH(TMR.DATA_REFERENCIA) = ? AND YEAR(TMR.DATA_REFERENCIA) = ? AND TC.COD_CONTRATO = ?");

            preparedStatement.setInt(1, pMes);
            preparedStatement.setInt(2, pAno);
            preparedStatement.setInt(3, pCodContrato);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vExisteCalculo = resultSet.getInt(1);

            }

            if (vExisteCalculo > 0) {

                /**Deleta as retroatividades associadas aquele mês/ano.*/

                preparedStatement = connection.prepareStatement("DELETE FROM TB_RETROATIVIDADE_TOTAL_MENSAL WHERE COD_TOTAL_MENSAL_A_RETER IN (SELECT TMR.COD" +
                        " FROM TB_TOTAL_MENSAL_A_RETER TMR JOIN TB_TERCEIRIZADO_CONTRATO TC ON TC.COD = TMR.COD_TERCEIRIZADO_CONTRATO" +
                        " WHERE MONTH(TMR.DATA_REFERENCIA) = ? AND YEAR(TMR.DATA_REFERENCIA) = ? AND TC.COD_CONTRATO = ?)");

                preparedStatement.setInt(1, pMes);
                preparedStatement.setInt(2, pAno);
                preparedStatement.setInt(3, pCodContrato);
                preparedStatement.executeUpdate();

                /**Deleta os recolhimentos realizados naquele mês/ano.*/

                preparedStatement = connection.prepareStatement("DELETE FROM TB_TOTAL_MENSAL_A_RETER WHERE MONTH(DATA_REFERENCIA) = ? AND YEAR(DATA_REFERENCIA) = ?" +
                        " AND COD_TERCEIRIZADO_CONTRATO IN (SELECT TC.COD FROM TB_TERCEIRIZADO_CONTRATO TC WHERE TC.COD_CONTRATO = ?)");

                preparedStatement.setInt(1, pMes);
                preparedStatement.setInt(2, pAno);
                preparedStatement.setInt(3, pCodContrato);
                preparedStatement.executeUpdate();

            }

        } catch (SQLException e) {

            throw new RuntimeException("Erro ao excluir registros de recolhimento para o mês a ser calculado.");

        }

        /**Caso não hajam mudaças de percentual no mês designado carregam-se os valores.*/

        if (!percentual.ExisteMudancaPercentual(pCodContrato, pMes, pAno, 1)) {

            /* Definição dos percentuais. */

            vPercentualFerias = percentual.RetornaPercentualContrato(pCodContrato, 1, pMes, pAno, 1,1);
            vPercentualTercoConstitucional = percentual.RetornaPercentualContrato(pCodContrato, 2, pMes, pAno, 1,1);
            vPercentualDecimoTerceiro = percentual.RetornaPercentualContrato(pCodContrato, 3, pMes, pAno, 1, 1);
            vPercentualIncidencia = (percentual.RetornaPercentualContrato(pCodContrato, 7, pMes, pAno, 1, 1) *
                    (vPercentualFerias + vPercentualDecimoTerceiro + vPercentualTercoConstitucional))/100;
            vPercentualIndenizacao = percentual.RetornaPercentualEstatico(pCodContrato, 4, pMes, pAno, 1, 1);
            vPercentualPenalidadeFGTS = percentual.RetornaPercentualEstatico(pCodContrato, 6, pMes, pAno, 1, 1);
            vPercentualMultaFGTS = percentual.RetornaPercentualEstatico(pCodContrato, 5, pMes, pAno, 1, 1);
            vPercentualIndenizacao = (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100)) *
                    (1 + (vPercentualFerias/100) + (vPercentualDecimoTerceiro/100) + (vPercentualTercoConstitucional/100))) * 100;

        }

        /**Busca funções do contrato.*/

        ArrayList<Integer> c1 = new ArrayList<>();

        try {

            preparedStatement = connection.prepareStatement("SELECT COD FROM TB_FUNCAO_CONTRATO WHERE COD_CONTRATO = ?");
            preparedStatement.setInt(1, pCodContrato);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                c1.add(resultSet.getInt("COD"));

            }

        } catch (SQLException sqle) {

            throw new NullPointerException("Erro ao tentar buscar as funções do contrato!");

        }

        /**Para cada função do contrato.*/

        Convencao convencao = new Convencao(connection);

        for (int i = 0; i < c1.size(); i++) {

            ArrayList<CodTerceirizadoECodFuncaoTerceirizadoModel> tuplas = selecionaTerceirizadosContratoFuncao(c1.get(i), vDataReferencia, pMes, pAno);

            /**Se não existe dupla convenção e duplo percentual.*/

            if (!convencao.ExisteDuplaConvencao(c1.get(i), pMes, pAno, 1) && !percentual.ExisteMudancaPercentual(pCodContrato, pMes, pAno, 1)) {

                vRemuneracao = remuneracao.RetornaRemuneracaoPeriodo(c1.get(i), pMes, pAno, 1, 1);

                if (vRemuneracao == 0) {

                    throw new NullPointerException("Erro na execução do procedimento: Remuneração não encontrada. CÓDICO: -20001");

                }

                /**Para cada funcionário que ocupa aquele função.*/

                for (int j= 0; j < tuplas.size(); j++) {

                    /*Redefine todas as variáveis.*/

                    vTotal = 0;
                    vTotalFerias = 0;
                    vTotalTercoConstitucional = 0;
                    vTotalDecimoTerceiro = 0;
                    vTotalIncidencia = 0;
                    vTotalIndenizacao = 0;

                    /*Calcula o valor integral para a função.*/

                    vTotalFerias = vRemuneracao * (vPercentualFerias/100);
                    vTotalTercoConstitucional = vRemuneracao * (vPercentualTercoConstitucional/100);
                    vTotalDecimoTerceiro = vRemuneracao * (vPercentualDecimoTerceiro/100);
                    vTotalIncidencia = vRemuneracao * (vPercentualIncidencia/100);
                    vTotalIndenizacao = vRemuneracao * (vPercentualIndenizacao/100);

                    /**No caso de mudança de função temos um recolhimento proporcional ao dias trabalhados no cargo, situação similar para a retenção proporcional.*/

                    if (retencao.ExisteMudancaFuncao(tuplas.get(j).getCodTerceirizadoContrato(), pMes, pAno) || !retencao.FuncaoRetencaoIntegral(tuplas.get(j).getCod(), pMes, pAno)) {

                        vTotalFerias = (vTotalFerias/30) * periodo.DiasTrabalhadosMes(tuplas.get(j).getCod(), pMes, pAno);
                        vTotalTercoConstitucional = (vTotalTercoConstitucional/30) * periodo.DiasTrabalhadosMes(tuplas.get(j).getCod(), pMes, pAno);
                        vTotalDecimoTerceiro = (vTotalDecimoTerceiro/30) * periodo.DiasTrabalhadosMes(tuplas.get(j).getCod(), pMes, pAno);
                        vTotalIncidencia = (vTotalIncidencia/30) * periodo.DiasTrabalhadosMes(tuplas.get(j).getCod(), pMes, pAno);
                        vTotalIndenizacao = (vTotalIndenizacao/30) * periodo.DiasTrabalhadosMes(tuplas.get(j).getCod(), pMes, pAno);

                    }

                    vTotal = (vTotalFerias + vTotalTercoConstitucional + vTotalDecimoTerceiro + vTotalIncidencia + vTotalIndenizacao);

                    try {

                        preparedStatement = connection.prepareStatement("INSERT INTO TB_TOTAL_MENSAL_A_RETER (COD_TERCEIRIZADO_CONTRATO, COD_FUNCAO_TERCEIRIZADO, FERIAS, TERCO_CONSTITUCIONAL," +
                                " DECIMO_TERCEIRO, INCIDENCIA_SUBMODULO_4_1, MULTA_FGTS, TOTAL, DATA_REFERENCIA, LOGIN_ATUALIZACAO, DATA_ATUALIZACAO) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'SYSTEM', CURRENT_TIMESTAMP)");

                        preparedStatement.setInt(1, tuplas.get(j).getCodTerceirizadoContrato());
                        preparedStatement.setInt(2, tuplas.get(j).getCod());
                        preparedStatement.setFloat(3, vTotalFerias);
                        preparedStatement.setFloat(4, vTotalTercoConstitucional);
                        preparedStatement.setFloat(5, vTotalDecimoTerceiro);
                        preparedStatement.setFloat(6, vTotalIncidencia);
                        preparedStatement.setFloat(7, vTotalIndenizacao);
                        preparedStatement.setFloat(8, vTotal);
                        preparedStatement.setDate(9, vDataReferencia);
                        preparedStatement.executeUpdate();

                    } catch (SQLException e) {

                        e.printStackTrace();

                        throw new RuntimeException("Erro ao tentar inserir os resultados do cálculo de Total Mensal a Reter no banco de dados!");

                    }

                }
            }

            /**Se não existe dupla convenção e existe duplo percentual.*/

            if (!convencao.ExisteDuplaConvencao(c1.get(i), pMes, pAno, 1) && percentual.ExisteMudancaPercentual(pCodContrato, pMes, pAno, 1)) {

                /**Define a remuneração do função.*/

                vRemuneracao = remuneracao.RetornaRemuneracaoPeriodo(c1.get(i), pMes, pAno, 1, 1);

                if (vRemuneracao == 0) {

                    throw new NullPointerException("Erro na execução do procedimento: Remuneração não encontrada. Código -20001");
                }

                /**Para cada funcionário que ocupa aquele função.*/

                for (int j = 0; j < tuplas.size(); j++) {

                    /*Redefine todas as variáveis.*/

                    vTotal = 0;
                    vTotalFerias = 0;
                    vTotalTercoConstitucional = 0;
                    vTotalDecimoTerceiro = 0;
                    vTotalIncidencia = 0;
                    vTotalIndenizacao = 0;

                    vValorFerias = 0;
                    vValorTercoConstitucional = 0;
                    vValorDecimoTerceiro = 0;
                    vValorIncidencia = 0;
                    vValorIndenizacao = 0;

                    vDataInicio = vDataReferencia;

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
                                " AND" +
                                " YEAR(DATA_FIM) = ?)" +
                                " UNION" +
                                " SELECT data_inicio AS data" +
                                " FROM tb_percentual_estatico" +
                                " WHERE (MONTH(DATA_INICIO)=?" +
                                " AND " +
                                " YEAR(DATA_INICIO)=?)" +
                                " UNION" +
                                " SELECT data_fim AS data" +
                                " FROM tb_percentual_estatico" +
                                " WHERE (MONTH(DATA_FIM)=?" +
                                " AND" +
                                " YEAR(DATA_FIM)=?)" +
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

                            datas.add(resultSet.getDate("DATA"));

                        }

                    } catch (SQLException e) {

                        throw new NullPointerException("Erro ao tentar carregar as datas de inicio e fim do contrato. Para a função: " + c1.get(i) + ". Terceirizado: " + tuplas.get(j).getCodTerceirizadoContrato()
                        + "Contrato: " + pCodContrato + ". No perídodo: " + vDataReferencia.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                    }

                    for (Date data: datas) {

                        /**Definição das datas de início e fim do subperíodo.*/

                        vDataFim = data;

                        /**Definição dos dias contidos no subperíodo*/

                        vDiasSubperiodo = (int)((ChronoUnit.DAYS.between(vDataInicio.toLocalDate(), vDataFim.toLocalDate())) + 1);

                        if (pMes == 2) {

                            if (vDataFim.toLocalDate().getDayOfMonth() == Date.valueOf(vDataReferencia.toLocalDate().withDayOfMonth(vDataReferencia.toLocalDate().lengthOfMonth())).toLocalDate().getDayOfMonth()) {

                                if (vDataFim.toLocalDate().getDayOfMonth() == 28) {

                                    vDiasSubperiodo = vDiasSubperiodo + 2;

                                } else {

                                    vDiasSubperiodo = vDiasSubperiodo + 1;

                                }

                            }

                        }

                        /* Definição dos percentuais do subperíodo. */

                        vPercentualFerias = percentual.RetornaPercentualContrato(pCodContrato, 1, vDataInicio, vDataFim, 1);
                        vPercentualTercoConstitucional = vPercentualFerias / 3;
                        vPercentualDecimoTerceiro = percentual.RetornaPercentualContrato(pCodContrato, 3, vDataInicio, vDataFim, 1);
                        vPercentualIncidencia = (percentual.RetornaPercentualContrato(pCodContrato, 7, vDataInicio, vDataFim, 1) *
                                (vPercentualFerias + vPercentualDecimoTerceiro + vPercentualTercoConstitucional)) / 100;
                        vPercentualIndenizacao = percentual.RetornaPercentualEstatico(pCodContrato, 4, vDataInicio, vDataFim, 1);
                        vPercentualPenalidadeFGTS = percentual.RetornaPercentualEstatico(pCodContrato, 6, vDataInicio, vDataFim, 1);
                        vPercentualMultaFGTS = percentual.RetornaPercentualEstatico(pCodContrato, 5, vDataInicio, vDataFim, 1);
                        vPercentualIndenizacao = (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100)) *
                                (1 + (vPercentualFerias/100) + (vPercentualDecimoTerceiro/100) + (vPercentualTercoConstitucional/100))) * 100;

                        /* Calculo da porção correspondente ao subperíodo.*/

                        vValorFerias = ((vRemuneracao * (vPercentualFerias/100))/30) * vDiasSubperiodo;
                        vValorTercoConstitucional = ((vRemuneracao * (vPercentualTercoConstitucional/100))/30) * vDiasSubperiodo;
                        vValorDecimoTerceiro = ((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * vDiasSubperiodo;
                        vValorIncidencia = ((vRemuneracao * (vPercentualIncidencia/100))/30) * vDiasSubperiodo;
                        vValorIndenizacao = ((vRemuneracao * (vPercentualIndenizacao/100))/30) * vDiasSubperiodo;

                        /* No caso de mudança de função ou retenção parcial temos um recolhimento proporcional ao dias trabalhados no cargo. */

                        if (retencao.ExisteMudancaFuncao(tuplas.get(j).getCodTerceirizadoContrato(), pMes, pAno) || !retencao.FuncaoRetencaoIntegral(tuplas.get(j).getCod(), pMes, pAno)) {

                            vValorFerias = (vValorFerias/vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tuplas.get(j).getCod(), vDataInicio, vDataFim);
                            vValorTercoConstitucional = (vValorTercoConstitucional/vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tuplas.get(j).getCod(), vDataInicio, vDataFim);
                            vValorDecimoTerceiro = (vValorDecimoTerceiro/vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tuplas.get(j).getCod(), vDataInicio, vDataFim);;
                            vValorIncidencia = (vValorIncidencia/vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tuplas.get(j).getCod(), vDataInicio, vDataFim);
                            vValorIndenizacao = (vValorIndenizacao/vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tuplas.get(j).getCod(), vDataInicio, vDataFim);

                        }

                        vTotalFerias = vTotalFerias + vValorFerias;
                        vTotalTercoConstitucional = vTotalTercoConstitucional + vValorTercoConstitucional;
                        vTotalDecimoTerceiro = vTotalDecimoTerceiro + vValorDecimoTerceiro;
                        vTotalIncidencia = vTotalIncidencia + vValorIncidencia;
                        vTotalIndenizacao = vTotalIndenizacao + vValorIndenizacao;

                        vDataInicio = Date.valueOf(vDataFim.toLocalDate().plusDays(1));

                    }

                    vTotal = (vTotalFerias + vTotalTercoConstitucional + vTotalDecimoTerceiro + vTotalIncidencia + vTotalIndenizacao);

                    insert.InsertTotalMensalAReter(tuplas.get(j).getCodTerceirizadoContrato(), tuplas.get(j).getCod(), vTotalFerias, vTotalTercoConstitucional, vTotalDecimoTerceiro, vTotalIncidencia,
                            vTotalIndenizacao, vTotal, vDataReferencia, pLoginAtualizacao);

                }

            }

            /**Se existe dupla convenção e não existe duplo percentual.*/

            if(convencao.ExisteDuplaConvencao(c1.get(i), pMes, pAno, 1) && !percentual.ExisteMudancaPercentual(pCodContrato, pMes, pAno, 1)) {

                /* Para cada funcionário que ocupa aquele funcao. */

                for(int j = 0; j < tuplas.size(); j++){

                    /* Redefine todas as variáveis. */

                    vTotal = 0;
                    vTotalFerias = 0;
                    vTotalTercoConstitucional = 0;
                    vTotalDecimoTerceiro = 0;
                    vTotalIncidencia = 0;
                    vTotalIndenizacao = 0;

                    vValorFerias = 0;
                    vValorTercoConstitucional = 0;
                    vValorDecimoTerceiro = 0;
                    vValorIncidencia = 0;
                    vValorIndenizacao = 0;

                    vDataInicio = vDataReferencia;
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
                        preparedStatement.setInt(2, c1.get(i));
                        preparedStatement.setInt(3, pMes);
                        preparedStatement.setInt(4, pAno);
                        preparedStatement.setInt(5, pCodContrato);
                        preparedStatement.setInt(6, c1.get(i));
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
                        preparedStatement.setInt(17, pAno);;
                        resultSet = preparedStatement.executeQuery();

                        while(resultSet.next()){
                            datas.add(resultSet.getDate("data"));
                        }
                    } catch (SQLException e) {
                        throw new NullPointerException("");
                    }
                    for(Date data : datas){

                        /* Definição da data fim do subperíodo */

                        vDataFim = data;

                        /**Definição dos dias contidos no subperíodo*/

                        vDiasSubperiodo = (int)((ChronoUnit.DAYS.between(vDataInicio.toLocalDate(), vDataFim.toLocalDate())) + 1);

                        if (pMes == 2) {

                            if (vDataFim.toLocalDate().getDayOfMonth() == Date.valueOf(vDataReferencia.toLocalDate().withDayOfMonth(vDataReferencia.toLocalDate().lengthOfMonth())).toLocalDate().getDayOfMonth()) {

                                if (vDataFim.toLocalDate().getDayOfMonth() == 28) {

                                    vDiasSubperiodo = vDiasSubperiodo + 2;

                                } else {

                                    vDiasSubperiodo = vDiasSubperiodo + 1;

                                }

                            }

                        }

                        vRemuneracao = remuneracao.RetornaRemuneracaoPeriodo(c1.get(i), vDataInicio, vDataFim, 1);

                        if(vRemuneracao == 0) {
                            throw new NullPointerException("Erro na execução do procedimento: Remuneração não encontrada. Erro -20001");
                        }

                        /**Cálculo da porção correspondente ao subperíodo.*/

                        vValorFerias = ((vRemuneracao * (vPercentualFerias/100))/30) * vDiasSubperiodo;
                        vValorTercoConstitucional = ((vRemuneracao * (vPercentualTercoConstitucional/100))/30) * vDiasSubperiodo;
                        vValorDecimoTerceiro = ((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * vDiasSubperiodo;
                        vValorIncidencia = ((vRemuneracao * (vPercentualIncidencia/100))/30) * vDiasSubperiodo;
                        vValorIndenizacao = ((vRemuneracao * (vPercentualIndenizacao/100))/30) * vDiasSubperiodo;

                        /* No caso de mudança de função ou retenção parcial temos um recolhimento proporcional ao dias trabalhados no cargo. */

                        if (retencao.ExisteMudancaFuncao(tuplas.get(j).getCodTerceirizadoContrato(), pMes, pAno) || !retencao.FuncaoRetencaoIntegral(tuplas.get(j).getCod(), pMes, pAno)) {

                            vValorFerias = (vValorFerias/vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tuplas.get(j).getCod(), vDataInicio, vDataFim);
                            vValorTercoConstitucional = (vValorTercoConstitucional/vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tuplas.get(j).getCod(), vDataInicio, vDataFim);
                            vValorDecimoTerceiro = (vValorDecimoTerceiro/vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tuplas.get(j).getCod(), vDataInicio, vDataFim);;
                            vValorIncidencia = (vValorIncidencia/vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tuplas.get(j).getCod(), vDataInicio, vDataFim);
                            vValorIndenizacao = (vValorIndenizacao/vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tuplas.get(j).getCod(), vDataInicio, vDataFim);

                        }

                        vTotalFerias = vTotalFerias + vValorFerias;
                        vTotalTercoConstitucional = vTotalTercoConstitucional + vValorTercoConstitucional;
                        vTotalDecimoTerceiro = vTotalDecimoTerceiro + vValorDecimoTerceiro;
                        vTotalIncidencia = vTotalIncidencia + vValorIncidencia;
                        vTotalIndenizacao = vTotalIndenizacao + vValorIndenizacao;

                        vDataInicio = Date.valueOf(vDataFim.toLocalDate().plusDays(1));
                    }
                    vTotal = (vTotalFerias + vTotalTercoConstitucional + vTotalDecimoTerceiro + vTotalIncidencia + vTotalIndenizacao);
                    insert.InsertTotalMensalAReter(tuplas.get(j).getCodTerceirizadoContrato(), tuplas.get(j).getCod(), vTotalFerias, vTotalTercoConstitucional, vTotalDecimoTerceiro, vTotalIncidencia, vTotalIndenizacao,
                            vTotal, vDataReferencia, pLoginAtualizacao);
                }
            }
            /* Se existe mudança de percentual e mudança de convenção. */
            if(convencao.ExisteDuplaConvencao(c1.get(i), pMes, pAno, 1) && percentual.ExisteMudancaPercentual(pCodContrato, pMes, pAno, 1)){
                /* Para cada funcionário que ocupa aquele funcao. */
                for(int j = 0; j < tuplas.size(); j++) {
                    /* Redefine todas as variáveis */

                    vTotal = 0;
                    vTotalFerias = 0;
                    vTotalTercoConstitucional = 0;
                    vTotalDecimoTerceiro = 0;
                    vTotalIncidencia = 0;
                    vTotalIndenizacao = 0;

                    vValorFerias = 0;
                    vValorTercoConstitucional = 0;
                    vValorDecimoTerceiro = 0;
                    vValorIncidencia = 0;
                    vValorIndenizacao = 0;
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
                                " AND" +
                                " YEAR(DATA_FIM) = ?)" +
                                " UNION" +
                                " SELECT data_inicio AS data" +
                                " FROM tb_percentual_estatico" +
                                " WHERE (MONTH(DATA_INICIO)=?" +
                                " AND " +
                                " YEAR(DATA_INICIO)=?)" +
                                " UNION" +
                                " SELECT data_fim AS data" +
                                " FROM tb_percentual_estatico" +
                                " WHERE (MONTH(DATA_FIM)=?" +
                                " AND" +
                                " YEAR(DATA_FIM)=?)" +
                                " UNION" +
                                " SELECT rfc.data_inicio AS data" +
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
                        preparedStatement.setInt(12, c1.get(i));
                        preparedStatement.setInt(13, pMes);
                        preparedStatement.setInt(14, pAno);
                        preparedStatement.setInt(15, pCodContrato);
                        preparedStatement.setInt(16, c1.get(i));
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

                        while(resultSet.next()) {
                            datas.add(resultSet.getDate("DATA"));
                        }

                    } catch (SQLException e) {
                        throw new NullPointerException("Erro ao carergar as datas de mudança de percentual e mudança de convenção para o funcionário : " + tuplas.get(j).getCodTerceirizadoContrato() +
                                ". na Função: " + c1.get(i));
                    }
                    for(Date data : datas) {
                        /* Definição da data fim do subperíodo. */
                        vDataFim = data;

                        /**Definição dos dias contidos no subperíodo*/

                        vDiasSubperiodo = (int)((ChronoUnit.DAYS.between(vDataInicio.toLocalDate(), vDataFim.toLocalDate())) + 1);

                        if (pMes == 2) {

                            if (vDataFim.toLocalDate().getDayOfMonth() == Date.valueOf(vDataReferencia.toLocalDate().withDayOfMonth(vDataReferencia.toLocalDate().lengthOfMonth())).toLocalDate().getDayOfMonth()) {

                                if (vDataFim.toLocalDate().getDayOfMonth() == 28) {

                                    vDiasSubperiodo = vDiasSubperiodo + 2;

                                } else {

                                    vDiasSubperiodo = vDiasSubperiodo + 1;

                                }

                            }

                        }

                        vRemuneracao = remuneracao.RetornaRemuneracaoPeriodo(c1.get(i), vDataInicio, vDataFim, 1);
                        if(vRemuneracao == 0) {
                            throw new NullPointerException("Erro na execução do procedimento: Remuneração não encontrada. Erro -20001");
                        }

                        /* Definição dos percentuais no subperíodo */
                        vPercentualFerias = percentual.RetornaPercentualContrato(pCodContrato, 1, vDataInicio, vDataFim, 1);
                        vPercentualTercoConstitucional = vPercentualFerias / 3;
                        vPercentualDecimoTerceiro = percentual.RetornaPercentualContrato(pCodContrato, 3, vDataInicio, vDataFim, 1);
                        vPercentualIncidencia = (percentual.RetornaPercentualContrato(pCodContrato, 7, vDataInicio, vDataFim, 1) *
                                (vPercentualFerias + vPercentualDecimoTerceiro + vPercentualTercoConstitucional))/100;

                        vPercentualIndenizacao = percentual.RetornaPercentualEstatico(pCodContrato, 4, vDataInicio, vDataFim, 1);
                        vPercentualPenalidadeFGTS = percentual.RetornaPercentualEstatico(pCodContrato, 6, vDataInicio, vDataFim, 1);
                        vPercentualMultaFGTS = percentual.RetornaPercentualEstatico(pCodContrato, 5, vDataInicio, vDataFim, 1);

                        vPercentualIndenizacao = (((vPercentualIndenizacao/100) *  (vPercentualPenalidadeFGTS/100) * (vPercentualMultaFGTS/100)) *
                                (1 + (vPercentualFerias/100) + (vPercentualDecimoTerceiro/100) + (vPercentualTercoConstitucional/100))) * 100;

                        /**Calculo da porção correspondente ao subperíodo.*/

                        vValorFerias = ((vRemuneracao * (vPercentualFerias/100))/30) * vDiasSubperiodo;
                        vValorTercoConstitucional = ((vRemuneracao * (vPercentualTercoConstitucional/100))/30) * vDiasSubperiodo;
                        vValorDecimoTerceiro = ((vRemuneracao * (vPercentualDecimoTerceiro/100))/30) * vDiasSubperiodo;
                        vValorIncidencia = ((vRemuneracao * (vPercentualIncidencia/100))/30) * vDiasSubperiodo;
                        vValorIndenizacao = ((vRemuneracao * (vPercentualIndenizacao/100))/30) * vDiasSubperiodo;

                        /* No caso de mudança de função ou retenção parcial temos um recolhimento proporcional ao dias trabalhados no cargo. */

                        if(!retencao.FuncaoRetencaoIntegral(tuplas.get(j).getCod(), pMes, pAno) || retencao.ExisteMudancaFuncao(tuplas.get(j).getCodTerceirizadoContrato(), pMes, pAno)) {

                            vValorFerias = (vValorFerias/vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tuplas.get(j).getCod(), vDataInicio, vDataFim);
                            vValorTercoConstitucional = (vValorTercoConstitucional/vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tuplas.get(j).getCod(), vDataInicio, vDataFim);
                            vValorDecimoTerceiro = (vValorDecimoTerceiro/vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tuplas.get(j).getCod(), vDataInicio, vDataFim);;
                            vValorIncidencia = (vValorIncidencia/vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tuplas.get(j).getCod(), vDataInicio, vDataFim);
                            vValorIndenizacao = (vValorIndenizacao/vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tuplas.get(j).getCod(), vDataInicio, vDataFim);

                        }

                        vTotalFerias = vTotalFerias + vValorFerias;
                        vTotalTercoConstitucional = vTotalTercoConstitucional + vValorTercoConstitucional;
                        vTotalDecimoTerceiro = vTotalDecimoTerceiro + vValorDecimoTerceiro;
                        vTotalIncidencia = vTotalIncidencia + vValorIncidencia;
                        vTotalIndenizacao = vTotalIndenizacao + vValorIndenizacao;

                        vDataInicio = Date.valueOf(vDataFim.toLocalDate().plusDays(1));

                    }

                    vTotal = (vTotalFerias + vTotalTercoConstitucional + vTotalDecimoTerceiro + vTotalIncidencia + vTotalIndenizacao);

                    insert.InsertTotalMensalAReter(tuplas.get(j).getCodTerceirizadoContrato(), tuplas.get(j).getCod(), vTotalFerias, vTotalTercoConstitucional, vTotalDecimoTerceiro, vTotalIncidencia, vTotalIndenizacao,
                            vTotal, vDataReferencia, pLoginAtualizacao);

                }

            }

        }

    }

    /**
     *
     * @param vDataFimMes
     * @return
     */
    Date adaptaDataPara360(Date vDataFimMes) {
        if(vDataFimMes.toLocalDate().getDayOfMonth() == 31) {
            vDataFimMes = Date.valueOf(vDataFimMes.toLocalDate().minusDays(1));
        }
        if(vDataFimMes.toLocalDate().getDayOfMonth() == 28){
            vDataFimMes = Date.valueOf(vDataFimMes.toLocalDate().plusDays(2));
        }
        if(vDataFimMes.toLocalDate().getDayOfMonth() == 29) {
            vDataFimMes = Date.valueOf(vDataFimMes.toLocalDate().plusDays(1));
        }
        return vDataFimMes;
    }

    ArrayList<CodTerceirizadoECodFuncaoTerceirizadoModel> selecionaTerceirizadosContratoFuncao (int pCodFuncaoContrato, Date pDataReferencia, int pMes, int pAno) {

        // Busca funcionários do contrato na respectiva função c1[i]

        ArrayList<CodTerceirizadoECodFuncaoTerceirizadoModel> tuplas = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT ft.cod_terceirizado_contrato, ft.cod FROM tb_funcao_terceirizado ft WHERE ft.cod_funcao_contrato = ?" +
                " AND ((ft.data_inicio <= ?) OR (MONTH(ft.data_inicio) = ?) AND YEAR(ft.data_inicio) = ?) AND ((ft.data_fim IS NULL) OR (ft.data_fim >= EOMONTH(?))" +
                " OR (MONTH(ft.data_fim) = ?) AND YEAR(ft.data_fim) = ?)")) {

            preparedStatement.setInt(1, pCodFuncaoContrato);
            preparedStatement.setDate(2, pDataReferencia);
            preparedStatement.setInt(3, pMes);
            preparedStatement.setInt(4, pAno);
            preparedStatement.setDate(5, pDataReferencia);
            preparedStatement.setInt(6, pMes);
            preparedStatement.setInt(7, pAno);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {

                    CodTerceirizadoECodFuncaoTerceirizadoModel tupla = new CodTerceirizadoECodFuncaoTerceirizadoModel(resultSet.getInt("COD"), resultSet.getInt("COD_TERCEIRIZADO_CONTRATO"));

                    tuplas.add(tupla);

                }

            }

        } catch(SQLException slqe) {

            throw new NullPointerException("Não foram encontrardos funcionários para a função: " + pCodFuncaoContrato);

        }

        return tuplas;

    }

    int contaDias(Date dataInicio, Date dataFim){
        return (int) ChronoUnit.DAYS.between(dataInicio.toLocalDate(), dataFim.toLocalDate()) + 1;
    }
    float calculaTotal(float valor, Date dataInicio, Date dataFim, int cod) {
        Periodos periodo = new Periodos(connection);
        return (valor/(contaDias(dataInicio, dataFim))) * periodo.DiasTrabalhadosPeriodo(cod, dataInicio, dataFim);
    }
}
