package br.jus.stj.siscovi.calculos;

import br.jus.stj.siscovi.dao.sql.ConsultaTSQL;
import br.jus.stj.siscovi.dao.sql.DeleteTSQL;
import br.jus.stj.siscovi.dao.sql.InsertTSQL;
import br.jus.stj.siscovi.dao.sql.UpdateTSQL;
import br.jus.stj.siscovi.model.CodFuncaoContratoECodFuncaoTerceirizadoModel;
import br.jus.stj.siscovi.model.RegistroRestituicaoRescisao;
import br.jus.stj.siscovi.model.ValorRestituicaoRescisaoModel;

import java.sql.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class RestituicaoRescisao {

    private Connection connection;

    public RestituicaoRescisao(Connection connection) {

        this.connection = connection;

    }

    /**
     * Método que recupera o total da rescisão a ser restituída.
     *
     * @param pCodTerceirizadoContrato;
     * @param pDataDesligamento;
     * @param pDataInicioFeriasIntegrais;
     * @param pDataFimFeriasIntegrais;
     * @param pDataInicioFeriasProporcionais;
     * @param pDataFimFeriasProporcionais;
     *
     */

    public ValorRestituicaoRescisaoModel CalculaRestituicaoRescisao (int pCodTerceirizadoContrato,
                                                                     Date pDataDesligamento,
                                                                     Date pDataInicioFeriasIntegrais,
                                                                     Date pDataFimFeriasIntegrais,
                                                                     Date pDataInicioFeriasProporcionais,
                                                                     Date pDataFimFeriasProporcionais) {

        ConsultaTSQL consulta = new ConsultaTSQL(connection);
        DecimoTerceiro decimoTerceiro = new DecimoTerceiro(connection);
        Saldo saldo = new Saldo(connection);

        /*Variáveis totalizadoras de valores.*/

        float vTotalFerias = 0;
        float vTotalTercoConstitucional = 0;
        float vTotalIncidenciaFerias = 0;
        float vTotalIncidenciaTerco = 0;
        float vTotalDecimoTerceiro = 0;
        float vTotalIncidenciaDecimoTerceiro = 0;
        float vTotalMultaFGTSRemuneracao = 0;
        float vTotalMultaFGTSFerias = 0;
        float vTotalMultaFGTSTerco = 0;
        float vTotalMultaFGTSDecimoTerceiro = 0;
        float vTotalMultaFGTSRestante = 0;
        float vTotalFeriasProporcional = 0;
        float vTotalTercoConstitucionalProporcional = 0;
        float vTotalIncidenciaFeriasProporcional = 0;
        float vTotalIncidenciaTercoProporcional = 0;
        float vTotalMultaFGTSFeriasProporcional = 0;
        float vTotalMultaFGTSTercoProporcional = 0;

        /*Variáveis de data.*/

        Date vDataDisponibilizacao;
        Date vDataInicioContagemDecTer;

        /*Checagem dos parâmetros passados.*/

        if (pDataDesligamento == null) {

            throw new NullPointerException("Erro na passagem dos parâmetros.");

        }

        vDataDisponibilizacao = consulta.RetornaDataDisponibilizacaoTerceirizado(pCodTerceirizadoContrato);

        vDataInicioContagemDecTer = decimoTerceiro.RetornaDataInicioContagem(pCodTerceirizadoContrato, pDataDesligamento.toLocalDate().getYear());

        vTotalDecimoTerceiro = CalcularValorRubricaRescisao(pCodTerceirizadoContrato, 3, vDataInicioContagemDecTer, pDataDesligamento);
        vTotalIncidenciaDecimoTerceiro = CalcularValorRubricaRescisao(pCodTerceirizadoContrato, 6, vDataInicioContagemDecTer, pDataDesligamento);
        vTotalMultaFGTSDecimoTerceiro = CalcularValorRubricaRescisao(pCodTerceirizadoContrato, 9, vDataInicioContagemDecTer, pDataDesligamento);
        vTotalMultaFGTSRemuneracao = CalcularValorRubricaRescisao(pCodTerceirizadoContrato, 10, vDataDisponibilizacao, pDataDesligamento);


        if (pDataInicioFeriasIntegrais != null) {

            vTotalFerias = CalcularValorRubricaRescisao(pCodTerceirizadoContrato, 1, pDataInicioFeriasIntegrais, pDataFimFeriasIntegrais);
            vTotalTercoConstitucional = CalcularValorRubricaRescisao(pCodTerceirizadoContrato, 2, pDataInicioFeriasIntegrais, pDataFimFeriasIntegrais);
            vTotalIncidenciaFerias = CalcularValorRubricaRescisao(pCodTerceirizadoContrato, 4, pDataInicioFeriasIntegrais, pDataFimFeriasIntegrais);
            vTotalIncidenciaTerco = CalcularValorRubricaRescisao(pCodTerceirizadoContrato, 5, pDataInicioFeriasIntegrais, pDataFimFeriasIntegrais);
            vTotalMultaFGTSFerias = CalcularValorRubricaRescisao(pCodTerceirizadoContrato, 7, pDataInicioFeriasIntegrais, pDataFimFeriasIntegrais);
            vTotalMultaFGTSTerco = CalcularValorRubricaRescisao(pCodTerceirizadoContrato, 8, pDataInicioFeriasIntegrais, pDataFimFeriasIntegrais);

        }

        if (pDataInicioFeriasProporcionais != null) {

            vTotalFeriasProporcional = CalcularValorRubricaRescisao(pCodTerceirizadoContrato, 1, pDataInicioFeriasProporcionais, pDataFimFeriasProporcionais);
            vTotalTercoConstitucionalProporcional = CalcularValorRubricaRescisao(pCodTerceirizadoContrato, 2, pDataInicioFeriasProporcionais, pDataFimFeriasProporcionais);
            vTotalIncidenciaFeriasProporcional = CalcularValorRubricaRescisao(pCodTerceirizadoContrato, 4, pDataInicioFeriasProporcionais, pDataFimFeriasProporcionais);
            vTotalIncidenciaTercoProporcional = CalcularValorRubricaRescisao(pCodTerceirizadoContrato, 5, pDataInicioFeriasProporcionais, pDataFimFeriasProporcionais);
            vTotalMultaFGTSFeriasProporcional = CalcularValorRubricaRescisao(pCodTerceirizadoContrato, 7, pDataInicioFeriasProporcionais, pDataFimFeriasProporcionais);
            vTotalMultaFGTSTercoProporcional = CalcularValorRubricaRescisao(pCodTerceirizadoContrato, 8, pDataInicioFeriasProporcionais, pDataFimFeriasProporcionais);
        }

        float vTotalMultaFGTS = vTotalMultaFGTSDecimoTerceiro + vTotalMultaFGTSRemuneracao + vTotalMultaFGTSFerias + vTotalMultaFGTSTerco + vTotalMultaFGTSFeriasProporcional + vTotalMultaFGTSTercoProporcional;
        vTotalMultaFGTSRestante = saldo.getSaldoIndividualContaVinculada(pCodTerceirizadoContrato, 1, 5) - vTotalMultaFGTS;

        return new ValorRestituicaoRescisaoModel(vDataInicioContagemDecTer,
                                                 vTotalDecimoTerceiro,
                                                 vTotalIncidenciaDecimoTerceiro,
                                                 vTotalMultaFGTSDecimoTerceiro,
                                                 vTotalFerias,
                                                 vTotalTercoConstitucional,
                                                 vTotalIncidenciaFerias,
                                                 vTotalIncidenciaTerco,
                                                 vTotalMultaFGTSFerias,
                                                 vTotalMultaFGTSTerco,
                                                 vTotalMultaFGTSRestante,
                                                 vTotalFeriasProporcional,
                                                 vTotalTercoConstitucionalProporcional,
                                                 vTotalIncidenciaFeriasProporcional,
                                                 vTotalIncidenciaTercoProporcional,
                                                 vTotalMultaFGTSFeriasProporcional,
                                                 vTotalMultaFGTSTercoProporcional,
                                                 vTotalMultaFGTSRemuneracao);

    }

    /**
     * Método que calcula o total da rescisão a ser restituída.
     *
     * @param pCodTerceirizadoContrato;
     * @param pRubrica;
     * @param pDataInicioCalculo;
     * @param pDataFimCalculo;
     *
     */

    public float CalcularValorRubricaRescisao (int pCodTerceirizadoContrato,
                                               int pRubrica,
                                               Date pDataInicioCalculo,
                                               Date pDataFimCalculo) {

        //Cod. pRubrica:
        // 1 -  return vTotalFerias;
        // 2 -  return vTotalTercoConstitucional;
        // 3 -  return vTotalDecimoTerceiro;
        // 4 -  return vTotalIncidenciaFerias;
        // 5 -  return vTotalIncidenciaTerco;
        // 6 -  return vTotalIncidenciaDecimoTerceiro;
        // 7 -  return vTotalMultaFGTSFerias;
        // 8 -  return vTotalMultaFGTSTerco;
        // 9 -  return vTotalMultaFGTSDecimoTerceiro;
        // 10 - return vTotalMultaFGTSRemuneracao;


        Retencao retencao = new Retencao(connection);
        Percentual percentual = new Percentual(connection);
        Periodos periodo = new Periodos(connection);
        Remuneracao remuneracao = new Remuneracao(connection);
        Saldo saldo = new Saldo(connection);
        ConsultaTSQL consulta = new ConsultaTSQL(connection);

        float vRetorno = 0;

        /*Chaves primárias.*/

        int vCodContrato;

        /*Variáveis totalizadoras de valores.*/

        float vTotalFerias = 0;
        float vTotalTercoConstitucional = 0;
        float vTotalIncidenciaFerias = 0;
        float vTotalIncidenciaTerco = 0;
        float vTotalDecimoTerceiro = 0;
        float vTotalIncidenciaDecimoTerceiro = 0;
        float vTotalMultaFGTSRemuneracao = 0;
        float vTotalMultaFGTSFerias = 0;
        float vTotalMultaFGTSTerco = 0;
        float vTotalMultaFGTSDecimoTerceiro = 0;


        /*Variáveis de valores parciais.*/

        float vValorFerias;
        float vValorTercoConstitucional;
        float vValorIncidenciaFerias;
        float vValorIncidenciaTerco;
        float vValorDecimoTerceiro;
        float vValorIncidenciaDecimoTerceiro;
        float vValorMultaFGTSRemuneracao;
        float vValorMultaFGTSFerias;
        float vValorMultaFGTSTerco;
        float vValorMultaFGTSDecimoTerceiro;

        /*Variáveis de percentuais.*/

        float vPercentualFerias;
        float vPercentualTercoConstitucional;
        float vPercentualIncidencia;
        float vPercentualDecimoTerceiro;
        float vPercentualFGTS;
        float vPercentualMultaFGTS;
        float vPercentualPenalidadeFGTS;

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

        /*Atribuiçao da data de disponibilização e do cod do contrato.*/

        vCodContrato = consulta.RetornaContratoTerceirizado(pCodTerceirizadoContrato);

        /*Define o valor das variáveis vMes e Vano de acordo com a adata de início do período aquisitivo.*/

        vMes = pDataInicioCalculo.toLocalDate().getMonthValue();
        vAno = pDataInicioCalculo.toLocalDate().getYear();

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
                    vPercentualDecimoTerceiro = percentual.RetornaPercentualContrato(vCodContrato, 3, vMes, vAno, 1, 2);
                    vPercentualIncidencia = percentual.RetornaPercentualContrato(vCodContrato, 7, vMes, vAno, 1, 2);
                    vPercentualFGTS = percentual.RetornaPercentualEstatico(vCodContrato, 4, vMes, vAno, 1, 2);
                    vPercentualPenalidadeFGTS = percentual.RetornaPercentualEstatico(vCodContrato, 6, vMes, vAno, 1, 2);
                    vPercentualMultaFGTS = percentual.RetornaPercentualEstatico(vCodContrato, 5, vMes, vAno, 1, 2);

                    if (vRemuneracao == 0) {

                        throw new NullPointerException("Erro na execução do procedimento: Remuneração não encontrada. Código -20001");

                    }

                    /*Cálculo do valor integral correspondente ao mês avaliado.*/

                    vValorFerias = (vRemuneracao * (vPercentualFerias / 100));
                    vValorTercoConstitucional = (vRemuneracao * (vPercentualTercoConstitucional / 100));
                    vValorDecimoTerceiro = (vRemuneracao * (vPercentualDecimoTerceiro / 100));
                    vValorIncidenciaFerias = (vValorFerias * (vPercentualIncidencia / 100));
                    vValorIncidenciaTerco = (vValorTercoConstitucional * (vPercentualIncidencia / 100));
                    vValorIncidenciaDecimoTerceiro = (vValorDecimoTerceiro * (vPercentualIncidencia / 100));
                    vValorMultaFGTSFerias = (vValorFerias * ((vPercentualFGTS / 100) * (vPercentualMultaFGTS / 100) * (vPercentualPenalidadeFGTS / 100)));
                    vValorMultaFGTSTerco = (vValorTercoConstitucional * ((vPercentualFGTS / 100) * (vPercentualMultaFGTS / 100) * (vPercentualPenalidadeFGTS / 100)));
                    vValorMultaFGTSDecimoTerceiro = (vValorDecimoTerceiro * ((vPercentualFGTS / 100) * (vPercentualMultaFGTS / 100) * (vPercentualPenalidadeFGTS / 100)));
                    vValorMultaFGTSRemuneracao = (vRemuneracao * ((vPercentualFGTS / 100) * (vPercentualMultaFGTS / 100) * (vPercentualPenalidadeFGTS / 100)));

                    /*o caso de mudança de função temos um recolhimento proporcional ao dias trabalhados no cargo,
                     situação similar para a retenção proporcional por menos de 14 dias trabalhados.*/

                    if (retencao.ExisteMudancaFuncao(pCodTerceirizadoContrato, vMes, vAno) || !retencao.FuncaoRetencaoIntegral(tupla.getCod(), vMes, vAno)) {

                        vValorFerias = (vValorFerias / 30) * periodo.DiasTrabalhadosMes(tupla.getCod(), vMes, vAno);
                        vValorTercoConstitucional = (vValorTercoConstitucional / 30) * periodo.DiasTrabalhadosMes(tupla.getCod(), vMes, vAno);
                        vValorDecimoTerceiro = (vValorDecimoTerceiro / 30) * periodo.DiasTrabalhadosMes(tupla.getCod(), vMes, vAno);
                        vValorIncidenciaFerias = (vValorIncidenciaFerias / 30) * periodo.DiasTrabalhadosMes(tupla.getCod(), vMes, vAno);
                        vValorIncidenciaTerco = (vValorIncidenciaTerco / 30) * periodo.DiasTrabalhadosMes(tupla.getCod(), vMes, vAno);
                        vValorIncidenciaDecimoTerceiro = (vValorIncidenciaDecimoTerceiro / 30) * periodo.DiasTrabalhadosMes(tupla.getCod(), vMes, vAno);
                        vValorMultaFGTSFerias = (vValorMultaFGTSFerias / 30) * periodo.DiasTrabalhadosMes(tupla.getCod(), vMes, vAno);
                        vValorMultaFGTSTerco = (vValorMultaFGTSTerco / 30) * periodo.DiasTrabalhadosMes(tupla.getCod(), vMes, vAno);
                        vValorMultaFGTSDecimoTerceiro = (vValorMultaFGTSDecimoTerceiro / 30) * periodo.DiasTrabalhadosMes(tupla.getCod(), vMes, vAno);
                        vValorMultaFGTSRemuneracao = (vValorMultaFGTSRemuneracao / 30) * periodo.DiasTrabalhadosMes(tupla.getCod(), vMes, vAno);

                    }

                    /*Contabilização do valor calculado.*/

                    vTotalFerias = vTotalFerias + vValorFerias;
                    vTotalTercoConstitucional = vTotalTercoConstitucional + vValorTercoConstitucional;
                    vTotalDecimoTerceiro = vTotalDecimoTerceiro + vValorDecimoTerceiro;
                    vTotalIncidenciaFerias = vTotalIncidenciaFerias + vValorIncidenciaFerias;
                    vTotalIncidenciaTerco = vTotalIncidenciaTerco + vValorIncidenciaTerco;
                    vTotalIncidenciaDecimoTerceiro = vTotalIncidenciaDecimoTerceiro + vValorIncidenciaDecimoTerceiro;
                    vTotalMultaFGTSRemuneracao = vTotalMultaFGTSRemuneracao + vValorMultaFGTSRemuneracao;
                    vTotalMultaFGTSFerias = vTotalMultaFGTSFerias + vValorMultaFGTSFerias;
                    vTotalMultaFGTSTerco = vTotalMultaFGTSTerco + vValorMultaFGTSTerco;
                    vTotalMultaFGTSDecimoTerceiro = vTotalMultaFGTSDecimoTerceiro + vValorMultaFGTSDecimoTerceiro;

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
                        vPercentualDecimoTerceiro = percentual.RetornaPercentualContrato(vCodContrato, 3, vDataInicio, vDataFim, 2);
                        vPercentualIncidencia = percentual.RetornaPercentualContrato(vCodContrato, 7, vDataInicio, vDataFim, 2);
                        vPercentualFGTS = percentual.RetornaPercentualEstatico(vCodContrato, 4, vDataInicio, vDataFim, 2);
                        vPercentualPenalidadeFGTS = percentual.RetornaPercentualEstatico(vCodContrato, 6, vDataInicio, vDataFim, 2);
                        vPercentualMultaFGTS = percentual.RetornaPercentualEstatico(vCodContrato, 5, vDataInicio, vDataFim, 2);

                        /*Calculo da porção correspondente ao subperíodo.*/

                        vValorFerias = ((vRemuneracao * (vPercentualFerias / 100)) / 30) * vDiasSubperiodo;
                        vValorTercoConstitucional = ((vRemuneracao * (vPercentualTercoConstitucional / 100)) / 30) * vDiasSubperiodo;
                        vValorDecimoTerceiro = ((vRemuneracao * (vPercentualDecimoTerceiro / 100)) / 30) * vDiasSubperiodo;
                        vValorIncidenciaFerias = (vValorFerias * (vPercentualIncidencia / 100));
                        vValorIncidenciaTerco = (vValorTercoConstitucional * (vPercentualIncidencia / 100));
                        vValorIncidenciaDecimoTerceiro = (vValorDecimoTerceiro * (vPercentualIncidencia / 100));
                        vValorMultaFGTSFerias = ((vValorFerias * ((vPercentualFGTS / 100) * (vPercentualMultaFGTS / 100) * (vPercentualPenalidadeFGTS / 100))) / 30) * vDiasSubperiodo;
                        vValorMultaFGTSTerco = ((vValorTercoConstitucional * ((vPercentualFGTS / 100) * (vPercentualMultaFGTS / 100) * (vPercentualPenalidadeFGTS / 100))) / 30) * vDiasSubperiodo;
                        vValorMultaFGTSDecimoTerceiro = ((vValorDecimoTerceiro * ((vPercentualFGTS / 100) * (vPercentualMultaFGTS / 100) * (vPercentualPenalidadeFGTS / 100))) / 30) * vDiasSubperiodo;
                        vValorMultaFGTSRemuneracao = ((vRemuneracao * ((vPercentualFGTS / 100) * (vPercentualMultaFGTS / 100) * (vPercentualPenalidadeFGTS / 100))) / 30) * vDiasSubperiodo;

                        /*No caso de mudança de função temos um recolhimento proporcional ao dias trabalhados no cargo,
                         situação similar para a retenção proporcional por menos de 30 dias trabalhados.*/

                        if (retencao.ExisteMudancaFuncao(pCodTerceirizadoContrato, vMes, vAno) || !retencao.FuncaoRetencaoIntegral(tupla.getCod(), vMes, vAno)) {

                            vValorFerias = (vValorFerias / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorTercoConstitucional = (vValorTercoConstitucional / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorDecimoTerceiro = (vValorDecimoTerceiro / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorIncidenciaFerias = (vValorIncidenciaFerias / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorIncidenciaTerco = (vValorIncidenciaTerco / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorIncidenciaDecimoTerceiro = (vValorIncidenciaDecimoTerceiro / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorMultaFGTSFerias = (vValorMultaFGTSFerias / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorMultaFGTSTerco = (vValorMultaFGTSTerco / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorMultaFGTSDecimoTerceiro = (vValorMultaFGTSDecimoTerceiro / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorMultaFGTSRemuneracao = (vValorMultaFGTSRemuneracao / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);

                        }

                        /*Contabilização do valor calculado.*/

                        vTotalFerias = vTotalFerias + vValorFerias;
                        vTotalTercoConstitucional = vTotalTercoConstitucional + vValorTercoConstitucional;
                        vTotalDecimoTerceiro = vTotalDecimoTerceiro + vValorDecimoTerceiro;
                        vTotalIncidenciaFerias = vTotalIncidenciaFerias + vValorIncidenciaFerias;
                        vTotalIncidenciaTerco = vTotalIncidenciaTerco + vValorIncidenciaTerco;
                        vTotalIncidenciaDecimoTerceiro = vTotalIncidenciaDecimoTerceiro + vValorIncidenciaDecimoTerceiro;
                        vTotalMultaFGTSRemuneracao = vTotalMultaFGTSRemuneracao + vValorMultaFGTSRemuneracao;
                        vTotalMultaFGTSFerias = vTotalMultaFGTSFerias + vValorMultaFGTSFerias;
                        vTotalMultaFGTSTerco = vTotalMultaFGTSTerco + vValorMultaFGTSTerco;
                        vTotalMultaFGTSDecimoTerceiro = vTotalMultaFGTSDecimoTerceiro + vValorMultaFGTSDecimoTerceiro;

                        vDataInicio = Date.valueOf(vDataFim.toLocalDate().plusDays(1));

                    }

                }

                /*Se existe alteração de remuneração apenas.*/

                if (convencao.ExisteDuplaConvencao(tupla.getCodFuncaoContrato(), vMes, vAno, 2) && !percentual.ExisteMudancaPercentual(vCodContrato, vMes, vAno, 2)) {

                    /*Definição dos percentuais, que não se alteram no período.*/

                    vPercentualFerias = percentual.RetornaPercentualContrato(vCodContrato, 1, vMes, vAno, 1, 2);
                    vPercentualTercoConstitucional = percentual.RetornaPercentualContrato(vCodContrato, 2, vMes, vAno, 1, 2);
                    vPercentualDecimoTerceiro = percentual.RetornaPercentualContrato(vCodContrato, 3, vMes, vAno, 1, 2);
                    vPercentualIncidencia = percentual.RetornaPercentualContrato(vCodContrato, 7, vMes, vAno, 1, 2);
                    vPercentualFGTS = percentual.RetornaPercentualEstatico(vCodContrato, 4, vMes, vAno, 1, 2);
                    vPercentualPenalidadeFGTS = percentual.RetornaPercentualEstatico(vCodContrato, 6, vMes, vAno, 1, 2);
                    vPercentualMultaFGTS = percentual.RetornaPercentualEstatico(vCodContrato, 5, vMes, vAno, 1, 2);

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
                        vValorDecimoTerceiro = ((vRemuneracao * (vPercentualDecimoTerceiro / 100)) / 30) * vDiasSubperiodo;
                        vValorIncidenciaFerias = (vValorFerias * (vPercentualIncidencia / 100));
                        vValorIncidenciaTerco = (vValorTercoConstitucional * (vPercentualIncidencia / 100));
                        vValorIncidenciaDecimoTerceiro = (vValorDecimoTerceiro * (vPercentualIncidencia / 100));
                        vValorMultaFGTSFerias = ((vValorFerias * ((vPercentualFGTS / 100) * (vPercentualMultaFGTS / 100) * (vPercentualPenalidadeFGTS / 100))) / 30) * vDiasSubperiodo;
                        vValorMultaFGTSTerco = ((vValorTercoConstitucional * ((vPercentualFGTS / 100) * (vPercentualMultaFGTS / 100) * (vPercentualPenalidadeFGTS / 100))) / 30) * vDiasSubperiodo;
                        vValorMultaFGTSDecimoTerceiro = ((vValorDecimoTerceiro * ((vPercentualFGTS / 100) * (vPercentualMultaFGTS / 100) * (vPercentualPenalidadeFGTS / 100))) / 30) * vDiasSubperiodo;
                        vValorMultaFGTSRemuneracao = ((vRemuneracao * ((vPercentualFGTS / 100) * (vPercentualMultaFGTS / 100) * (vPercentualPenalidadeFGTS / 100))) / 30) * vDiasSubperiodo;

                        /*No caso de mudança de função temos um recolhimento proporcional ao dias trabalhados no cargo,
                         situação similar para a retenção proporcional por menos de 14 dias trabalhados.*/

                        if (retencao.ExisteMudancaFuncao(pCodTerceirizadoContrato, vMes, vAno) || !retencao.FuncaoRetencaoIntegral(tupla.getCod(), vMes, vAno)) {

                            vValorFerias = (vValorFerias / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorTercoConstitucional = (vValorTercoConstitucional / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorDecimoTerceiro = (vValorDecimoTerceiro / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorIncidenciaFerias = (vValorIncidenciaFerias / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorIncidenciaTerco = (vValorIncidenciaTerco / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorIncidenciaDecimoTerceiro = (vValorIncidenciaDecimoTerceiro / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorMultaFGTSFerias = (vValorMultaFGTSFerias / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorMultaFGTSTerco = (vValorMultaFGTSTerco / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorMultaFGTSDecimoTerceiro = (vValorMultaFGTSDecimoTerceiro / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorMultaFGTSRemuneracao = (vValorMultaFGTSRemuneracao / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);

                        }

                        /*Contabilização do valor calculado.*/

                        vTotalFerias = vTotalFerias + vValorFerias;
                        vTotalTercoConstitucional = vTotalTercoConstitucional + vValorTercoConstitucional;
                        vTotalDecimoTerceiro = vTotalDecimoTerceiro + vValorDecimoTerceiro;
                        vTotalIncidenciaFerias = vTotalIncidenciaFerias + vValorIncidenciaFerias;
                        vTotalIncidenciaTerco = vTotalIncidenciaTerco + vValorIncidenciaTerco;
                        vTotalIncidenciaDecimoTerceiro = vTotalIncidenciaDecimoTerceiro + vValorIncidenciaDecimoTerceiro;
                        vTotalMultaFGTSRemuneracao = vTotalMultaFGTSRemuneracao + vValorMultaFGTSRemuneracao;
                        vTotalMultaFGTSFerias = vTotalMultaFGTSFerias + vValorMultaFGTSFerias;
                        vTotalMultaFGTSTerco = vTotalMultaFGTSTerco + vValorMultaFGTSTerco;
                        vTotalMultaFGTSDecimoTerceiro = vTotalMultaFGTSDecimoTerceiro + vValorMultaFGTSDecimoTerceiro;

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
                        vPercentualDecimoTerceiro = percentual.RetornaPercentualContrato(vCodContrato, 3, vDataInicio, vDataFim, 2);
                        vPercentualIncidencia = percentual.RetornaPercentualContrato(vCodContrato, 7, vDataInicio, vDataFim, 2);
                        vPercentualFGTS = percentual.RetornaPercentualEstatico(vCodContrato, 4, vDataInicio, vDataFim, 2);
                        vPercentualPenalidadeFGTS = percentual.RetornaPercentualEstatico(vCodContrato, 6, vDataInicio, vDataFim, 2);
                        vPercentualMultaFGTS = percentual.RetornaPercentualEstatico(vCodContrato, 5, vDataInicio, vDataFim, 2);

                        /*Calculo da porção correspondente ao subperíodo.*/

                        vValorFerias = ((vRemuneracao * (vPercentualFerias / 100)) / 30) * vDiasSubperiodo;
                        vValorTercoConstitucional = ((vRemuneracao * (vPercentualTercoConstitucional / 100)) / 30) * vDiasSubperiodo;
                        vValorDecimoTerceiro = ((vRemuneracao * (vPercentualDecimoTerceiro / 100)) / 30) * vDiasSubperiodo;
                        vValorIncidenciaFerias = (vValorFerias * (vPercentualIncidencia / 100));
                        vValorIncidenciaTerco = (vValorTercoConstitucional * (vPercentualIncidencia / 100));
                        vValorIncidenciaDecimoTerceiro = (vValorDecimoTerceiro * (vPercentualIncidencia / 100));
                        vValorMultaFGTSFerias = ((vValorFerias * ((vPercentualFGTS / 100) * (vPercentualMultaFGTS / 100) * (vPercentualPenalidadeFGTS / 100))) / 30) * vDiasSubperiodo;
                        vValorMultaFGTSTerco = ((vValorTercoConstitucional * ((vPercentualFGTS / 100) * (vPercentualMultaFGTS / 100) * (vPercentualPenalidadeFGTS / 100))) / 30) * vDiasSubperiodo;
                        vValorMultaFGTSDecimoTerceiro = ((vValorDecimoTerceiro * ((vPercentualFGTS / 100) * (vPercentualMultaFGTS / 100) * (vPercentualPenalidadeFGTS / 100))) / 30) * vDiasSubperiodo;
                        vValorMultaFGTSRemuneracao = ((vRemuneracao * ((vPercentualFGTS / 100) * (vPercentualMultaFGTS / 100) * (vPercentualPenalidadeFGTS / 100))) / 30) * vDiasSubperiodo;

                        /*No caso de mudança de função temos um recolhimento proporcional ao dias trabalhados no cargo,
                         situação similar para a retenção proporcional por menos de 14 dias trabalhados.*/

                        if (retencao.ExisteMudancaFuncao(pCodTerceirizadoContrato, vMes, vAno) || !retencao.FuncaoRetencaoIntegral(tupla.getCod(), vMes, vAno)) {

                            vValorFerias = (vValorFerias / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorTercoConstitucional = (vValorTercoConstitucional / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorDecimoTerceiro = (vValorDecimoTerceiro / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorIncidenciaFerias = (vValorIncidenciaFerias / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorIncidenciaTerco = (vValorIncidenciaTerco / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorIncidenciaDecimoTerceiro = (vValorIncidenciaDecimoTerceiro / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorMultaFGTSFerias = (vValorMultaFGTSFerias / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorMultaFGTSTerco = (vValorMultaFGTSTerco / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorMultaFGTSDecimoTerceiro = (vValorMultaFGTSDecimoTerceiro / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);
                            vValorMultaFGTSRemuneracao = (vValorMultaFGTSRemuneracao / vDiasSubperiodo) * periodo.DiasTrabalhadosPeriodo(tupla.getCod(), vDataInicio, vDataFim);

                        }

                        /*Contabilização do valor calculado.*/

                        vTotalFerias = vTotalFerias + vValorFerias;
                        vTotalTercoConstitucional = vTotalTercoConstitucional + vValorTercoConstitucional;
                        vTotalDecimoTerceiro = vTotalDecimoTerceiro + vValorDecimoTerceiro;
                        vTotalIncidenciaFerias = vTotalIncidenciaFerias + vValorIncidenciaFerias;
                        vTotalIncidenciaTerco = vTotalIncidenciaTerco + vValorIncidenciaTerco;
                        vTotalIncidenciaDecimoTerceiro = vTotalIncidenciaDecimoTerceiro + vValorIncidenciaDecimoTerceiro;
                        vTotalMultaFGTSRemuneracao = vTotalMultaFGTSRemuneracao + vValorMultaFGTSRemuneracao;
                        vTotalMultaFGTSFerias = vTotalMultaFGTSFerias + vValorMultaFGTSFerias;
                        vTotalMultaFGTSTerco = vTotalMultaFGTSTerco + vValorMultaFGTSTerco;
                        vTotalMultaFGTSDecimoTerceiro = vTotalMultaFGTSDecimoTerceiro + vValorMultaFGTSDecimoTerceiro;

                        vDataInicio = Date.valueOf(vDataFim.toLocalDate().plusDays(1));

                    }


                }

            }

            /*Contabilização do valor final (valor calculado menos restituições).*/

            if (vMes == 12 || (vMes == pDataFimCalculo.toLocalDate().getMonthValue()) && vAno == pDataFimCalculo.toLocalDate().getYear()) {

                vTotalFerias = (vTotalFerias - saldo.getSaldoContaVinculada(pCodTerceirizadoContrato, vAno, 2, 1));
                vTotalTercoConstitucional = (vTotalTercoConstitucional - saldo.getSaldoContaVinculada(pCodTerceirizadoContrato, vAno, 2, 2));
                vTotalDecimoTerceiro = vTotalDecimoTerceiro - saldo.getSaldoContaVinculada(pCodTerceirizadoContrato, vAno, 3, 3);
                vTotalIncidenciaDecimoTerceiro = vTotalIncidenciaDecimoTerceiro - saldo.getSaldoContaVinculada(pCodTerceirizadoContrato, vAno, 3, 103);
                vTotalIncidenciaFerias = (vTotalIncidenciaFerias - saldo.getSaldoContaVinculada(pCodTerceirizadoContrato, vAno, 2, 101));
                vTotalIncidenciaTerco = (vTotalIncidenciaTerco - saldo.getSaldoContaVinculada(pCodTerceirizadoContrato, vAno, 2, 102));

            }

            if (vMes != 12) {

                vMes = vMes + 1;
            } else {

                vMes = 1;
                vAno = vAno + 1;

            }

            vDataReferencia = Date.valueOf(vAno + "-" + vMes + "-" + "01");

        } while (vDataReferencia.before(pDataFimCalculo) || vDataReferencia.equals(pDataFimCalculo));

        if (pRubrica == 1) {
            vRetorno = vTotalFerias;
        }

        if (pRubrica == 2) {
            vRetorno = vTotalTercoConstitucional;
        }

        if (pRubrica == 3) {
            vRetorno = vTotalDecimoTerceiro;
        }

        if (pRubrica == 4) {
            vRetorno = vTotalIncidenciaFerias;
        }

        if (pRubrica == 5) {
            vRetorno = vTotalIncidenciaTerco;
        }

        if (pRubrica == 6) {
            vRetorno = vTotalIncidenciaDecimoTerceiro;
        }

        if (pRubrica == 7) {
            vRetorno = vTotalMultaFGTSFerias;
        }

        if (pRubrica == 8) {
            vRetorno = vTotalMultaFGTSTerco;
        }

        if (pRubrica == 9) {
            vRetorno = vTotalMultaFGTSDecimoTerceiro;
        }

        if (pRubrica == 10) {
            vRetorno = vTotalMultaFGTSRemuneracao;
        }

        return vRetorno;

    }

    /**
     * Método que registra o calculo do total da rescisão a ser restituído para um
     * determinado empregado.
     *
     * @param pCodTerceirizadoContrato;
     * @param pTipoRestituicao;
     * @param pTipoRescisao;
     * @param pDataDesligamento;
     * @param pDataInicioFeriasIntegrais;
     * @param pDataFimFeriasIntegrais;
     * @param pDataInicioFeriasProporcionais;
     * @param pDataFimFeriasProporcionais;
     * @param pDataInicioContagemDecTer;
     * @param pValorDecimoTerceiro;
     * @param pValorIncidenciaDecimoTerceiro;
     * @param pValorFGTSDecimoTerceiro;
     * @param pValorFerias;
     * @param pValorTerco;
     * @param pValorIncidenciaFerias;
     * @param pValorIncidenciaTerco;
     * @param pValorFGTSFerias;
     * @param pValorFGTSTerco;
     * @param pValorFeriasProporcional;
     * @param pValorTercoProporcional;
     * @param pValorIncidenciaFeriasProporcional;
     * @param pValorIncidenciaTercoProporcional;
     * @param pValorFGTSFeriasProporcional;
     * @param pValorFGTSTercoProporcional;
     * @param pValorFGTSSalario;
     * @param pLoginAtualizacao;
     *
     */

    public Integer RegistrarRestituicaoRescisao (int pCodTerceirizadoContrato,
                                                 String pTipoRestituicao,
                                                 String pTipoRescisao,
                                                 Date pDataDesligamento,
                                                 Date pDataInicioFeriasIntegrais,
                                                 Date pDataFimFeriasIntegrais,
                                                 Date pDataInicioFeriasProporcionais,
                                                 Date pDataFimFeriasProporcionais,
                                                 Date pDataInicioContagemDecTer,
                                                 float valorFeriasVencidasMovimentado,
                                                 float valorFeriasProporcionaisMovimentado,
                                                 float valorDecimoTerceiroMovimentado,
                                                 float pValorDecimoTerceiro,
                                                 float pValorIncidenciaDecimoTerceiro,
                                                 float pValorFGTSDecimoTerceiro,
                                                 float pValorFerias,
                                                 float pValorTerco,
                                                 float pValorIncidenciaFerias,
                                                 float pValorIncidenciaTerco,
                                                 float pValorFGTSFerias,
                                                 float pValorFGTSTerco,
                                                 float pValorFeriasProporcional,
                                                 float pValorTercoProporcional,
                                                 float pValorIncidenciaFeriasProporcional,
                                                 float pValorIncidenciaTercoProporcional,
                                                 float pValorFGTSFeriasProporcional,
                                                 float pValorFGTSTercoProporcional,
                                                 float pValorFGTSSalario,
                                                 String pLoginAtualizacao) {

        ConsultaTSQL consulta = new ConsultaTSQL(connection);
        InsertTSQL insert = new InsertTSQL(connection);

        /*Chaves Primárias*/

        int vCodTbRestituicaoRescisao;
        int vCodTipoRestituicao;
        int vCodTipoRescisao;

        /*Variáveis de controle do saldo reidual.*/

        float vIncidDecTer = 0;
        float vFGTSDecimoTerceiro = 0;
        float vFeriasIntegrais = 0;
        float vTercoIntegral = 0;
        float vIncidFerias = 0;
        float vIncidTerco = 0;
        float vFGTSFerias = 0;
        float vFGTSTerco = 0;
        float vFeriasProporcionais = 0;
        float vTercoProporcional = 0;
        float vIncidFeriasProporcional = 0;
        float vIncidTercoProporcional = 0;
        float vFGTSFeriasProporcional = 0;
        float vFGTSTercoProporcional = 0;
        float vFGTSRemuneracao = 0;
        float vDecimoTerceiro = 0;


        /*Atribuição do cod do tipo de restituição.*/

        vCodTipoRestituicao = consulta.RetornaCodTipoRestituicao(pTipoRestituicao);

        /*Atribuição do cod do tipo de rescisão.*/

        vCodTipoRescisao = consulta.RetornaCodTipoRescisao(pTipoRescisao);

        /*Provisionamento da incidência para o saldo residual no caso de movimentação.*/

        if (pTipoRestituicao.equals("MOVIMENTAÇÃO")) {

            vIncidDecTer = pValorIncidenciaDecimoTerceiro;
            vIncidFerias = pValorIncidenciaFerias;
            vIncidTerco = pValorIncidenciaTerco;
            vFGTSDecimoTerceiro = pValorFGTSDecimoTerceiro;
            vFGTSFerias = pValorFGTSFerias;
            vFGTSTerco = pValorFGTSTerco;
            vFGTSRemuneracao = pValorFGTSSalario;
            vIncidFeriasProporcional = pValorIncidenciaFeriasProporcional;
            vIncidTercoProporcional = pValorIncidenciaTercoProporcional;
            vFGTSFeriasProporcional = pValorFGTSFeriasProporcional;
            vFGTSTercoProporcional = pValorFGTSTercoProporcional;

            vFeriasIntegrais = pValorFerias;
            vTercoIntegral = pValorTerco;
            vFeriasProporcionais = pValorFeriasProporcional;
            vTercoProporcional = pValorTercoProporcional;

            pValorTerco = valorFeriasVencidasMovimentado/4;
            pValorTercoProporcional = valorFeriasProporcionaisMovimentado/4;
            pValorFerias = valorFeriasVencidasMovimentado - pValorTerco;
            pValorFeriasProporcional = valorFeriasProporcionaisMovimentado - pValorTercoProporcional;

            vFeriasIntegrais = vFeriasIntegrais - pValorFerias;
            vTercoIntegral = vTercoIntegral - pValorTerco;
            vFeriasProporcionais = vFeriasProporcionais - pValorFeriasProporcional;
            vTercoProporcional = vTercoProporcional - pValorTercoProporcional;

            vDecimoTerceiro = pValorDecimoTerceiro - valorDecimoTerceiroMovimentado;
            pValorDecimoTerceiro = valorDecimoTerceiroMovimentado;

            pValorIncidenciaDecimoTerceiro = 0;
            pValorIncidenciaFerias = 0;
            pValorIncidenciaTerco = 0;
            pValorFGTSDecimoTerceiro = 0;
            pValorFGTSFerias = 0;
            pValorFGTSTerco = 0;
            pValorFGTSSalario = 0;
            pValorIncidenciaFeriasProporcional = 0;
            pValorIncidenciaTercoProporcional = 0;
            pValorFGTSFeriasProporcional = 0;
            pValorFGTSTercoProporcional = 0;

        }

        /*Gravação no banco*/

        vCodTbRestituicaoRescisao = insert.InsertRestituicaoRescisao(pCodTerceirizadoContrato,
                                                                     vCodTipoRestituicao,
                                                                     vCodTipoRescisao,
                                                                     pDataDesligamento,
                                                                     pDataInicioFeriasIntegrais,
                                                                     pDataFimFeriasIntegrais,
                                                                     pDataInicioFeriasProporcionais,
                                                                     pDataFimFeriasProporcionais,
                                                                     pDataInicioContagemDecTer,
                                                                     pValorDecimoTerceiro,
                                                                     pValorIncidenciaDecimoTerceiro,
                                                                     pValorFGTSDecimoTerceiro,
                                                                     pValorFerias,
                                                                     pValorTerco,
                                                                     pValorIncidenciaFerias,
                                                                     pValorIncidenciaTerco,
                                                                     pValorFGTSFerias,
                                                                     pValorFGTSTerco,
                                                                     pValorFeriasProporcional,
                                                                     pValorTercoProporcional,
                                                                     pValorIncidenciaFeriasProporcional,
                                                                     pValorIncidenciaTercoProporcional,
                                                                     pValorFGTSFeriasProporcional,
                                                                     pValorFGTSTercoProporcional,
                                                                     pValorFGTSSalario,
                                                                     pLoginAtualizacao);

        if (pTipoRestituicao.equals("MOVIMENTAÇÃO")) {

            insert.InsertSaldoResidualRescisao(vCodTbRestituicaoRescisao,
                    vDecimoTerceiro,
                    vIncidDecTer,
                    vFGTSDecimoTerceiro,
                    vFeriasIntegrais,
                    vTercoIntegral,
                    vIncidFerias,
                    vIncidTerco,
                    vFGTSFerias,
                    vFGTSTerco,
                    vFeriasProporcionais,
                    vTercoProporcional,
                    vIncidFeriasProporcional,
                    vIncidTercoProporcional,
                    vFGTSFeriasProporcional,
                    vFGTSTercoProporcional,
                    vFGTSRemuneracao,
                    pLoginAtualizacao);

        }

        return vCodTbRestituicaoRescisao;

    }

    public void RecalculoRestituicaoRescisao (int pCodRestituicaoRescisao,
                                              String pTipoRestituicao,
                                              String pTipoRescisao,
                                              Date pDataDesligamento,
                                              Date pDataInicioFeriasIntegrais,
                                              Date pDataFimFeriasIntegrais,
                                              Date pDataInicioFeriasProporcionais,
                                              Date pDataFimFeriasProporcionais,
                                              Date pDataInicioContagemDecTer,
                                              float pValorDecimoTerceiro,
                                              float pValorIncidenciaDecimoTerceiro,
                                              float pValorFGTSDecimoTerceiro,
                                              float pValorFerias,
                                              float pValorTerco,
                                              float pValorIncidenciaFerias,
                                              float pValorIncidenciaTerco,
                                              float pValorFGTSFerias,
                                              float pValorFGTSTerco,
                                              float pValorFeriasProporcional,
                                              float pValorTercoProporcional,
                                              float pValorIncidenciaFeriasProporcional,
                                              float pValorIncidenciaTercoProporcional,
                                              float pValorFGTSFeriasProporcional,
                                              float pValorFGTSTercoProporcional,
                                              float pValorFGTSSalario,
                                              String pLoginAtualizacao) {

        int vRetornoChavePrimaria;
        ConsultaTSQL consulta = new ConsultaTSQL(connection);
        InsertTSQL insert = new InsertTSQL(connection);
        UpdateTSQL update = new UpdateTSQL(connection);
        DeleteTSQL delete = new DeleteTSQL(connection);

        int vCodTipoRestituicao = consulta.RetornaCodTipoRestituicao(pTipoRestituicao);
        int vCodTipoRescisao = consulta.RetornaCodTipoRescisao(pTipoRescisao);

        RegistroRestituicaoRescisao registro = consulta.RetornaRegistroRestituicaoRescisao(pCodRestituicaoRescisao);

        if (registro == null) {

            throw new NullPointerException("Registro anterior não encontrado.");

        }

        vRetornoChavePrimaria = insert.InsertHistoricoRestituicaoRescisao(registro.getpCod(),
                                                                          registro.getpCodTipoRestituicao(),
                                                                          registro.getpCodTipoRescisao(),
                                                                          registro.getpDataDesligamento(),
                                                                          registro.getpDataInicioFeriasIntegrais(),
                                                                          registro.getpDataFimFeriasIntegrais(),
                                                                          registro.getpDataInicioFeriasProporcionais(),
                                                                          registro.getpDataFimFeriasProporcionais(),
                                                                          registro.getpDataInicioContagemDecTer(),
                                                                          registro.getpValorDecimoTerceiro(),
                                                                          registro.getpIncidSubmod41DecTerceiro(),
                                                                          registro.getpIncidMultaFGTSDecTeceriro(),
                                                                          registro.getpValorFerias(),
                                                                          registro.getpValorTerco(),
                                                                          registro.getpIncidSubmod41Ferias(),
                                                                          registro.getpIncidSubmod41Terco(),
                                                                          registro.getpIncidMultaFGTSFerias(),
                                                                          registro.getpIncidMultaFGTSTerco(),
                                                                          registro.getValorFeriasProporcional(),
                                                                          registro.getValorTercoProporcional(),
                                                                          registro.getValorIncidenciaFeriasProporcional(),
                                                                          registro.getValorIncidenciaTercoProporcional(),
                                                                          registro.getValorFGTSFeriasProporcional(),
                                                                          registro.getValorFGTSTercoProporcional(),
                                                                          registro.getpMultaFGTSSalario(),
                                                                          registro.getpDataReferencia(),
                                                                          registro.getpAutorizado(),
                                                                          registro.getpRestituido(),
                                                                          registro.getpObservacao(),
                                                                          registro.getpLoginAtualizacao());

        delete.DeleteSaldoResidualRescisao(pCodRestituicaoRescisao);

        /*Variáveis de controle do saldo reidual.*/

        float vIncidDecTer = 0;
        float vFGTSDecimoTerceiro = 0;
        float vIncidFerias = 0;
        float vIncidTerco = 0;
        float vFGTSFerias = 0;
        float vFGTSTerco = 0;
        float vFGTSRemuneracao = 0;
        float vIncidFeriasProporcional = 0;
        float vIncidTercoProporcional = 0;
        float vFGTSFeriasProporcional = 0;
        float vFGTSTercoProporcional = 0;

        /*Provisionamento da incidência para o saldo residual no caso de movimentação.*/

        if (pTipoRestituicao.equals("MOVIMENTAÇÃO")) {

            vIncidDecTer = pValorIncidenciaDecimoTerceiro;
            vIncidFerias = pValorIncidenciaFerias;
            vIncidTerco = pValorIncidenciaTerco;
            vFGTSDecimoTerceiro = pValorFGTSDecimoTerceiro;
            vFGTSFerias = pValorFGTSFerias;
            vFGTSTerco = pValorFGTSTerco;
            vFGTSRemuneracao = pValorFGTSSalario;
            vIncidFeriasProporcional = pValorIncidenciaFeriasProporcional;
            vIncidTercoProporcional = pValorIncidenciaTercoProporcional;
            vFGTSFeriasProporcional = pValorFGTSFeriasProporcional;
            vFGTSTercoProporcional = pValorFGTSTercoProporcional;

            pValorIncidenciaDecimoTerceiro = 0;
            pValorIncidenciaFerias = 0;
            pValorIncidenciaTerco = 0;
            pValorFGTSDecimoTerceiro = 0;
            pValorFGTSFerias = 0;
            pValorFGTSTerco = 0;
            pValorIncidenciaFeriasProporcional = 0;
            pValorIncidenciaTercoProporcional = 0;
            pValorFGTSFeriasProporcional = 0;
            pValorFGTSTercoProporcional = 0;
            pValorFGTSSalario = 0;


        }

        update.UpdateRestituicaoRescisao(pCodRestituicaoRescisao,
                vCodTipoRestituicao,
                vCodTipoRescisao,
                pDataDesligamento,
                pDataInicioFeriasIntegrais,
                pDataFimFeriasIntegrais,
                pDataInicioFeriasProporcionais,
                pDataFimFeriasProporcionais,
                pDataInicioContagemDecTer,
                pValorDecimoTerceiro,
                pValorIncidenciaDecimoTerceiro,
                pValorFGTSDecimoTerceiro,
                pValorFerias,
                pValorTerco,
                pValorIncidenciaFerias,
                pValorIncidenciaTerco,
                pValorFGTSFerias,
                pValorFGTSTerco,
                pValorFeriasProporcional,
                pValorTercoProporcional,
                pValorIncidenciaFeriasProporcional,
                pValorIncidenciaTercoProporcional,
                pValorFGTSFeriasProporcional,
                pValorFGTSTercoProporcional,
                pValorFGTSSalario,
                "",
                "",
                "",
                pLoginAtualizacao);

        if (pTipoRestituicao.equals("MOVIMENTAÇÃO")) {

            insert.InsertSaldoResidualRescisao(pCodRestituicaoRescisao,
                    0,
                    vIncidDecTer,
                    vFGTSDecimoTerceiro,
                    0,
                    0,
                    vIncidFerias,
                    vIncidTerco,
                    vFGTSFerias,
                    vFGTSTerco,
                    0,
                    0,
                    vIncidFeriasProporcional,
                    vIncidTercoProporcional,
                    vFGTSFeriasProporcional,
                    vFGTSTercoProporcional,
                    vFGTSRemuneracao,
                    pLoginAtualizacao);

        }

    }

}