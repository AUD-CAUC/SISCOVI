package br.jus.stj.siscovi.calculos;

import br.jus.stj.siscovi.model.CodFuncaoContratoECodFuncaoTerceirizadoModel;
import br.jus.stj.siscovi.model.RegistroRestituicaoDecimoTerceiro;
import br.jus.stj.siscovi.model.ValorRestituicaoDecimoTerceiroModel;
import br.jus.stj.siscovi.dao.sql.*;

import java.sql.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class RestituicaoDecimoTerceiro {

    private Connection connection;

    public RestituicaoDecimoTerceiro (Connection connection) {

        this.connection = connection;

    }

    /**
     * Método que calcula o total de 13° a ser restituído para um
     * determinado período aquisitivo.
     *
     * @param pCodTerceirizadoContrato;
     * @param pNumeroParcela;
     * @param pInicioContagem;
     * @param pFimContagem;
     */

    public ValorRestituicaoDecimoTerceiroModel CalculaRestituicaoDecimoTerceiro (int pCodTerceirizadoContrato,
                                                                                 int pNumeroParcela,
                                                                                 Date pInicioContagem,
                                                                                 Date pFimContagem) {

        Retencao retencao = new Retencao(connection);
        Percentual percentual = new Percentual(connection);
        Periodos periodo = new Periodos(connection);
        Remuneracao remuneracao = new Remuneracao(connection);
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        /*Chaves primárias.*/

        int vCodContrato;

        /*Variáveis totalizadoras de valores.*/

        float vTotalDecimoTerceiro = 0;
        float vTotalIncidencia = 0;

        /*Variáveis de valores parciais.*/

        float vValorDecimoTerceiro;
        float vValorIncidencia;

        /*Variáveis de percentuais.*/

        float vPercentualDecimoTerceiro;
        float vPercentualIncidencia;

        /*Variável de remuneração da função.*/

        float vRemuneracao;

        /*Variáveis de data.*/

        Date vDataReferencia;
        Date vDataInicio;
        Date vDataFim;
        int vAno;
        int vMes;

        /*Variáveis de controle.*/

        int vDiasSubperiodo;

        /*Checagem dos parâmetros passados.*/

        if (pInicioContagem == null || pFimContagem == null) {

            throw new NullPointerException("Erro na checagem dos parâmetros.");

        }

        /*Carrega o código do contrato.*/

        vCodContrato = consulta.RetornaContratoTerceirizado(pCodTerceirizadoContrato);

        /*Define o valor das variáveis vMes e Vano de acordo com a adata de inínio do período aquisitivo.*/

        vMes = pInicioContagem.toLocalDate().getMonthValue();
        vAno = pInicioContagem.toLocalDate().getYear();

        /*Definição da data referência (sempre o primeiro dia do mês).*/

        vDataReferencia = Date.valueOf(vAno + "-" + vMes + "-" + "01");

        /*Início da contabilização de férias do período.*/

        do {

            /*Seleciona as funções que o terceirizado ocupou no mês avaliado.*/

            ArrayList<CodFuncaoContratoECodFuncaoTerceirizadoModel> tuplas = consulta.SelecionaFuncaoContratoEFuncaoTerceirizado(pCodTerceirizadoContrato, vDataReferencia);

            Convencao convencao = new Convencao(connection);

            /*Para cada função que o terceirizado ocupou no mês avaliado.*/

            for (CodFuncaoContratoECodFuncaoTerceirizadoModel tupla : tuplas) {

                /*Caso não exista mais de uma remuneração vigente no mês e não tenha havido alteração nos percentuais do contrato ou nos percentuais estáticos.*/

                if (!convencao.ExisteDuplaConvencao(tupla.getCodFuncaoContrato(), vMes, vAno, 2) && !percentual.ExisteMudancaPercentual(vCodContrato, vMes, vAno, 2)) {

                    /*Define o valor da remuneração da função e dos percentuais do contrato.*/

                    vRemuneracao = remuneracao.RetornaRemuneracaoPeriodo(tupla.getCodFuncaoContrato(), vMes, vAno, 1, 2);
                    vPercentualDecimoTerceiro = percentual.RetornaPercentualContrato(vCodContrato, 3, vMes, vAno, 1, 2);
                    vPercentualIncidencia = percentual.RetornaPercentualContrato(vCodContrato, 7, vMes, vAno, 1, 2);

                    if (vRemuneracao == 0) {

                        throw new NullPointerException("Erro na execução do procedimento: Remuneração não encontrada. Código -20001");

                    }

                    /*Cálculo do valor integral correspondente ao mês avaliado.*/

                    vValorDecimoTerceiro = (vRemuneracao * (vPercentualDecimoTerceiro / 100));
                    vValorIncidencia = (vValorDecimoTerceiro * (vPercentualIncidencia / 100));

                    /*o caso de mudança de função temos um recolhimento proporcional ao dias trabalhados no cargo,
                     situação similar para a retenção proporcional por menos de 14 dias trabalhados.*/

                    if (retencao.ExisteMudancaFuncao(pCodTerceirizadoContrato, vMes, vAno) || !retencao.FuncaoRetencaoIntegral(tupla.getCod(), vMes, vAno)) {

                        vValorDecimoTerceiro = (vValorDecimoTerceiro / 30) * periodo.DiasTrabalhadosMes(tupla.getCod(), vMes, vAno);
                        vValorIncidencia = (vValorIncidencia / 30) * periodo.DiasTrabalhadosMes(tupla.getCod(), vMes, vAno);

                    }

                    /*Contabilização do valor calculado.*/

                    vTotalDecimoTerceiro = vTotalDecimoTerceiro + vValorDecimoTerceiro;
                    vTotalIncidencia = vTotalIncidencia + vValorIncidencia;

                }

                /*Se existe apenas alteração de percentual no mês.*/

                if (!convencao.ExisteDuplaConvencao(tupla.getCodFuncaoContrato(), vMes, vAno, 2) && percentual.ExisteMudancaPercentual(vCodContrato, vMes, vAno, 2)) {

                    /*Define a remuneração do cargo, que não se altera no período.*/

                    vRemuneracao = remuneracao.RetornaRemuneracaoPeriodo(tupla.getCodFuncaoContrato(), vMes, vAno, 1, 2);

                    if (vRemuneracao == 0) {

                        throw new NullPointerException("Erro na execução do procedimento: Remuneração não encontrada. Código -20001");

                    }

                    /*Definição da data de início como sendo a data referência (primeiro dia do mês).*/

                    vDataInicio = vDataReferencia;

                    /*Seleciona as datas que compõem os subperíodos gerados pelas alterações de percentual no mês.*/

                    List<Date> datas = consulta.RetornaSubperiodosMesPercentual(vCodContrato,
                            vMes,
                            vAno,
                            vDataReferencia);

                    /*Loop contendo das datas das alterações de percentuais que comporão os subperíodos.*/

                    for (Date data : datas) {

                        /*Definição da data fim do subperíodo.*/

                        vDataFim = data;

                        /*Definição dos dias contidos no subperíodo*/

                        vDiasSubperiodo = (int) ((ChronoUnit.DAYS.between(vDataInicio.toLocalDate(), vDataFim.toLocalDate())) + 1);

                        if (vMes == 2) {

                            vDiasSubperiodo = periodo.AjusteDiasSubperiodoFevereiro(vDataReferencia, vDataFim, vDiasSubperiodo);

                        }

                        /*Definição dos percentuais do subperíodo.*/

                        vPercentualDecimoTerceiro = percentual.RetornaPercentualContrato(vCodContrato, 3, vDataInicio, vDataFim, 2);
                        vPercentualIncidencia = percentual.RetornaPercentualContrato(vCodContrato, 7, vDataInicio, vDataFim, 2);

                        /*Calculo da porção correspondente ao subperíodo.*/

                        vValorDecimoTerceiro = ((vRemuneracao * (vPercentualDecimoTerceiro / 100)) / 30) * vDiasSubperiodo;
                        vValorIncidencia = (vValorDecimoTerceiro * (vPercentualIncidencia / 100));

                        /*No caso de mudança de função temos um recolhimento proporcional ao dias trabalhados no cargo,
                         situação similar para a retenção proporcional por menos de 30 dias trabalhados.*/

                        if (retencao.ExisteMudancaFuncao(pCodTerceirizadoContrato, vMes, vAno) || !retencao.FuncaoRetencaoIntegral(tupla.getCod(), vMes, vAno)) {

                            vValorDecimoTerceiro = (vValorDecimoTerceiro / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorIncidencia = (vValorIncidencia / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);

                        }

                        /*Contabilização do valor calculado.*/

                        vTotalDecimoTerceiro = vTotalDecimoTerceiro + vValorDecimoTerceiro;
                        vTotalIncidencia = vTotalIncidencia + vValorIncidencia;

                        vDataInicio = Date.valueOf(vDataFim.toLocalDate().plusDays(1));

                    }

                }

                /*Se existe alteração de remuneração apenas.*/

                if (convencao.ExisteDuplaConvencao(tupla.getCodFuncaoContrato(), vMes, vAno, 2) && !percentual.ExisteMudancaPercentual(vCodContrato, vMes, vAno, 2)) {

                    /*Definição dos percentuais, que não se alteram no período.*/

                    vPercentualDecimoTerceiro = percentual.RetornaPercentualContrato(vCodContrato, 3, vMes, vAno, 1, 2);
                    vPercentualIncidencia = percentual.RetornaPercentualContrato(vCodContrato, 7, vMes, vAno, 1, 2);

                    /*Definição da data de início como sendo a data referência (primeiro dia do mês).*/

                    vDataInicio = vDataReferencia;

                    /*Seleciona as datas que compõem os subperíodos gerados pelas alterações de remuneração no mês.*/

                    List<Date> datas = consulta.RetornaSubperiodosMesRemuneracao(vCodContrato,
                            vMes,
                            vAno,
                            tupla.getCodFuncaoContrato(),
                            vDataReferencia);

                    /*Loop contendo das datas das alterações de remuneração que comporão os subperíodos.*/

                    for (Date data : datas) {

                        /*Definição da data fim do subperíodo.*/

                        vDataFim = data;

                        /*Definição dos dias contidos no subperíodo*/

                        vDiasSubperiodo = (int) ((ChronoUnit.DAYS.between(vDataInicio.toLocalDate(), vDataFim.toLocalDate())) + 1);

                        if (vMes == 2) {

                            vDiasSubperiodo = periodo.AjusteDiasSubperiodoFevereiro(vDataReferencia, vDataFim, vDiasSubperiodo);

                        }

                        /*Define a remuneração do cargo, que não se altera no período.*/

                        vRemuneracao = remuneracao.RetornaRemuneracaoPeriodo(tupla.getCodFuncaoContrato(), vDataInicio, vDataFim, 2);

                        if (vRemuneracao == 0) {

                            throw new NullPointerException("Erro na execução do procedimento: Remuneração não encontrada. Código -20001");

                        }

                        /*Calculo da porção correspondente ao subperíodo.*/

                        vValorDecimoTerceiro = ((vRemuneracao * (vPercentualDecimoTerceiro / 100)) / 30) * vDiasSubperiodo;
                        vValorIncidencia = (vValorDecimoTerceiro * (vPercentualIncidencia / 100));

                        /*No caso de mudança de função temos um recolhimento proporcional ao dias trabalhados no cargo,
                         situação similar para a retenção proporcional por menos de 14 dias trabalhados.*/

                        if (retencao.ExisteMudancaFuncao(pCodTerceirizadoContrato, vMes, vAno) || !retencao.FuncaoRetencaoIntegral(tupla.getCod(), vMes, vAno)) {

                            vValorDecimoTerceiro = (vValorDecimoTerceiro / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorIncidencia = (vValorIncidencia / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);

                        }

                        /*Contabilização do valor calculado.*/

                        vTotalDecimoTerceiro = vTotalDecimoTerceiro + vValorDecimoTerceiro;
                        vTotalIncidencia = vTotalIncidencia + vValorIncidencia;

                        vDataInicio = Date.valueOf(vDataFim.toLocalDate().plusDays(1));

                    }

                }

                /*Se existe alteração na remuneração e nos percentuais.*/

                if (convencao.ExisteDuplaConvencao(tupla.getCodFuncaoContrato(), vMes, vAno, 2) && percentual.ExisteMudancaPercentual(vCodContrato, vMes, vAno, 2)) {

                    /*Definição da data de início como sendo a data referência (primeiro dia do mês).*/

                    vDataInicio = vDataReferencia;

                    /*Seleciona as datas que compõem os subperíodos gerados pelas alterações de percentual e remuneração no mês.*/

                    List<Date> datas = consulta.RetornaSubperiodosMesPercentualRemuneracao(vCodContrato,
                            vMes,
                            vAno,
                            tupla.getCodFuncaoContrato(),
                            vDataReferencia);

                    /*Loop contendo das datas das alterações de percentual e remuneração que comporão os subperíodos.*/

                    for (Date data : datas) {

                        /*Definição da data fim do subperíodo.*/

                        vDataFim = data;

                        /*Definição dos dias contidos no subperíodo*/

                        vDiasSubperiodo = (int) ((ChronoUnit.DAYS.between(vDataInicio.toLocalDate(), vDataFim.toLocalDate())) + 1);

                        if (vMes == 2) {

                            vDiasSubperiodo = periodo.AjusteDiasSubperiodoFevereiro(vDataReferencia, vDataFim, vDiasSubperiodo);

                        }

                        /*Define a remuneração do cargo, que não se altera no período.*/

                        vRemuneracao = remuneracao.RetornaRemuneracaoPeriodo(tupla.getCodFuncaoContrato(), vDataInicio, vDataFim, 2);

                        if (vRemuneracao == 0) {

                            throw new NullPointerException("Erro na execução do procedimento: Remuneração não encontrada. Código -20001");

                        }

                        /*Definição dos percentuais do subperíodo.*/

                        vPercentualDecimoTerceiro = percentual.RetornaPercentualContrato(vCodContrato, 3, vDataInicio, vDataFim, 2);
                        vPercentualIncidencia = percentual.RetornaPercentualContrato(vCodContrato, 7, vDataInicio, vDataFim, 2);

                        /*Calculo da porção correspondente ao subperíodo.*/

                        vValorDecimoTerceiro = ((vRemuneracao * (vPercentualDecimoTerceiro / 100)) / 30) * vDiasSubperiodo;
                        vValorIncidencia = (vValorDecimoTerceiro * (vPercentualIncidencia / 100));

                        /*No caso de mudança de função temos um recolhimento proporcional ao dias trabalhados no cargo,
                         situação similar para a retenção proporcional por menos de 14 dias trabalhados.*/

                        if (retencao.ExisteMudancaFuncao(pCodTerceirizadoContrato, vMes, vAno) || !retencao.FuncaoRetencaoIntegral(tupla.getCod(), vMes, vAno)) {

                            vValorDecimoTerceiro = (vValorDecimoTerceiro / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorIncidencia = (vValorIncidencia / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);

                        }

                        /*Contabilização do valor calculado.*/

                        vTotalDecimoTerceiro = vTotalDecimoTerceiro + vValorDecimoTerceiro;
                        vTotalIncidencia = vTotalIncidencia + vValorIncidencia;

                        vDataInicio = Date.valueOf(vDataFim.toLocalDate().plusDays(1));

                    }


                }

            }

            if (vMes != 12) {

                vMes = vMes + 1;
            } else {

                vMes = 1;
                vAno = vAno + 1;

            }

            vDataReferencia = Date.valueOf(vAno + "-" + vMes + "-" + "01");

        } while (vDataReferencia.before(pFimContagem) || vDataReferencia.equals(pFimContagem));

        //System.out.println(vTotalDecimoTerceiro);
        //System.out.println(vTotalIncidencia);

        /*No caso de segunda parcela a movimentação gera resíduos referentes ao
         valor do décimo terceiro que é afetado pelos descontos (IRPF, INSS e etc.)*/

        if (pNumeroParcela == 1 || pNumeroParcela == 2) {

            vTotalDecimoTerceiro = vTotalDecimoTerceiro / 2;

            vTotalIncidencia = vTotalIncidencia / 2;

        }

        return new ValorRestituicaoDecimoTerceiroModel(vTotalDecimoTerceiro,
                                                       vTotalIncidencia);

    }


    /**
     * Método que registra o calculo do total de férias a ser restituído para um
     * determinado período aquisitivo.
     *
     * @param pCodTerceirizadoContrato;
     * @param pTipoRestituicao;
     * @param pNumeroParcela;
     * @param pValorDecimoTerceiro;
     * @param pValorIncidencia;
     * @param pLoginAtualizacao;
    */

    public Integer RegistraRestituicaoDecimoTerceiro (int pCodTerceirizadoContrato,
                                                      String pTipoRestituicao,
                                                      int pNumeroParcela,
                                                      Date pInicioContagem,
                                                      float pValorDecimoTerceiro,
                                                      float pValorIncidencia,
                                                      float pValorMovimentado,
                                                      String pLoginAtualizacao) {

        ConsultaTSQL consulta = new ConsultaTSQL(connection);
        InsertTSQL insert = new InsertTSQL(connection);

        /*Chaves primárias.*/

        int vCodTbRestituicao13;
        int vCodTipoRestituicao;

        /*Variáveis auxiliares.*/

        float vValor = 0;
        float vIncidencia = 0;

        /*Atribuição do cod do tipo de restituição.*/

        vCodTipoRestituicao = consulta.RetornaCodTipoRestituicao(pTipoRestituicao);

        if (vCodTipoRestituicao == 0) {

            throw new NullPointerException("Tipo de restituição não encontrada.");

        }

        /*Recuparação do próximo valor da sequência da chave primária da tabela tb_restituicao_decimo_terceiro.*/

        /*No caso de segunda parcela a movimentação gera resíduos referentes ao
         valor do décimo terceiro que é afetado pelos descontos (IRPF, INSS e etc.)*/

        if ((pNumeroParcela == 2 || pNumeroParcela == 0) && (pTipoRestituicao.equals("MOVIMENTAÇÃO"))) {

            vValor = pValorDecimoTerceiro - pValorMovimentado;

            pValorDecimoTerceiro = pValorMovimentado;

        }

        /*Provisionamento da incidência para o saldo residual no caso de movimentação.*/

        if (pTipoRestituicao.equals("MOVIMENTAÇÃO")) {

            vIncidencia = pValorIncidencia;

            pValorIncidencia = 0;

        }

        /*Gravação no banco*/

        vCodTbRestituicao13 = insert.InsertRestituicaoDecimoTerceiro(pCodTerceirizadoContrato,
                                                                     vCodTipoRestituicao,
                                                                     pNumeroParcela,
                                                                     pInicioContagem,
                                                                     pValorDecimoTerceiro,
                                                                     pValorIncidencia,
                                                                     pLoginAtualizacao);

        if (pTipoRestituicao.equals("MOVIMENTAÇÃO")) {

            insert.InsertSaldoResidualDecimoTerceiro(vCodTbRestituicao13,
                                                     vValor,
                                                     vIncidencia,
                                                     pLoginAtualizacao);

        }

        return vCodTbRestituicao13;

    }

    public Integer RecalculoRestituicaoDecimoTerceiro (int pCodRestituicaoDecimoTerceiro,
                                                       String pTipoRestituicao,
                                                       int pNumeroParcela,
                                                       Date pInicioContagem,
                                                       float pValorDecimoTerceiro,
                                                       float pValorIncidencia,
                                                       float pValorMovimentado,
                                                       String pLoginAtualizacao) {

        int vRetornoChavePrimaria;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);
        InsertTSQL insert = new InsertTSQL(connection);
        UpdateTSQL update = new UpdateTSQL(connection);
        DeleteTSQL delete = new DeleteTSQL(connection);

        int vCodTipoRestituicao = consulta.RetornaCodTipoRestituicao(pTipoRestituicao);

        RegistroRestituicaoDecimoTerceiro registro = consulta.RetornaRegistroRestituicaoDecimoTerceiro(pCodRestituicaoDecimoTerceiro);

        if (registro == null) {

            throw new NullPointerException("Registro anterior não encontrado.");

        }

        vRetornoChavePrimaria = insert.InsertHistoricoRestituicaoDecimoTerceiro(registro.getpCod(),
                                                                                registro.getpCodTipoRestituicao(),
                                                                                registro.getpParcela(),
                                                                                registro.getpDataInicioContagem(),
                                                                                registro.getpValor(),
                                                                                registro.getpIncidenciaSubmodulo41(),
                                                                                registro.getpDataReferencia(),
                                                                                registro.getpAutorizado(),
                                                                                registro.getpRestituido(),
                                                                                registro.getpObservacao(),
                                                                                registro.getpLoginAtualizacao());

        delete.DeleteSaldoResidualRescisao(pCodRestituicaoDecimoTerceiro);

        /*Variáveis auxiliares.*/

        float vValor = 0;
        float vIncidencia = 0;

        /*No caso de segunda parcela a movimentação gera resíduos referentes ao
         valor do décimo terceiro que é afetado pelos descontos (IRPF, INSS e etc.)*/

        if ((pNumeroParcela == 2 || pNumeroParcela == 0) && (pTipoRestituicao.equals("MOVIMENTAÇÃO"))) {

            vValor = pValorDecimoTerceiro - pValorMovimentado;

            pValorDecimoTerceiro = pValorMovimentado;

        }

        /*Provisionamento da incidência para o saldo residual no caso de movimentação.*/

        if (pTipoRestituicao.equals("MOVIMENTAÇÃO")) {

            vIncidencia = pValorIncidencia;

            pValorIncidencia = 0;

        }

        update.UpdateRestituicaoDecimoTerceiro(pCodRestituicaoDecimoTerceiro,
                                               vCodTipoRestituicao,
                                               pNumeroParcela,
                                               pInicioContagem,
                                               pValorDecimoTerceiro,
                                               pValorIncidencia,
                                               "",
                                               "",
                                               "",
                                               pLoginAtualizacao);

        if (pTipoRestituicao.equals("MOVIMENTAÇÃO")) {

            insert.InsertSaldoResidualDecimoTerceiro(pCodRestituicaoDecimoTerceiro,
                                                     vValor,
                                                     vIncidencia,
                                                     pLoginAtualizacao);

        }

        return vRetornoChavePrimaria;

    }

}