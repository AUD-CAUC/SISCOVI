package br.jus.stj.siscovi.test;

import br.jus.stj.siscovi.dao.ConnectSQLServer;
import br.jus.stj.siscovi.dao.sql.ConsultaTSQL;
import br.jus.stj.siscovi.dao.sql.DeleteTSQL;
import br.jus.stj.siscovi.model.*;

public class TestConsultaTSQL {

    public static void main (String[] args) {

        ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        ConsultaTSQL consulta = new ConsultaTSQL(connectSQLServer.dbConnect());
        DeleteTSQL delete = new DeleteTSQL(connectSQLServer.dbConnect());

        int retorno;

        retorno = consulta.RetornaCodSequenceTable("TB_PERCENTUAL_DINAMICO");

        System.out.print("RetornaCodSequenceTable: " + retorno + "\n\n");

        RegistroRubricaModel registroRubrica = consulta.RetornaRegistroRubrica(1);

        System.out.print("RetornaRegistroRubrica: \n");
        System.out.println(registroRubrica.getpCod());
        System.out.println(registroRubrica.getpNome());
        System.out.println(registroRubrica.getpSigla());
        System.out.println(registroRubrica.getpDescricao());
        System.out.println(registroRubrica.getpLoginAtualizacao());
        System.out.println(registroRubrica.getpDataAtualizacao());

        RegistroContrato registroContrato = consulta.RetornaRegistroContrato(1);

        System.out.print("\nRetornaRegistroContrato: \n");
        System.out.println(registroContrato.getpCod());
        System.out.println(registroContrato.getpNomeEmpresa());
        System.out.println(registroContrato.getpCnpj());
        System.out.println(registroContrato.getpNumeroContrato());
        System.out.println(registroContrato.getpNumeroProcessoStj());
        System.out.println(registroContrato.getpSeAtivo());
        System.out.println(registroContrato.getpObjeto());
        System.out.println(registroContrato.getpLoginAtualizacao());
        System.out.println(registroContrato.getpDataAtualizacao());

        RegistroConvencaoColetiva registroConvencaoColetiva = consulta.RetornaRegistroConvencaoColetiva(1);

        System.out.print("\nRetornaRegistroConvencaoColetiva: \n");
        System.out.println(registroConvencaoColetiva.getpCod());
        System.out.println(registroConvencaoColetiva.getpSigla());
        System.out.println(registroConvencaoColetiva.getpDataBase());
        System.out.println(registroConvencaoColetiva.getpDescricao());
        System.out.println(registroConvencaoColetiva.getpLoginAtualizacao());
        System.out.println(registroConvencaoColetiva.getpDataAtualizacao());

        RegistroEventoContratual registroEventoContratual = consulta.RetornaRegistroEventoContratual(1);

        System.out.print("\nRetornaRegistroEventoContratual: \n");
        System.out.println(registroEventoContratual.getpCod());
        System.out.println(registroEventoContratual.getpCodContrato());
        System.out.println(registroEventoContratual.getpCodTipoEvento());
        System.out.println(registroEventoContratual.getpProrrogacao());
        System.out.println(registroEventoContratual.getpAssunto());
        System.out.println(registroEventoContratual.getpDataInicioVigencia());
        System.out.println(registroEventoContratual.getpDataFimVigencia());
        System.out.println(registroEventoContratual.getpDataAssinatura());
        System.out.println(registroEventoContratual.getpLoginAtualizacao());
        System.out.println(registroEventoContratual.getpDataAtualizacao());

        RegistroFuncao registroFuncao = consulta.RetornaRegistroFuncao(1);

        System.out.print("\nRetornaRegistroFuncao: \n");
        System.out.println(registroFuncao.getpCod());
        System.out.println(registroFuncao.getpNome());
        System.out.println(registroFuncao.getpDescricao());
        System.out.println(registroFuncao.getpLoginAtualizacao());
        System.out.println(registroFuncao.getpDataAtualizacao());

        RegistroFuncaoContrato registroFuncaoContrato = consulta.RetornaRegistroFuncaoContrato(1);

        System.out.print("\nRetornaRegistroFuncaoContrato: \n");
        System.out.println(registroFuncaoContrato.getpCod());
        System.out.println(registroFuncaoContrato.getpCodContrato());
        System.out.println(registroFuncaoContrato.getpCodFuncao());
        System.out.println(registroFuncaoContrato.getpDescricao());
        System.out.println(registroFuncaoContrato.getpLoginAtualizacao());
        System.out.println(registroFuncaoContrato.getpDataAtualizacao());

        RegistroHistoricoGestaoContrato registroHistoricoGestaoContrato = consulta.RetornaRegistroHistoricoGestaoContrato(1);

        System.out.print("\nRetornaRegistroHistoricoGestaoContrato: \n");
        System.out.println(registroHistoricoGestaoContrato.getpCod());
        System.out.println(registroHistoricoGestaoContrato.getpCodContrato());
        System.out.println(registroHistoricoGestaoContrato.getpCodUsuario());
        System.out.println(registroHistoricoGestaoContrato.getpCodPerfilGestao());
        System.out.println(registroHistoricoGestaoContrato.getpDataInicio());
        System.out.println(registroHistoricoGestaoContrato.getpDataFim());
        System.out.println(registroHistoricoGestaoContrato.getpLoginAtualizacao());
        System.out.println(registroHistoricoGestaoContrato.getpDataAtualizacao());

        RegistroFuncaoTerceirizado registroFuncaoTerceirizado = consulta.RetornaRegistroFuncaoTerceirizado(1);

        System.out.print("\nRetornaRegistroFuncaoTerceirizado: \n");
        System.out.println(registroFuncaoTerceirizado.getpCod());
        System.out.println(registroFuncaoTerceirizado.getpCodTerceirizadoContrato());
        System.out.println(registroFuncaoTerceirizado.getpCodFuncaoContrato());
        System.out.println(registroFuncaoTerceirizado.getpDataInicio());
        System.out.println(registroFuncaoTerceirizado.getpDataFim());
        System.out.println(registroFuncaoTerceirizado.getpLoginAtualizacao());
        System.out.println(registroFuncaoTerceirizado.getpDataAtualizacao());

        RegistroPercentualContrato registroPercentualContrato = consulta.RetornaRegistroPercentualContrato(1);

        System.out.print("\nRetornaRegistroPercentualContrato: \n");
        System.out.println(registroPercentualContrato.getpCod());
        System.out.println(registroPercentualContrato.getpCodContrato());
        System.out.println(registroPercentualContrato.getpCodRubrica());
        System.out.println(registroPercentualContrato.getpPercentual());
        System.out.println(registroPercentualContrato.getpDataInicio());
        System.out.println(registroPercentualContrato.getpDataFim());
        System.out.println(registroPercentualContrato.getpDataAditamento());
        System.out.println(registroPercentualContrato.getpLoginAtualizacao());
        System.out.println(registroPercentualContrato.getpDataAtualizacao());

        RegistroPercentualEstatico registroPercentualEstatico = consulta.RetornaRegistroPercentualEstatico(1);

        System.out.print("\nRetornaRegistroPercentualEstatico: \n");
        System.out.println(registroPercentualEstatico.getpCod());
        System.out.println(registroPercentualEstatico.getpCodRubrica());
        System.out.println(registroPercentualEstatico.getpPercentual());
        System.out.println(registroPercentualEstatico.getpDataInicio());
        System.out.println(registroPercentualEstatico.getpDataFim());
        System.out.println(registroPercentualEstatico.getpDataAditamento());
        System.out.println(registroPercentualEstatico.getpLoginAtualizacao());
        System.out.println(registroPercentualEstatico.getpDataAtualizacao());

        RegistroPerfilGestao registroPerfilGestao = consulta.RetornaRegistroPerfilGestao(1);

        System.out.print("\nRetornaRegistroPerfilGestao: \n");
        System.out.println(registroPerfilGestao.getpCod());
        System.out.println(registroPerfilGestao.getpNome());
        System.out.println(registroPerfilGestao.getpSigla());
        System.out.println(registroPerfilGestao.getpDescricao());
        System.out.println(registroPerfilGestao.getpLoginAtualizacao());
        System.out.println(registroPerfilGestao.getpDataAtualizacao());

        RegistroPerfilUsuario registroPerfilUsuario = consulta.RetornaRegistroPerfilUsuario(1);

        System.out.print("\nRetornaRegistroPerfilUsuario: \n");
        System.out.println(registroPerfilUsuario.getpCod());
        System.out.println(registroPerfilUsuario.getpNome());
        System.out.println(registroPerfilUsuario.getpSigla());
        System.out.println(registroPerfilUsuario.getpDescricao());
        System.out.println(registroPerfilUsuario.getpLoginAtualizacao());
        System.out.println(registroPerfilUsuario.getpDataAtualizacao());

        RegistroRemuneracaoFunCon registroRemuneracaoFunCon = consulta.RetornaRegistroRemuneracaoFunCon(1);

        System.out.print("\nRetornaRegistroRemuneracaoFunCon: \n");
        System.out.println(registroRemuneracaoFunCon.getpCod());
        System.out.println(registroRemuneracaoFunCon.getpCodFuncaoContrato());
        System.out.println(registroRemuneracaoFunCon.getpCodConvencaoColetiva());
        System.out.println(registroRemuneracaoFunCon.getpDataInicio());
        System.out.println(registroRemuneracaoFunCon.getpDataFim());
        System.out.println(registroRemuneracaoFunCon.getpDataAditamento());
        System.out.println(registroRemuneracaoFunCon.getpRemuneracao());
        System.out.println(registroRemuneracaoFunCon.getpTrienios());
        System.out.println(registroRemuneracaoFunCon.getpAdicionais());
        System.out.println(registroRemuneracaoFunCon.getpLoginAtualizacao());
        System.out.println(registroRemuneracaoFunCon.getpDataAtualizacao());

        RegistroTerceirizado registroTerceirizado = consulta.RetornaRegistroTerceirizado(1);

        System.out.print("\nRetornaRegistroTerceirizado: \n");
        System.out.println(registroTerceirizado.getpCod());
        System.out.println(registroTerceirizado.getpCpf());
        System.out.println(registroTerceirizado.getpAtivo());
        System.out.println(registroTerceirizado.getpLoginAtualizacao());
        System.out.println(registroTerceirizado.getpDataAtualizacao());

        RegistroTerceirizadoContrato registroTerceirizadoContrato = consulta.RetornaRegistroTerceirizadoContrato(1);

        System.out.print("\nRetornaRegistroTerceirizadoContrato: \n");
        System.out.println(registroTerceirizadoContrato.getpCod());
        System.out.println(registroTerceirizadoContrato.getpCodContrato());
        System.out.println(registroTerceirizadoContrato.getpCodTerceirizado());
        System.out.println(registroTerceirizadoContrato.getpDataDisponibilizacao());
        System.out.println(registroTerceirizadoContrato.getpDataDesligamento());
        System.out.println(registroTerceirizadoContrato.getpLoginAtualizacao());
        System.out.println(registroTerceirizadoContrato.getpDataAtualizacao());

        RegistroTipoEventoContratual registroTipoEventoContratual = consulta.RetornaRegistroTipoEventoContratual(1);

        System.out.print("\nRetornaRegistroTipoEventoContratual: \n");
        System.out.println(registroTipoEventoContratual.getpCod());
        System.out.println(registroTipoEventoContratual.getpTipo());
        System.out.println(registroTipoEventoContratual.getpLoginAtualizacao());
        System.out.println(registroTipoEventoContratual.getpDataAtualizacao());

        RegistroTipoRescisao registroTipoRescisao = consulta.RetornaRegistroTipoRescisao(1);

        System.out.print("\nRetornaRegistroTipoRescisao: \n");
        System.out.println(registroTipoRescisao.getpCod());
        System.out.println(registroTipoRescisao.getpTipoRescisao());
        System.out.println(registroTipoRescisao.getpLoginAtualizacao());
        System.out.println(registroTipoRescisao.getpDataAtualizacao());

        RegistroTipoRestituicao registroTipoRestituicao = consulta.RetornaRegistroTipoRestituicao(1);

        System.out.print("\nRetornaRegistroTipoRestituicao: \n");
        System.out.println(registroTipoRestituicao.getpCod());
        System.out.println(registroTipoRestituicao.getpNome());
        System.out.println(registroTipoRestituicao.getpLoginAtualizacao());
        System.out.println(registroTipoRestituicao.getpDataAtualizacao());

        RegistroTotalMensalAReter registroTotalMensalAReter = consulta.RetornaRegistroTotalMensalAReter(1);

        System.out.print("\nRetornaRegistroTotalMensalAReter: \n");
        System.out.println(registroTotalMensalAReter.getpCod());
        System.out.println(registroTotalMensalAReter.getpCodTerceirizadoContrato());
        System.out.println(registroTotalMensalAReter.getpCodFuncaoTerceirizado());
        System.out.println(registroTotalMensalAReter.getpFerias());
        System.out.println(registroTotalMensalAReter.getpTercoConstitucional());
        System.out.println(registroTotalMensalAReter.getpDecimoTerceiro());
        System.out.println(registroTotalMensalAReter.getpIncidenciaSubmodulo41());
        System.out.println(registroTotalMensalAReter.getpMultaFgts());
        System.out.println(registroTotalMensalAReter.getpTotal());
        System.out.println(registroTotalMensalAReter.getpDataReferencia());
        System.out.println(registroTotalMensalAReter.getpAutorizado());
        System.out.println(registroTotalMensalAReter.getpRetido());
        System.out.println(registroTotalMensalAReter.getpObservacao());
        System.out.println(registroTotalMensalAReter.getpLoginAtualizacao());
        System.out.println(registroTotalMensalAReter.getpDataAtualizacao());

        RegistroUsuario registroUsuario = consulta.RetornaRegistroUsuario(1);

        System.out.print("\nRetornaRegistroUsuario: \n");
        System.out.println(registroUsuario.getpCod());
        System.out.println(registroUsuario.getpNome());
        System.out.println(registroUsuario.getpLogin());
        System.out.println(registroUsuario.getpPassword());
        System.out.println(registroTotalMensalAReter.getpLoginAtualizacao());
        System.out.println(registroTotalMensalAReter.getpDataAtualizacao());



/*
        RegistroHistRestituicaoDecTer registroHistRestituicaoDecTer = consulta.RetornaRegistroHistRestituicaoDecTer(2);

        System.out.print("\nRetornaRegistroHistRestituicaoDecTer: \n");
        System.out.println(registroHistRestituicaoDecTer.getpCod());
        System.out.println(registroHistRestituicaoDecTer.getpCodRestituicaoDecTerceiro());
        System.out.println(registroHistRestituicaoDecTer.getpCodTipoRestituicao());
        System.out.println(registroHistRestituicaoDecTer.getpParcela());
        System.out.println(registroHistRestituicaoDecTer.getpDataInicioContagem());
        System.out.println(registroHistRestituicaoDecTer.getpValor());
        System.out.println(registroHistRestituicaoDecTer.getpIncidenciaSubmodulo41());
        System.out.println(registroHistRestituicaoDecTer.getpDataReferencia());
        System.out.println(registroHistRestituicaoDecTer.getpAutorizado());
        System.out.println(registroHistRestituicaoDecTer.getpRestituido());
        System.out.println(registroHistRestituicaoDecTer.getpObservacao());
        System.out.println(registroHistRestituicaoDecTer.getpLoginAtualizacao());
        System.out.println(registroHistRestituicaoDecTer.getpDataAtualizacao());

        retorno = delete.DeleteRegistro(2, "tb_hist_restituicao_dec_ter");

        System.out.print("RetornaRegistroHistRestituicaoDecTer - Cod deleção: " + retorno + "\n");

        RegistroHistRestituicaoFerias registroHistRestituicaoFerias = consulta.RetornaRegistroHistRestituicaoFerias(2);

        System.out.print("\nRetornaRegistroHistRestituicaoFerias: \n");
        System.out.println(registroHistRestituicaoFerias.getpCod());
        System.out.println(registroHistRestituicaoFerias.getpCodRestituicaoFerias());
        System.out.println(registroHistRestituicaoFerias.getpCodTipoRestituicao());
        System.out.println(registroHistRestituicaoFerias.getpDataInicioPeriodoAquisitivo());
        System.out.println(registroHistRestituicaoFerias.getpDataFimPeriodoAquisitivo());
        System.out.println(registroHistRestituicaoFerias.getpDataInicioUsufruto());
        System.out.println(registroHistRestituicaoFerias.getpDataFimUsufruto());
        System.out.println(registroHistRestituicaoFerias.getpDiasVendidos());
        System.out.println(registroHistRestituicaoFerias.getpValorFerias());
        System.out.println(registroHistRestituicaoFerias.getpValorTercoConstitucional());
        System.out.println(registroHistRestituicaoFerias.getpIncidSubmod41Ferias());
        System.out.println(registroHistRestituicaoFerias.getpIncidSubmod41Terco());
        System.out.println(registroHistRestituicaoFerias.getpParcela());
        System.out.println(registroHistRestituicaoFerias.getpDataReferencia());
        System.out.println(registroHistRestituicaoFerias.getpAutorizado());
        System.out.println(registroHistRestituicaoFerias.getpRestituido());
        System.out.println(registroHistRestituicaoFerias.getpObservacao());
        System.out.println(registroHistRestituicaoFerias.getpLoginAtualizacao());
        System.out.println(registroHistRestituicaoFerias.getpDataAtualizacao());

        retorno = delete.DeleteRegistro(2, "tb_hist_restituicao_ferias");

        System.out.print("RetornaRegistroHistRestituicaoFerias - Cod deleção: " + retorno + "\n");

        RegistroHistRestituicaoRescisao registroHistRestituicaoRescisao = consulta.RetornaRegistroHistRestituicaoRescisao(2);

        System.out.print("\nRetornaRegistroHistRestituicaoRescisao: \n");
        System.out.println(registroHistRestituicaoRescisao.getpCod());
        System.out.println(registroHistRestituicaoRescisao.getpCodRestituicaoRescisao());
        System.out.println(registroHistRestituicaoRescisao.getpCodTipoRestituicao());
        System.out.println(registroHistRestituicaoRescisao.getpCodTipoRescisao());
        System.out.println(registroHistRestituicaoRescisao.getpDataDesligamento());
        System.out.println(registroHistRestituicaoRescisao.getpDataInicioFerias());
        System.out.println(registroHistRestituicaoRescisao.getpValorDecimoTerceiro());
        System.out.println(registroHistRestituicaoRescisao.getpIncidSubmod41DecTerceiro());
        System.out.println(registroHistRestituicaoRescisao.getpIncidMultaFgtsDecTerceiro());
        System.out.println(registroHistRestituicaoRescisao.getpValorFerias());
        System.out.println(registroHistRestituicaoRescisao.getpValorTerco());
        System.out.println(registroHistRestituicaoRescisao.getpIncidSubmod41Ferias());
        System.out.println(registroHistRestituicaoRescisao.getpIncidSubmod41Terco());
        System.out.println(registroHistRestituicaoRescisao.getpIncidMultaFgtsFerias());
        System.out.println(registroHistRestituicaoRescisao.getpIncidMultaFgtsTerco());
        System.out.println(registroHistRestituicaoRescisao.getpMultaFgtsSalario());
        System.out.println(registroHistRestituicaoRescisao.getpDataReferencia());
        System.out.println(registroHistRestituicaoRescisao.getpAutorizado());
        System.out.println(registroHistRestituicaoRescisao.getpRestituido());
        System.out.println(registroHistRestituicaoRescisao.getpObservacao());
        System.out.println(registroHistRestituicaoRescisao.getpLoginAtualizacao());
        System.out.println(registroHistRestituicaoRescisao.getpDataAtualizacao());

        retorno = delete.DeleteRegistro(2, "tb_hist_restituicao_rescisao");

        System.out.print("RetornaRegistroHistRestituicaoRescisao - Cod deleção: " + retorno + "\n");



        RegistroRetroPercentualEstatico registroRetroPercentualEstatico = consulta.RetornaRegistroRetroPercentualEstatico(null);

        System.out.print("\nRetornaRegistroRetroPercentualEstatico: \n");
        System.out.println(registroRetroPercentualEstatico.getpCod());
        System.out.println(registroRetroPercentualEstatico.getpCodContrato());
        System.out.println(registroRetroPercentualEstatico.getpCodPercentualEstatico());
        System.out.println(registroRetroPercentualEstatico.getpDataInicio());
        System.out.println(registroRetroPercentualEstatico.getpDataFim());
        System.out.println(registroRetroPercentualEstatico.getpDataCobranca());
        System.out.println(registroRetroPercentualEstatico.getpLoginAtualizacao());
        System.out.println(registroRetroPercentualEstatico.getpDataAtualizacao());


        RegistroRestituicaoDecimoTerceiro registroRestituicaoDecimoTerceiro = consulta.RetornaRegistroRestituicaoDecimoTerceiro(2);

        System.out.print("\nRetornaRegistroRestituicaoDecimoTerceiro: \n");
        System.out.println(registroRestituicaoDecimoTerceiro.getpCod());
        System.out.println(registroRestituicaoDecimoTerceiro.getpCodTerceirizadoContrato());
        System.out.println(registroRestituicaoDecimoTerceiro.getpCodTipoRestituicao());
        System.out.println(registroRestituicaoDecimoTerceiro.getpParcela());
        System.out.println(registroRestituicaoDecimoTerceiro.getpDataInicioContagem());
        System.out.println(registroRestituicaoDecimoTerceiro.getpValor());
        System.out.println(registroRestituicaoDecimoTerceiro.getpIncidenciaSubmodulo41());
        System.out.println(registroRestituicaoDecimoTerceiro.getpDataReferencia());
        System.out.println(registroRestituicaoDecimoTerceiro.getpAutorizado());
        System.out.println(registroRestituicaoDecimoTerceiro.getpRestituido());
        System.out.println(registroRestituicaoDecimoTerceiro.getpObservacao());
        System.out.println(registroRestituicaoDecimoTerceiro.getpLoginAtualizacao());
        System.out.println(registroRestituicaoDecimoTerceiro.getpDataAtualizacao());

        RegistroRestituicaoFerias registroRestituicaoFerias = consulta.RetornaRegistroRestituicaoFerias(2);

        System.out.print("\nRetornaRegistroRestituicaoFerias: \n");
        System.out.println(registroRestituicaoFerias.getpCod());
        System.out.println(registroRestituicaoFerias.getpCodTerceirizadoContrato());
        System.out.println(registroRestituicaoFerias.getpCodTipoRestituicao());
        System.out.println(registroRestituicaoFerias.getpDataInicioPeriodoAquisitivo());
        System.out.println(registroRestituicaoFerias.getpDataFimPeriodoAquisitivo());
        System.out.println(registroRestituicaoFerias.getpDataInicioUsufruto());
        System.out.println(registroRestituicaoFerias.getpDataFimUsufruto());
        System.out.println(registroRestituicaoFerias.getpDiasVendidos());
        System.out.println(registroRestituicaoFerias.getpValorFerias());
        System.out.println(registroRestituicaoFerias.getpValorTercoConstitucional());
        System.out.println(registroRestituicaoFerias.getpIncidenciaSubmod41Ferias());
        System.out.println(registroRestituicaoFerias.getpIncidenciaSubmod41Terco());
        System.out.println(registroRestituicaoFerias.getpParcela());
        System.out.println(registroRestituicaoFerias.getpDataReferencia());
        System.out.println(registroRestituicaoFerias.getpAutorizado());
        System.out.println(registroRestituicaoFerias.getpRestituido());
        System.out.println(registroRestituicaoFerias.getpObservacao());
        System.out.println(registroRestituicaoFerias.getpLoginAtualizacao());
        System.out.println(registroRestituicaoFerias.getpDataAtualizacao());

        RegistroRestituicaoRescisao registroRestituicaoRescisao = consulta.RetornaRegistroRestituicaoRescisao(3);

        System.out.print("\nRetornaRegistroRestituicaoRescisao: \n");
        System.out.println(registroRestituicaoRescisao.getpCod());
        System.out.println(registroRestituicaoRescisao.getpCodTerceirizadoContrato());
        System.out.println(registroRestituicaoRescisao.getpCodTipoRestituicao());
        System.out.println(registroRestituicaoRescisao.getpCodTipoRescisao());
        System.out.println(registroRestituicaoRescisao.getpDataDesligamento());
        System.out.println(registroRestituicaoRescisao.getpDataInicioFerias());
        System.out.println(registroRestituicaoRescisao.getpValorDecimoTerceiro());
        System.out.println(registroRestituicaoRescisao.getpIncidSubmod41DecTerceiro());
        System.out.println(registroRestituicaoRescisao.getpIncidMultaFGTSDecTeceriro());
        System.out.println(registroRestituicaoRescisao.getpValorFerias());
        System.out.println(registroRestituicaoRescisao.getpValorTerco());
        System.out.println(registroRestituicaoRescisao.getpIncidSubmod41Ferias());
        System.out.println(registroRestituicaoRescisao.getpIncidSubmod41Terco());
        System.out.println(registroRestituicaoRescisao.getpIncidMultaFGTSFerias());
        System.out.println(registroRestituicaoRescisao.getpIncidMultaFGTSTerco());
        System.out.println(registroRestituicaoRescisao.getpMultaFGTSSalario());
        System.out.println(registroRestituicaoRescisao.getpDataReferencia());
        System.out.println(registroRestituicaoRescisao.getpAutorizado());
        System.out.println(registroRestituicaoRescisao.getpRestituido());
        System.out.println(registroRestituicaoRescisao.getpObservacao());
        System.out.println(registroRestituicaoRescisao.getpLoginAtualizacao());
        System.out.println(registroRestituicaoRescisao.getpDataAtualizacao());

        RegistroSaldoResidualDecTer registroSaldoResidualDecTer = consulta.RetornaRegistroSaldoResidualDecTer(2);

        System.out.print("\nRetornaRegistroSaldoResidualDecTer: \n");
        System.out.println(registroSaldoResidualDecTer.getpCod());
        System.out.println(registroSaldoResidualDecTer.getpCodRestituicaoDecTerceiro());
        System.out.println(registroSaldoResidualDecTer.getpValor());
        System.out.println(registroSaldoResidualDecTer.getpIncidenciaSubmodulo41());
        System.out.println(registroSaldoResidualDecTer.getpAutorizado());
        System.out.println(registroSaldoResidualDecTer.getpRestituido());
        System.out.println(registroSaldoResidualDecTer.getpLoginAtualizacao());
        System.out.println(registroSaldoResidualDecTer.getpDataAtualizacao());

        RegistroSaldoResidualFerias registroSaldoResidualFerias = consulta.RetornaRegistroSaldoResidualFerias(2);

        System.out.print("\nRetornaRegistroSaldoResidualFerias: \n");
        System.out.println(registroSaldoResidualFerias.getpCod());
        System.out.println(registroSaldoResidualFerias.getpCodRestituicaoFerias());
        System.out.println(registroSaldoResidualFerias.getpValorFerias());
        System.out.println(registroSaldoResidualFerias.getpValorTerco());
        System.out.println(registroSaldoResidualFerias.getpIncidSubmod41Ferias());
        System.out.println(registroSaldoResidualFerias.getpIncidSubmod41Terco());
        System.out.println(registroSaldoResidualFerias.getpAutorizado());
        System.out.println(registroSaldoResidualFerias.getpRestituido());
        System.out.println(registroSaldoResidualFerias.getpLoginAtualizacao());
        System.out.println(registroSaldoResidualFerias.getpDataAtualizacao());

        RegistroSaldoResidualRescisao registroSaldoResidualRescisao = consulta.RetornaRegistroSaldoResidualRescisao(3);

        System.out.print("\nRetornaRegistroSaldoResidualRescisao: \n");
        System.out.println(registroSaldoResidualRescisao.getpCod());
        System.out.println(registroSaldoResidualRescisao.getpCodRestituicaoRescisao());
        System.out.println(registroSaldoResidualRescisao.getpValorDecimoTerceiro());
        System.out.println(registroSaldoResidualRescisao.getpIncidSubmod41DecTerceiro());
        System.out.println(registroSaldoResidualRescisao.getpIncidMultaFgtsDecTerceiro());
        System.out.println(registroSaldoResidualRescisao.getpValorFerias());
        System.out.println(registroSaldoResidualRescisao.getpValorTerco());
        System.out.println(registroSaldoResidualRescisao.getpIncidSubmod41Ferias());
        System.out.println(registroSaldoResidualRescisao.getpIncidSubmod41Terco());
        System.out.println(registroSaldoResidualRescisao.getpIncidMultaFgtsFerias());
        System.out.println(registroSaldoResidualRescisao.getpIncidMultaFgtsTerco());
        System.out.println(registroSaldoResidualRescisao.getpMultaFgtsSalario());
        System.out.println(registroRestituicaoRescisao.getpAutorizado());
        System.out.println(registroRestituicaoRescisao.getpRestituido());
        System.out.println(registroRestituicaoRescisao.getpLoginAtualizacao());
*/
    }

}
