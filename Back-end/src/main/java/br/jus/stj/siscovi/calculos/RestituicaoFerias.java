package br.jus.stj.siscovi.calculos;

import br.jus.stj.siscovi.model.CodFuncaoContratoECodFuncaoTerceirizadoModel;
import br.jus.stj.siscovi.model.RegistroRestituicaoFerias;
import br.jus.stj.siscovi.model.ValorRestituicaoFeriasModel;
import br.jus.stj.siscovi.dao.sql.*;

import java.sql.*;
//import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


public class RestituicaoFerias {

    private Connection connection;

    public RestituicaoFerias(Connection connection) {

        this.connection = connection;

    }

    /**
     * Método que calcula o total de férias a ser restituído para um
     * determinado período aquisitivo.
     *
     * @param pCodTerceirizadoContrato;
     * @param pDiasVendidos;
     * @param pInicioFerias;
     * @param pFimFerias;
     * @param pInicioPeriodoAquisitivo;
     * @param pFimPeriodoAquisitivo;
     */

    public ValorRestituicaoFeriasModel CalculaRestituicaoFerias (int pCodTerceirizadoContrato,
                                                                 int pDiasVendidos,
                                                                 Date pInicioFerias,
                                                                 Date pFimFerias,
                                                                 Date pInicioPeriodoAquisitivo,
                                                                 Date pFimPeriodoAquisitivo) {

        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Retencao retencao = new Retencao(connection);
        Percentual percentual = new Percentual(connection);
        Periodos periodo = new Periodos(connection);
        Remuneracao remuneracao = new Remuneracao(connection);
        Ferias ferias = new Ferias(connection);
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        /*Chaves primárias.*/

        int vCodContrato = 0;

        /*Variáveis totalizadoras de valores.*/

        float vTotalFerias = 0;
        float vTotalTercoConstitucional = 0;
        float vTotalIncidenciaFerias = 0;
        float vTotalIncidenciaTerco = 0;

        /*Variáveis de valores parciais.*/

        float vValorFerias;
        float vValorTercoConstitucional;
        float vValorIncidenciaFerias;
        float vValorIncidenciaTerco;

        /*Variáveis de percentuais.*/

        float vPercentualFerias;
        float vPercentualTercoConstitucional;
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
        float vDiasDeFerias;
        float vDiasAdquiridos;
        int vDiasVendidos;
        boolean vFeriasMenorDeAno = false;

        /*Checagem dos parâmetros passados.*/

        if (pInicioFerias == null || pFimFerias == null || pInicioPeriodoAquisitivo == null || pFimPeriodoAquisitivo == null) {

            throw new NullPointerException("Erro na checagem dos parâmetros.");

        }

        /*Verificação do período aquisitivo menor que 1 ano.*/

        if (pInicioFerias.before(pFimPeriodoAquisitivo) && !ferias.ExisteFeriasTerceirizado(pCodTerceirizadoContrato)) {

            pFimPeriodoAquisitivo = Date.valueOf(pInicioFerias.toLocalDate().plusDays(-1));
            vFeriasMenorDeAno = true;

        } else {

            if (pInicioFerias.before(pFimPeriodoAquisitivo) && ferias.ExisteFeriasTerceirizado(pCodTerceirizadoContrato)) {

                throw new NullPointerException("Período de usufruto informado é inconsistente (dentro do período aquisitivo).");

            }

        }

        /*Carrega o código do contrato.*/

        try {

            preparedStatement = connection.prepareStatement("SELECT tc.cod_contrato FROM tb_terceirizado_contrato tc WHERE tc.cod=?");

            preparedStatement.setInt(1, pCodTerceirizadoContrato);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vCodContrato = resultSet.getInt(1);

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        /*Define o valor das variáveis vMes e Vano de acordo com a adata de inínio do período aquisitivo.*/

        vMes = pInicioPeriodoAquisitivo.toLocalDate().getMonthValue();
        vAno = pInicioPeriodoAquisitivo.toLocalDate().getYear();

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
                    vPercentualFerias = percentual.RetornaPercentualContrato(vCodContrato, 1, vMes, vAno, 1, 2);
                    vPercentualTercoConstitucional = percentual.RetornaPercentualContrato(vCodContrato, 2, vMes, vAno, 1, 2);
                    vPercentualIncidencia = percentual.RetornaPercentualContrato(vCodContrato, 7, vMes, vAno, 1, 2);

                    if (vRemuneracao == 0) {

                        throw new NullPointerException("Erro na execução do procedimento: Remuneração não encontrada. Código -20001");

                    }

                    /*Cálculo do valor integral correspondente ao mês avaliado.*/

                    vValorFerias = (vRemuneracao * (vPercentualFerias / 100));
                    vValorTercoConstitucional = (vRemuneracao * (vPercentualTercoConstitucional / 100));
                    vValorIncidenciaFerias = (vValorFerias * (vPercentualIncidencia / 100));
                    vValorIncidenciaTerco = (vValorTercoConstitucional * (vPercentualIncidencia / 100));

                    /*o caso de mudança de função temos um recolhimento proporcional ao dias trabalhados no cargo,
                    situação similar para a retenção proporcional por menos de 14 dias trabalhados.*/

                    if (retencao.ExisteMudancaFuncao(pCodTerceirizadoContrato, vMes, vAno) || !retencao.FuncaoRetencaoIntegral(tupla.getCod(), vMes, vAno)) {

                        vValorFerias = (vValorFerias / 30) * periodo.DiasTrabalhadosMes(tupla.getCod(), vMes, vAno);
                        vValorTercoConstitucional = (vValorTercoConstitucional / 30) * periodo.DiasTrabalhadosMes(tupla.getCod(), vMes, vAno);
                        vValorIncidenciaFerias = (vValorIncidenciaFerias / 30) * periodo.DiasTrabalhadosMes(tupla.getCod(), vMes, vAno);
                        vValorIncidenciaTerco = (vValorIncidenciaTerco / 30) * periodo.DiasTrabalhadosMes(tupla.getCod(), vMes, vAno);

                    }

                    /*Contabilização do valor calculado.*/

                    vTotalFerias = vTotalFerias + vValorFerias;
                    vTotalTercoConstitucional = vTotalTercoConstitucional + vValorTercoConstitucional;
                    vTotalIncidenciaFerias = vTotalIncidenciaFerias + vValorIncidenciaFerias;
                    vTotalIncidenciaTerco = vTotalIncidenciaTerco + vValorIncidenciaTerco;

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

                        vPercentualFerias = percentual.RetornaPercentualContrato(vCodContrato, 1, vDataInicio, vDataFim, 2);
                        vPercentualTercoConstitucional = percentual.RetornaPercentualContrato(vCodContrato, 2, vDataInicio, vDataFim, 2);
                        vPercentualIncidencia = percentual.RetornaPercentualContrato(vCodContrato, 7, vDataInicio, vDataFim, 2);

                        /*Calculo da porção correspondente ao subperíodo.*/

                        vValorFerias = ((vRemuneracao * (vPercentualFerias / 100)) / 30) * vDiasSubperiodo;
                        vValorTercoConstitucional = ((vRemuneracao * (vPercentualTercoConstitucional / 100)) / 30) * vDiasSubperiodo;
                        vValorIncidenciaFerias = (vValorFerias * (vPercentualIncidencia / 100));
                        vValorIncidenciaTerco = (vValorTercoConstitucional * (vPercentualIncidencia / 100));

                        /*No caso de mudança de função temos um recolhimento proporcional ao dias trabalhados no cargo,
                         situação similar para a retenção proporcional por menos de 30 dias trabalhados.*/

                        if (retencao.ExisteMudancaFuncao(pCodTerceirizadoContrato, vMes, vAno) || !retencao.FuncaoRetencaoIntegral(tupla.getCod(), vMes, vAno)) {

                            vValorFerias = (vValorFerias / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorTercoConstitucional = (vValorTercoConstitucional / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorIncidenciaFerias = (vValorIncidenciaFerias / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorIncidenciaTerco = (vValorIncidenciaTerco / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);

                        }

                        /*Contabilização do valor calculado.*/

                        vTotalFerias = vTotalFerias + vValorFerias;
                        vTotalTercoConstitucional = vTotalTercoConstitucional + vValorTercoConstitucional;
                        vTotalIncidenciaFerias = vTotalIncidenciaFerias + vValorIncidenciaFerias;
                        vTotalIncidenciaTerco = vTotalIncidenciaTerco + vValorIncidenciaTerco;

                        vDataInicio = Date.valueOf(vDataFim.toLocalDate().plusDays(1));

                    }

                }

                /*Se existe alteração de remuneração apenas.*/

                if (convencao.ExisteDuplaConvencao(tupla.getCodFuncaoContrato(), vMes, vAno, 2) && !percentual.ExisteMudancaPercentual(vCodContrato, vMes, vAno, 2)) {

                    /*Definição dos percentuais, que não se alteram no período.*/

                    vPercentualFerias = percentual.RetornaPercentualContrato(vCodContrato, 1, vMes, vAno, 1, 2);
                    vPercentualTercoConstitucional = percentual.RetornaPercentualContrato(vCodContrato, 2, vMes, vAno, 1, 2);
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

                        vValorFerias = ((vRemuneracao * (vPercentualFerias / 100)) / 30) * vDiasSubperiodo;
                        vValorTercoConstitucional = ((vRemuneracao * (vPercentualTercoConstitucional / 100)) / 30) * vDiasSubperiodo;
                        vValorIncidenciaFerias = (vValorFerias * (vPercentualIncidencia / 100));
                        vValorIncidenciaTerco = (vValorTercoConstitucional * (vPercentualIncidencia / 100));

                        /*No caso de mudança de função temos um recolhimento proporcional ao dias trabalhados no cargo,
                         situação similar para a retenção proporcional por menos de 14 dias trabalhados.*/

                        if (retencao.ExisteMudancaFuncao(pCodTerceirizadoContrato, vMes, vAno) || !retencao.FuncaoRetencaoIntegral(tupla.getCod(), vMes, vAno)) {

                            vValorFerias = (vValorFerias / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorTercoConstitucional = (vValorTercoConstitucional / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorIncidenciaFerias = (vValorIncidenciaFerias / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorIncidenciaTerco = (vValorIncidenciaTerco / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);

                        }

                        /*Contabilização do valor calculado.*/

                        vTotalFerias = vTotalFerias + vValorFerias;
                        vTotalTercoConstitucional = vTotalTercoConstitucional + vValorTercoConstitucional;
                        vTotalIncidenciaFerias = vTotalIncidenciaFerias + vValorIncidenciaFerias;
                        vTotalIncidenciaTerco = vTotalIncidenciaTerco + vValorIncidenciaTerco;

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

                        vPercentualFerias = percentual.RetornaPercentualContrato(vCodContrato, 1, vDataInicio, vDataFim, 2);
                        vPercentualTercoConstitucional = percentual.RetornaPercentualContrato(vCodContrato, 2, vDataInicio, vDataFim, 2);
                        vPercentualIncidencia = percentual.RetornaPercentualContrato(vCodContrato, 7, vDataInicio, vDataFim, 2);

                        /*Calculo da porção correspondente ao subperíodo.*/

                        vValorFerias = ((vRemuneracao * (vPercentualFerias / 100)) / 30) * vDiasSubperiodo;
                        vValorTercoConstitucional = ((vRemuneracao * (vPercentualTercoConstitucional / 100)) / 30) * vDiasSubperiodo;
                        vValorIncidenciaFerias = (vValorFerias * (vPercentualIncidencia / 100));
                        vValorIncidenciaTerco = (vValorTercoConstitucional * (vPercentualIncidencia / 100));

                        /*No caso de mudança de função temos um recolhimento proporcional ao dias trabalhados no cargo,
                         situação similar para a retenção proporcional por menos de 14 dias trabalhados.*/

                        if (retencao.ExisteMudancaFuncao(pCodTerceirizadoContrato, vMes, vAno) || !retencao.FuncaoRetencaoIntegral(tupla.getCod(), vMes, vAno)) {

                            vValorFerias = (vValorFerias / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorTercoConstitucional = (vValorTercoConstitucional / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorIncidenciaFerias = (vValorIncidenciaFerias / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorIncidenciaTerco = (vValorIncidenciaTerco / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);

                        }

                        /*Contabilização do valor calculado.*/

                        vTotalFerias = vTotalFerias + vValorFerias;
                        vTotalTercoConstitucional = vTotalTercoConstitucional + vValorTercoConstitucional;
                        vTotalIncidenciaFerias = vTotalIncidenciaFerias + vValorIncidenciaFerias;
                        vTotalIncidenciaTerco = vTotalIncidenciaTerco + vValorIncidenciaTerco;

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

        } while (vDataReferencia.before(pFimPeriodoAquisitivo) || vDataReferencia.equals(pFimPeriodoAquisitivo));

        /*Atribuição de valor a vDiasVendidos*/

        if (pDiasVendidos == 0) {

            vDiasVendidos = 0;

        } else {

            vDiasVendidos = pDiasVendidos;

        }

        /*Dias de férias usufruídos para o cálculo proporcional.*/

        vDiasDeFerias = (ChronoUnit.DAYS.between(pInicioFerias.toLocalDate(), pFimFerias.toLocalDate()) + 1 + vDiasVendidos);

        /*Dias de férias adquiridos no período aquisitivo.*/

        vDiasAdquiridos = ferias.DiasPeriodoAquisitivo(pInicioPeriodoAquisitivo, pFimPeriodoAquisitivo);

        /*Definição do montante proporcional a ser restituído*/

        if ((vDiasDeFerias > vDiasAdquiridos) && !vFeriasMenorDeAno) {

            throw new NullPointerException("Foram concedidos mais dias de férias do que o disponível para o período aquisitivo informado.");

        } else {

            if ((vDiasDeFerias > vDiasAdquiridos) && vFeriasMenorDeAno) vDiasDeFerias = vDiasAdquiridos;

        }

        vTotalFerias = (vTotalFerias/vDiasAdquiridos) * vDiasDeFerias;
        vTotalIncidenciaFerias = (vTotalIncidenciaFerias/vDiasAdquiridos) * vDiasDeFerias;
        vTotalIncidenciaTerco = (vTotalIncidenciaTerco/vDiasAdquiridos) * vDiasDeFerias;

        /*Cancelamento do terço constitucional para parcela diferente da única ou primeira.*/

        if (ferias.ParcelasConcedidas(pCodTerceirizadoContrato, pInicioPeriodoAquisitivo, pFimPeriodoAquisitivo) > 0) {

            vTotalTercoConstitucional = 0;

        }

        return new ValorRestituicaoFeriasModel(vTotalFerias,
                                               vTotalTercoConstitucional,
                                               vTotalIncidenciaFerias,
                                               vTotalIncidenciaTerco,
                                               pInicioPeriodoAquisitivo,
                                               pFimPeriodoAquisitivo);

    }

    /**
     * Método que registra o cálculo do total de férias a ser restituído para um
     * determinado período aquisitivo.
     *
     * @param pCodTerceirizadoContrato;
     * @param pTipoRestituicao;
     * @param pDiasVendidos;
     * @param pInicioFerias;
     * @param pFimFerias;
     * @param pInicioPeriodoAquisitivo;
     * @param pFimPeriodoAquisitivo;
     * @param pValorMovimentado;
     * @param pParcela;
     * @param pLoginAtualizacao;
     */

    public Integer RegistraRestituicaoFerias (int pCodTerceirizadoContrato,
                                              String pTipoRestituicao,
                                              int pDiasVendidos,
                                              Date pInicioFerias,
                                              Date pFimFerias,
                                              Date pInicioPeriodoAquisitivo,
                                              Date pFimPeriodoAquisitivo,
                                              int pParcela,
                                              float pValorMovimentado,
                                              float pTotalFerias,
                                              float pTotalTercoConstitucional,
                                              float pTotalIncidenciaFerias,
                                              float pTotalIncidenciaTerco,
                                              String pLoginAtualizacao) {

        ConsultaTSQL consulta = new ConsultaTSQL(connection);
        InsertTSQL insert = new InsertTSQL(connection);

        /*Chaves primárias.*/

        int vCodTbRestituicaoFerias;

        /*Atribuição do cod do tipo de restituição.*/

        int vCodTipoRestituicao = consulta.RetornaCodTipoRestituicao(pTipoRestituicao);

        /*Variáveis auxiliares.*/

        float vIncidenciaFerias = 0;
        float vIncidenciaTerco = 0;
        float vTerco = 0;
        float vFerias = 0;

        /*Provisionamento da incidência para o saldo residual no caso de movimentação.*/

        if (pTipoRestituicao.equals("MOVIMENTAÇÃO")) {

            vIncidenciaFerias = pTotalIncidenciaFerias;
            vIncidenciaTerco = pTotalIncidenciaTerco;

            vTerco = pTotalTercoConstitucional;
            vFerias = pTotalFerias;

            pTotalTercoConstitucional = pValorMovimentado/4;
            pTotalFerias = pValorMovimentado - pTotalTercoConstitucional;

            vTerco = vTerco - pTotalTercoConstitucional;
            vFerias = vFerias - pTotalFerias;

            pTotalIncidenciaFerias = 0;
            pTotalIncidenciaTerco = 0;

        }

        /*Gravação no banco*/

        vCodTbRestituicaoFerias = insert.InsertRestituicaoFerias(pCodTerceirizadoContrato,
                                                                 vCodTipoRestituicao,
                                                                 pDiasVendidos,
                                                                 pInicioFerias,
                                                                 pFimFerias,
                                                                 pInicioPeriodoAquisitivo,
                                                                 pFimPeriodoAquisitivo,
                                                                 pParcela,
                                                                 pTotalFerias,
                                                                 pTotalTercoConstitucional,
                                                                 pTotalIncidenciaFerias,
                                                                 pTotalIncidenciaTerco,
                                                                 pLoginAtualizacao);

        if (pTipoRestituicao.equals("MOVIMENTAÇÃO")) {

            insert.InsertSaldoResidualFerias(vCodTbRestituicaoFerias,
                                             vFerias,
                                             vTerco,
                                             vIncidenciaFerias,
                                             vIncidenciaTerco,
                                             pLoginAtualizacao);

        }

        return vCodTbRestituicaoFerias;

    }

    public void RecalculoRestituicaoFerias (int pCodRestituicaoFerias,
                                               String pTipoRestituicao,
                                               int pDiasVendidos,
                                               Date pInicioFerias,
                                               Date pFimFerias,
                                               Date pInicioPeriodoAquisitivo,
                                               Date pFimPeriodoAquisitivo,
                                               int pParcela,
                                               float pValorMovimentado,
                                               float pTotalFerias,
                                               float pTotalTercoConstitucional,
                                               float pTotalIncidenciaFerias,
                                               float pTotalIncidenciaTerco,
                                               String pLoginAtualizacao) {

        int vRetornoChavePrimaria;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);
        InsertTSQL insert = new InsertTSQL(connection);
        UpdateTSQL update = new UpdateTSQL(connection);
        DeleteTSQL delete = new DeleteTSQL(connection);

        int vCodTipoRestituicao = consulta.RetornaCodTipoRestituicao(pTipoRestituicao);

        RegistroRestituicaoFerias registro = consulta.RetornaRegistroRestituicaoFerias(pCodRestituicaoFerias);

        if (registro == null) {

            throw new NullPointerException("Registro anterior não encontrado.");

        }



        vRetornoChavePrimaria = insert.InsertHistoricoRestituicaoFerias(registro.getpCod(),
                                                                        registro.getpCodTipoRestituicao(),
                                                                        registro.getpDataInicioPeriodoAquisitivo(),
                                                                        registro.getpDataFimPeriodoAquisitivo(),
                                                                        registro.getpDataInicioUsufruto(),
                                                                        registro.getpDataFimUsufruto(),
                                                                        registro.getpValorFerias(),
                                                                        registro.getpValorTercoConstitucional(),
                                                                        registro.getpIncidenciaSubmod41Ferias(),
                                                                        registro.getpIncidenciaSubmod41Terco(),
                                                                        registro.getpParcela(),
                                                                        registro.getpDataReferencia(),
                                                                        registro.getpDiasVendidos(),
                                                                        registro.getpAutorizado(),
                                                                        registro.getpRestituido(),
                                                                        registro.getpObservacao(),
                                                                        registro.getpLoginAtualizacao());

        delete.DeleteSaldoResidualRescisao(pCodRestituicaoFerias);

                /*Variáveis auxiliares.*/

        float vValorIncidenciaFerias = 0;
        float vValorIncidenciaTerco = 0;
        float vValorTerco = 0;
        float vValorFerias = 0;

        /*Provisionamento da incidência para o saldo residual no caso de movimentação.*/

        if (pTipoRestituicao.equals("MOVIMENTAÇÃO")) {

            vValorIncidenciaFerias = pTotalIncidenciaFerias;
            vValorIncidenciaTerco = pTotalIncidenciaTerco;

            vValorTerco = pTotalTercoConstitucional;
            vValorFerias = pTotalFerias;

            pTotalTercoConstitucional = pValorMovimentado/4;
            pTotalFerias = pValorMovimentado - pTotalTercoConstitucional;

            vValorTerco = vValorTerco - pTotalTercoConstitucional;
            vValorFerias = vValorFerias - pTotalFerias;

            pTotalIncidenciaTerco = 0;
            pTotalIncidenciaFerias = 0;

        }

        update.UpdateRestituicaoFerias(pCodRestituicaoFerias,
                                       vCodTipoRestituicao,
                                       pInicioPeriodoAquisitivo,
                                       pFimPeriodoAquisitivo,
                                       pInicioFerias,
                                       pFimFerias,
                                       pDiasVendidos,
                                       pTotalFerias,
                                       pTotalTercoConstitucional,
                                       pTotalIncidenciaFerias,
                                       pTotalIncidenciaTerco,
                                       pParcela,
                                       "",
                                       "",
                                      "",
                                       pLoginAtualizacao);

        if (pTipoRestituicao.equals("MOVIMENTAÇÃO")) {

            insert.InsertSaldoResidualFerias(pCodRestituicaoFerias,
                    vValorFerias,
                    vValorTerco,
                    vValorIncidenciaFerias,
                    vValorIncidenciaTerco,
                    pLoginAtualizacao);

        }

    }

}