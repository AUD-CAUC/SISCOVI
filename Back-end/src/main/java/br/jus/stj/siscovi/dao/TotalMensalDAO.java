package br.jus.stj.siscovi.dao;

import br.jus.stj.siscovi.helpers.Mes;
import br.jus.stj.siscovi.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TotalMensalDAO {

    private final Connection connection;

    public TotalMensalDAO(Connection connection) {
        this.connection = connection;
    }

    public ArrayList<ListaTotalMensalData> getValoresCalculadosAnteriormente(int codContrato, int codGestor){
        ContratoDAO contratoDAO = new ContratoDAO(connection);
        ArrayList<DatasDeCalculoModel> datasDeCalculo = recuperaAnosDeCalculosAnteriores(codContrato);
        ArrayList<ListaTotalMensalData> lista = new ArrayList<>();
        for(int i = 0; i < datasDeCalculo.size(); i++) {
            ArrayList<TotalMensal> totais = new ArrayList<>();

            String sql = "SELECT  u.nome AS \"Gestor\"," +
                    "c.nome_empresa AS \"Empresa\",\n" +
                    " 'Contrato Nº: ' + c.numero_contrato AS \"Contrato\"," +
                    " f.nome AS \"Função\",\n" +
                    " tmr.data_referencia AS \"Data referência\"," +
                    " ROUND(SUM(tmr.ferias), 2) AS \"Férias retido\"," +
                    " ROUND(SUM(tmr.terco_constitucional), 2) AS \"Terço constitucional retido\"," +
                    " ROUND(SUM(tmr.decimo_terceiro), 2) AS \"Décimo terceiro retido\"," +
                    " ROUND(SUM(tmr.incidencia_submodulo_4_1), 2) AS \"Incidência retido\"," +
                    " ROUND(SUM(tmr.multa_fgts), 2) AS \"MULTA do FGTS retido\"," +
                    " ROUND(SUM(tmr.ferias) + SUM(tmr.terco_constitucional) + SUM(tmr.decimo_terceiro) + SUM(tmr.incidencia_submodulo_4_1) + SUM(tmr.multa_fgts), 2) AS \"Total retido\"," +
                    "COUNT(ft.COD_TERCEIRIZADO_CONTRATO) AS TERCEIRIZADOS" +
                    " FROM tb_funcao_contrato fc" +
                    " JOIN tb_contrato c ON c.cod = fc.cod_contrato" +
                    " JOIN tb_funcao f ON f.cod = fc.cod_funcao" +
                    " JOIN tb_funcao_terceirizado ft ON ft.cod_funcao_contrato = fc.cod" +
                    " JOIN tb_terceirizado_contrato tc ON tc.cod = ft.cod_terceirizado_contrato" +
                    " JOIN tb_total_mensal_a_reter tmr ON tmr.cod_terceirizado_contrato = tc.cod" +
                    " JOIN tb_historico_gestao_contrato hgc ON hgc.cod_contrato = c.cod" +
                    " JOIN tb_usuario u ON u.cod = hgc.cod_usuario" +
                    " WHERE c.cod = ?" +
                    " AND MONTH(tmr.data_referencia) = ?" +
                    " AND YEAR(tmr.data_referencia) = ?" +
                    " AND hgc.cod_usuario = ?" +
                    " AND tmr.cod_funcao_terceirizado = ft.cod" +
                    " AND tmr.retido = 'S'" +
                    " AND tmr.autorizado = 'S'"+
                    " GROUP BY u.nome," +
                    " c.nome_empresa," +
                    " 'Contrato Nº: ' + c.numero_contrato," +
                    " f.nome," +
                    " tmr.data_referencia" +
                    " ORDER BY 1,2,3,4;";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, codContrato);
                preparedStatement.setInt(2, datasDeCalculo.get(i).getMes());
                preparedStatement.setInt(3, datasDeCalculo.get(i).getAno());
                preparedStatement.setInt(4, codGestor);
                try (ResultSet resultSet = preparedStatement.executeQuery()){
                    while(resultSet.next()){
                        TotalMensal totalMensal = new TotalMensal(resultSet.getFloat(6), resultSet.getFloat(7), resultSet.getFloat(8),
                                resultSet.getFloat(9), resultSet.getFloat(10), resultSet.getFloat(11), resultSet.getString("FUNÇÃO"),
                                resultSet.getInt("TERCEIRIZADOS"));
                        totais.add(totalMensal);
                    }
                    if(totais.size() > 0){
                        Date dataReferencia = Date.valueOf(datasDeCalculo.get(i).getAno() + "-" + datasDeCalculo.get(i).getMes() + "-01");
                        ListaTotalMensalData listaTotalMensalData = new ListaTotalMensalData(dataReferencia, totais);
                        lista.add(listaTotalMensalData);
                    }
                }

            } catch (SQLException e) {
                throw new NullPointerException("Erro ao tentar carregar os cálculos de Total Mensal a Reter para o contrato: " + codContrato);
            }
        }
        return lista;
    }

    protected ArrayList<DatasDeCalculoModel> recuperaAnosDeCalculosAnteriores (int codContrato) {
        ArrayList<DatasDeCalculoModel> tuplas = new ArrayList<>();
        String sql = "SELECT DISTINCT((DATA_REFERENCIA)) FROM TB_TOTAL_MENSAL_A_RETER TMR JOIN TB_FUNCAO_TERCEIRIZADO FT ON FT.COD=TMR.COD_FUNCAO_TERCEIRIZADO" +
                " JOIN TB_FUNCAO_CONTRATO FC ON FC.cod=FT.COD_FUNCAO_CONTRATO WHERE FC.COD_CONTRATO=? ORDER BY DATA_REFERENCIA ASC";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, codContrato);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while(resultSet.next()){
                    DatasDeCalculoModel datasDeCalculoModel = new DatasDeCalculoModel( resultSet.getDate("DATA_REFERENCIA").toLocalDate().getYear(),
                            resultSet.getDate("DATA_REFERENCIA").toLocalDate().getMonthValue());
                    tuplas.add(datasDeCalculoModel);
                }
            }
        } catch (SQLException e) {
            throw new NullPointerException("Não há cálculos realizados ainda !");
        }
        return tuplas;
    }

    public ArrayList<TotalMensal> getCalculoRealizado(int codUsuario ,int codContrato,int mes, int ano){
        ArrayList<TotalMensal> lista = new ArrayList<>();
        String sql = "SELECT  u.nome AS \"Gestor\"," +
                "c.nome_empresa AS \"Empresa\"," +
                " 'Contrato Nº: ' + c.numero_contrato AS \"Contrato\"," +
                " f.nome AS \"Função\"," +
                " tmr.data_referencia AS \"Data referência\"," +
                " ROUND(SUM(tmr.ferias), 2) AS \"Férias retido\"," +
                " ROUND(SUM(tmr.terco_constitucional), 2) AS \"Terço constitucional retido\"," +
                " ROUND(SUM(tmr.decimo_terceiro), 2) AS \"Décimo terceiro retido\"," +
                " ROUND(SUM(tmr.incidencia_submodulo_4_1), 2) AS \"Incidência retido\"," +
                " ROUND(SUM(tmr.multa_fgts), 2) AS \"MULTA do FGTS retido\"," +
                " ROUND(SUM(tmr.ferias) + SUM(tmr.terco_constitucional) + SUM(tmr.decimo_terceiro) + SUM(tmr.incidencia_submodulo_4_1) + SUM(tmr.multa_fgts), 2) AS \"Total retido\"," +
                " COUNT(ft.COD_TERCEIRIZADO_CONTRATO) AS TERCEIRIZADOS" +
                " FROM tb_funcao_contrato fc" +
                " JOIN tb_contrato c ON c.cod = fc.cod_contrato" +
                " JOIN tb_funcao f ON f.cod = fc.cod_funcao" +
                " JOIN tb_funcao_terceirizado ft ON ft.cod_funcao_contrato = fc.cod" +
                " JOIN tb_terceirizado_contrato tc ON tc.cod = ft.cod_terceirizado_contrato" +
                " JOIN tb_total_mensal_a_reter tmr ON tmr.cod_terceirizado_contrato = tc.cod" +
                " JOIN tb_historico_gestao_contrato hgc ON hgc.cod_contrato = c.cod" +
                " JOIN tb_usuario u ON u.cod = hgc.cod_usuario" +
                " WHERE c.cod = ?" +
                " AND MONTH(tmr.data_referencia) = ?" +
                " AND YEAR(tmr.data_referencia) = ?" +
                " AND hgc.cod_usuario = ?" +
                " AND tmr.cod_funcao_terceirizado = ft.cod" +
                " GROUP BY u.nome," +
                " c.nome_empresa," +
                " 'Contrato Nº: ' + c.numero_contrato," +
                " f.nome," +
                " tmr.data_referencia" +
                " ORDER BY 1,2,3,4;";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, codContrato);
            preparedStatement.setInt(2, mes);
            preparedStatement.setInt(3, ano);
            preparedStatement.setInt(4, codUsuario);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while(resultSet.next()){
                    TotalMensal totalMensal = new TotalMensal(resultSet.getFloat(6), resultSet.getFloat(7), resultSet.getFloat(8), resultSet.getFloat(9),
                            resultSet.getFloat(10), resultSet.getFloat(11), resultSet.getString("Função"), resultSet.getInt("TERCEIRIZADOS"));
                    lista.add(totalMensal);
                }
            }

        } catch (SQLException e) {
            throw new NullPointerException("Erro ao tentar buscar cálculo de Total a mensal a reter com a data de referência: 01/" + mes + "/" + ano + ". Contrato: " + codContrato);
        }
        return lista;
    }

    public ArrayList<TotalMensalPendenteModel> getTotalMensalPendenteExecucao(int codContrato, int codUsuario) {
        int codGestor = new UsuarioDAO(this.connection).verifyPermission(codUsuario, codContrato);
        String sql = "";
        ArrayList<DatasDeCalculoModel> datasDeCalculo = recuperaAnosDeCalculosAnteriores(codContrato);
        ArrayList<TotalMensalPendenteModel> lista = new ArrayList<>();
        for(int i = 0; i < datasDeCalculo.size(); i++) {
            ArrayList<TotalMensal> totais = new ArrayList<>();
            sql = "SELECT u.nome AS \"Gestor\"," +
                    " c.nome_empresa AS \"Empresa\"," +
                    " 'Contrato Nº: ' + c.numero_contrato AS \"Contrato\"," +
                    " f.nome AS \"Função\"," +
                    " tmr.data_referencia AS \"Data referência\"," +
                    " ROUND(SUM(tmr.ferias), 2) AS \"Férias retido\"," +
                    " ROUND(SUM(tmr.terco_constitucional), 2) AS \"Terço constitucional retido\"," +
                    " ROUND(SUM(tmr.decimo_terceiro), 2) AS \"Décimo terceiro retido\"," +
                    " ROUND(SUM(tmr.incidencia_submodulo_4_1), 2) AS \"Incidência retido\"," +
                    " ROUND(SUM(tmr.multa_fgts), 2) AS \"MULTA do FGTS retido\"," +
                    " ROUND(SUM(tmr.ferias) + SUM(tmr.terco_constitucional) + SUM(tmr.decimo_terceiro) + SUM(tmr.incidencia_submodulo_4_1) + SUM(tmr.multa_fgts), 2) AS \"Total retido\"," +
                    " COUNT(ft.COD_TERCEIRIZADO_CONTRATO) AS TERCEIRIZADOS" +
                    " FROM tb_funcao_contrato fc" +
                    " JOIN tb_contrato c ON c.cod = fc.cod_contrato" +
                    " JOIN tb_funcao f ON f.cod = fc.cod_funcao" +
                    " JOIN tb_funcao_terceirizado ft ON ft.cod_funcao_contrato = fc.cod" +
                    " JOIN tb_terceirizado_contrato tc ON tc.cod = ft.cod_terceirizado_contrato" +
                    " JOIN tb_total_mensal_a_reter tmr ON tmr.cod_terceirizado_contrato = tc.cod" +
                    " JOIN tb_historico_gestao_contrato hgc ON hgc.cod_contrato = c.cod" +
                    " JOIN tb_usuario u ON u.cod = hgc.cod_usuario" +
                    " WHERE c.cod = ?" +
                    " AND MONTH(tmr.data_referencia) = ?" +
                    " AND YEAR(tmr.data_referencia) = ?" +
                    " AND hgc.cod_usuario = ?" +
                    " AND tmr.cod_funcao_terceirizado = ft.cod"+
                    " AND (tmr.AUTORIZADO = 'S' OR tmr.AUTORIZADO = 's')" +
                    " AND (tmr.RETIDO is null OR tmr.RETIDO = 'N' OR tmr.RETIDO = 'n')" +
                    " GROUP BY u.nome," +
                    " c.nome_empresa," +
                    " 'Contrato Nº: ' + c.numero_contrato," +
                    " f.nome," +
                    " tmr.data_referencia" +
                    " ORDER BY 1,2,3,4";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, codContrato);
                preparedStatement.setInt(2, datasDeCalculo.get(i).getMes());
                preparedStatement.setInt(3, datasDeCalculo.get(i).getAno());
                preparedStatement.setInt(4, codGestor);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while(resultSet.next()){
                        TotalMensal totalMensal = new TotalMensal(resultSet.getFloat(6), resultSet.getFloat(7), resultSet.getFloat(8),
                                resultSet.getFloat(9), resultSet.getFloat(10), resultSet.getFloat(11), resultSet.getString("FUNÇÃO"),
                                resultSet.getInt("TERCEIRIZADOS"));
                        totais.add(totalMensal);
                    }
                    if(totais.size() > 0) {
                        Date dataReferencia = Date.valueOf(datasDeCalculo.get(i).getAno() + "-" + datasDeCalculo.get(i).getMes() + "-01");
                        ListaTotalMensalData listaTotalMensalData = new ListaTotalMensalData(dataReferencia, totais);
                        TotalMensalPendenteModel totalMensalPendenteModel = new TotalMensalPendenteModel(listaTotalMensalData, "NÃO EXECUTADO");
                        lista.add(totalMensalPendenteModel);
                    }

                }
            } catch (SQLException sqle) {
                System.err.println(sqle.getStackTrace());
                throw new RuntimeException("Houve um erro ao tentar recuperar os cálculos de Total Mensal a Reter !");
            }
        }
        return lista;
    }

    public ArrayList<TotalMensalPendenteModel> getTotalMensalPendente(int codContrato, int codUsuario) {
        int codGestor = new UsuarioDAO(this.connection).verifyPermission(codUsuario, codContrato);
        String sql;
        ArrayList<DatasDeCalculoModel> datasDeCalculo = recuperaAnosDeCalculosAnteriores(codContrato);
        ArrayList<TotalMensalPendenteModel> lista = new ArrayList<>();
        for(int i = 0; i < datasDeCalculo.size(); i++) {
            ArrayList<TotalMensal> totais = new ArrayList<>();
            sql = "SELECT u.nome AS \"Gestor\"," +
                    "c.nome_empresa AS \"Empresa\"," +
                    " 'Contrato Nº: ' + c.numero_contrato AS \"Contrato\"," +
                    " f.nome AS \"Função\"," +
                    " tmr.data_referencia AS \"Data referência\"," +
                    " ROUND(SUM(tmr.ferias), 2) AS \"Férias retido\"," +
                    " ROUND(SUM(tmr.terco_constitucional), 2) AS \"Terço constitucional retido\"," +
                    " ROUND(SUM(tmr.decimo_terceiro), 2) AS \"Décimo terceiro retido\"," +
                    " ROUND(SUM(tmr.incidencia_submodulo_4_1), 2) AS \"Incidência retido\"," +
                    " ROUND(SUM(tmr.multa_fgts), 2) AS \"MULTA do FGTS retido\"," +
                    " ROUND(SUM(tmr.ferias) + SUM(tmr.terco_constitucional) + SUM(tmr.decimo_terceiro) + SUM(tmr.incidencia_submodulo_4_1) + SUM(tmr.multa_fgts), 2) AS \"Total retido\"," +
                    " COUNT(ft.COD_TERCEIRIZADO_CONTRATO) AS TERCEIRIZADOS," +
                    " tmr.AUTORIZADO," +
                    " tmr.OBSERVACAO" +
                    " FROM tb_funcao_contrato fc" +
                    " JOIN tb_contrato c ON c.cod = fc.cod_contrato" +
                    " JOIN tb_funcao f ON f.cod = fc.cod_funcao" +
                    " JOIN tb_funcao_terceirizado ft ON ft.cod_funcao_contrato = fc.cod" +
                    " JOIN tb_terceirizado_contrato tc ON tc.cod = ft.cod_terceirizado_contrato" +
                    " JOIN tb_total_mensal_a_reter tmr ON tmr.cod_terceirizado_contrato = tc.cod" +
                    " JOIN tb_historico_gestao_contrato hgc ON hgc.cod_contrato = c.cod" +
                    " JOIN tb_usuario u ON u.cod = hgc.cod_usuario" +
                    " WHERE c.cod = ?" +
                    " AND MONTH(tmr.data_referencia) = ?" +
                    " AND YEAR(tmr.data_referencia) = ?" +
                    " AND hgc.cod_usuario = ?" +
                    " AND tmr.cod_funcao_terceirizado = ft.cod" +
                    " AND (tmr.AUTORIZADO is null OR tmr.AUTORIZADO = 'N' OR tmr.AUTORIZADO='n')" +
                    " AND (tmr.RETIDO is null  OR tmr.RETIDO = 'N' OR tmr.RETIDO='n')" +
                    " GROUP BY u.nome, tmr.AUTORIZADO," +
                    " c.nome_empresa," +
                    " 'Contrato Nº: ' + c.numero_contrato," +
                    " f.nome," +
                    " tmr.data_referencia," +
                    " tmr.observacao" +
                    " ORDER BY 1,2,3,4";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, codContrato);
                preparedStatement.setInt(2, datasDeCalculo.get(i).getMes());
                preparedStatement.setInt(3, datasDeCalculo.get(i).getAno());
                preparedStatement.setInt(4, codGestor);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    String status = "";
                    String observacoes = "";
                    while(resultSet.next()){
                        TotalMensal totalMensal = new TotalMensal(resultSet.getFloat(6), resultSet.getFloat(7), resultSet.getFloat(8),
                                resultSet.getFloat(9), resultSet.getFloat(10), resultSet.getFloat(11), resultSet.getString("FUNÇÃO"),
                                resultSet.getInt("TERCEIRIZADOS"));
                        totais.add(totalMensal);
                        if(resultSet.getString("AUTORIZADO") == null || resultSet.getString("AUTORIZADO").isEmpty()) {
                            status = "EM ANÁLISE";
                        }else if(resultSet.getString("AUTORIZADO").equals("N") || resultSet.getString("AUTORIZADO").equals("n")) {
                            status = "NEGADO";
                        }
                        observacoes = resultSet.getString("OBSERVACAO");
                    }
                    if(totais.size() > 0) {
                        Date dataReferencia = Date.valueOf(datasDeCalculo.get(i).getAno() + "-" + datasDeCalculo.get(i).getMes() + "-01");
                        ListaTotalMensalData listaTotalMensalData = new ListaTotalMensalData(dataReferencia, totais);
                        TotalMensalPendenteModel totalMensalPendenteModel = new TotalMensalPendenteModel(listaTotalMensalData, status);
                        totalMensalPendenteModel.setObservacoes(observacoes);
                        lista.add(totalMensalPendenteModel);
                    }
                }
            } catch (SQLException sqle) {
                sqle.printStackTrace();
                System.err.println(sqle.getStackTrace());
                throw new RuntimeException("Houve um erro ao tentar recuperar os cálculos de Total Mensal a Reter !");
            }
        }
        return lista;
    }

    public boolean salvaAvaliacaoCalculosPendentes(int codigoContrato, List<TotalMensalPendenteModel> totaisPendentes, User user) {
        if(verificaPermissaoParaOperacao(1, user.getPerfil(), codigoContrato)) {
            String sql = "UPDATE TB_TOTAL_MENSAL_A_RETER SET AUTORIZADO=?, LOGIN_ATUALIZACAO=?, OBSERVACAO=?, DATA_ATUALIZACAO=CURRENT_TIMESTAMP WHERE DATA_REFERENCIA=? AND COD_TERCEIRIZADO_CONTRATO=?";
            List<Integer> listaTerceirizados = retornaCodigoTerceirizados(codigoContrato);
            if(salvaAvaliacaoCalculos(sql, totaisPendentes, listaTerceirizados, user)) {
                return true;
            }
        }
        return false;
    }

    public boolean salvaAvaliacaoCalculosPendentesExecucao(int codigoContrato, List<TotalMensalPendenteModel> totaisPendentes, User user){
        if(verificaPermissaoParaOperacao(1, user.getPerfil(), codigoContrato)) {
            String sql = "UPDATE TB_TOTAL_MENSAL_A_RETER SET RETIDO=?,LOGIN_ATUALIZACAO=?, OBSERVACAO=?, DATA_ATUALIZACAO=CURRENT_TIMESTAMP WHERE DATA_REFERENCIA=? AND COD_TERCEIRIZADO_CONTRATO=?";
            List<Integer> listaTerceirizados = retornaCodigoTerceirizados(codigoContrato);
            if(salvaAvaliacaoCalculos(sql, totaisPendentes, listaTerceirizados, user)){
                return true;
            }
        }
        return false;
    }

    public boolean verificaPermissaoParaOperacao(int operacao, PerfilModel perfil, int codigoContrato) {
        /*
         * operacao = 1 - Servidor da SAD verificando se o Cálculo será autorizado ou não
         * operacao = 2 - Usuário Recalculando cálculos negados
         * operacao = 3 - Servidor da SOF executando ou não cálculo já autorizado pela SAD
         *
         */
        if(operacao == 1) {
            if(perfil.getSigla().equals("ADMINISTRADOR")) {
                return true;
            }else {
                if(perfil.getCod() == 1) {

                }
            }
        }
        return false;
    }

    protected List<Integer> retornaCodigoTerceirizados(int codContrato) {
        List<Integer> lista = new ArrayList<>();
        String sql = "SELECT ft.COD FROM tb_funcao_contrato fc" +
                " JOIN tb_contrato c ON c.cod = fc.cod_contrato" +
                " JOIN tb_funcao f ON f.cod = fc.cod_funcao" +
                " JOIN tb_funcao_terceirizado ft ON ft.cod_funcao_contrato = fc.cod" +
                " JOIN tb_terceirizado_contrato tc ON tc.cod = ft.cod_terceirizado_contrato WHERE C.COD=?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, codContrato);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    lista.add(resultSet.getInt(1));
                }
            }
        }catch(SQLException sqle) {
            System.err.println(sqle.getStackTrace());
            throw new RuntimeException("Erro ao tentar recuperar os terceirizados do contrato " + codContrato + " ao tentar salvar a avaliação dos Cálculos");
        }
        return lista;
    }

    private void SqlTotalaReter(String sql, String status, String userName, String observacoes, Date dataReferencia, Integer codTerceirizadoContrato) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, status);
            preparedStatement.setString(2, userName);
            preparedStatement.setString(3, observacoes);
            preparedStatement.setDate(4, dataReferencia);
            preparedStatement.setInt(5, codTerceirizadoContrato);
            preparedStatement.executeUpdate();
        } catch (SQLException slqe) {
            throw new RuntimeException("Houve um erro ao tentar salvar as alterações de avaliação de Cálculo !");
        }
    }

    private boolean salvaAvaliacaoCalculos(String sql, List<TotalMensalPendenteModel> totaisPendentes, List<Integer> listaTerceirizados, User user) {
        for (TotalMensalPendenteModel totalMensal : totaisPendentes) {

            for(Integer codTerceirizadoContrato : listaTerceirizados) {

                if(totalMensal.getStatus().equals("N")){
                    String nsql = "UPDATE TB_TOTAL_MENSAL_A_RETER SET RETIDO=?, AUTORIZADO = 'N', LOGIN_ATUALIZACAO=?, OBSERVACAO=?, " +
                            "DATA_ATUALIZACAO=CURRENT_TIMESTAMP WHERE DATA_REFERENCIA=? AND COD_TERCEIRIZADO_CONTRATO=?";

                    SqlTotalaReter(nsql, totalMensal.getStatus(), user.getUsername(), totalMensal.getObservacoes(), totalMensal.getTotaisMensais().getDataReferencia(),
                            codTerceirizadoContrato);
                }
                else {
                    SqlTotalaReter(sql, totalMensal.getStatus(), user.getUsername(), totalMensal.getObservacoes(), totalMensal.getTotaisMensais().getDataReferencia(),
                            codTerceirizadoContrato);
                }

            }
        }
        return true;
    }

    public List<Mes> getMesesDeCalculoPermitidosPorAno(int codigoContrato, int ano) {
        List<Mes> meses = new ArrayList<>();
        if(!new ContratoDAO(connection).anoDentroPeriodoVigencia(ano, codigoContrato)) {
            return null;
        }
        String sql = " SELECT 1, 'Janeiro' \n" +
                " UNION ALL SELECT 2, 'Fevereiro'" +
                " UNION ALL SELECT 3, 'Março'" +
                " UNION ALL SELECT 4, 'Abril'" +
                " UNION ALL SELECT 5, 'Maio' " +
                " UNION ALL SELECT 6, 'Junho'" +
                " UNION ALL SELECT 7, 'Julho'" +
                " UNION ALL SELECT 8, 'Agosto'" +
                " UNION ALL SELECT 9, 'Setembro' " +
                " UNION ALL SELECT 10, 'Outubro' " +
                " UNION ALL SELECT 11, 'Novembro' " +
                " UNION ALL SELECT 12, 'Dezembro' " +
                " EXCEPT SELECT month(data_referencia), datename(month, data_referencia) " +
                " FROM tb_total_mensal_a_reter tmr " +
                " JOIN tb_terceirizado_contrato tc on tc.COD=tmr.cod_terceirizado_contrato" +
                " WHERE YEAR(data_referencia) = ?" +
                " AND (autorizado = 'S')" +
                " AND (retido = 'N' OR retido is null)" +
                " AND tc.cod_contrato = ?" +
                " ORDER BY 1 asc";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, ano);
            preparedStatement.setInt(2, codigoContrato);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Mes mes = new Mes(resultSet.getInt(1), resultSet.getString(2));
                    meses.add(mes);
                }
            }
        }catch(SQLException sqle) {
            sqle.printStackTrace();
            throw new RuntimeException("Erro ao tentar recuperar os meses válidos para se realizar cálculos no ano " + ano + " para o contrato " + codigoContrato +
                    ". Causado por: " + sqle.getMessage());
        }
        return meses;
    }

}
