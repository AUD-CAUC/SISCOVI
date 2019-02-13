package br.jus.stj.siscovi.dao;

import br.jus.stj.siscovi.calculos.Ferias;
import br.jus.stj.siscovi.model.CalcularRescisaoModel;
import br.jus.stj.siscovi.model.CalculoPendenteRescisaoModel;
import br.jus.stj.siscovi.model.TerceirizadoRescisao;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.*;
import java.util.List;

public class RescisaoDAO {
    private Connection connection;

    public RescisaoDAO (Connection connection) {
        this.connection = connection;
    }

    /**
     * @param codigoContrato
     * @return
     */
    public ArrayList<TerceirizadoRescisao> getListaTerceirizadoParaCalculoDeRescisao (int codigoContrato) {
        ArrayList<TerceirizadoRescisao> terceirizados = new ArrayList<>();
        Ferias ferias = new Ferias(connection);
        String sql = "SELECT TC.COD," +
                     "       T.NOME," +
                     "       TC.DATA_DESLIGAMENTO" +
                     " FROM tb_terceirizado_contrato TC" +
                     "   JOIN tb_terceirizado T ON T.COD = TC.COD_TERCEIRIZADO" +
                     " WHERE COD_CONTRATO = ?" +
                     "   AND TC.DATA_DESLIGAMENTO IS NOT NULL";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, codigoContrato);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    TerceirizadoRescisao terceirizadoRescisao = new TerceirizadoRescisao(resultSet.getInt("COD"),
                            resultSet.getString("NOME"),
                            resultSet.getDate(3),
                            ferias.RetornaDatasPeriodoFeriasRescisao(resultSet.getInt(1), resultSet.getDate(3), 1),
                            ferias.RetornaDatasPeriodoFeriasRescisao(resultSet.getInt(1), resultSet.getDate(3), 2),
                            ferias.RetornaDatasPeriodoFeriasRescisao(resultSet.getInt(1), resultSet.getDate(3), 3),
                            ferias.RetornaDatasPeriodoFeriasRescisao(resultSet.getInt(1), resultSet.getDate(3), 4),
                            null,
                            null);
                    terceirizados.add(terceirizadoRescisao);
                }
            } catch (SQLException sqle) {
                throw new NullPointerException("Erro ao recuperar funcionários do contrato.");
            }
        } catch (SQLException sqle) {
            throw new NullPointerException("Erro ao buscar funcionários ativos no contrato.");
        }
        return terceirizados;
    }

    public List<CalculoPendenteRescisaoModel> getRestituicoesRescisao(int codigoContrato, int codigoUsuario) {
        int cod = new UsuarioDAO(connection).verifyPermission(codigoUsuario, codigoContrato);
        int codGestor = new ContratoDAO(this.connection).codigoGestorContrato(codigoUsuario, codigoContrato);
        List<CalculoPendenteRescisaoModel> lista = new ArrayList<>();
        if(cod == codGestor) {
            String sql = "SELECT rt.COD,\n" +
                        "        rt.COD_TERCEIRIZADO_CONTRATO, \n" +
                        "        u.nome, \n" +
                        "        c.nome_empresa, \n" +
                        "        c.numero_contrato, \n" +
                        "        tr.nome, \n" +
                        "        trr.TIPO_RESCISAO, \n" +
                        "        t.nome, \n" +
                        "        f.nome, \n" +
                        "        rt.data_desligamento, \n" +
                        "        rt.data_inicio_ferias, \n" +
                        "        rt.data_fim_ferias, \n" +
                        "        rt.data_inicio_ferias_prop, \n" +
                        "        rt.data_fim_ferias_prop, \n" +
                        "        rt.data_inicio_contagem_dec_ter,\n" +
                        "        rt.valor_decimo_terceiro, \n" +
                        "        rt.INCID_SUBMOD_4_1_DEC_TERCEIRO, \n" +
                        "        rt.INCID_MULTA_FGTS_DEC_TERCEIRO, \n" +
                        "        rt.VALOR_FERIAS, \n" +
                        "        rt.VALOR_TERCO, \n" +
                        "        rt.INCID_SUBMOD_4_1_FERIAS, \n" +
                        "        rt.INCID_SUBMOD_4_1_TERCO,\n" +
                        "        rt.INCID_MULTA_FGTS_FERIAS,\n" +
                        "        rt.INCID_MULTA_FGTS_TERCO,\n" +
                        "        rt.VALOR_FERIAS_PROP,\n" +
                        "        rt.VALOR_TERCO_PROP,\n" +
                        "        rt.INCID_SUBMOD_4_1_FERIAS_PROP,\n" +
                        "        rt.INCID_MULTA_FGTS_TERCO_PROP,\n" +
                        "        rt.INCID_MULTA_FGTS_FERIAS_PROP,\n" +
                        "        rt.INCID_MULTA_FGTS_TERCO_PROP,\n" +
                        "        rt.MULTA_FGTS_SALARIO,\n" +
                        "        rt.valor_decimo_terceiro + rt.INCID_SUBMOD_4_1_DEC_TERCEIRO + rt.INCID_MULTA_FGTS_DEC_TERCEIRO + rt.VALOR_FERIAS + rt.VALOR_TERCO + rt.INCID_SUBMOD_4_1_FERIAS + rt.INCID_SUBMOD_4_1_TERCO + rt.INCID_MULTA_FGTS_FERIAS + rt.INCID_MULTA_FGTS_TERCO + rt.VALOR_FERIAS_PROP + rt.VALOR_TERCO_PROP + rt.INCID_SUBMOD_4_1_FERIAS_PROP + rt.INCID_MULTA_FGTS_TERCO_PROP + rt.INCID_MULTA_FGTS_FERIAS_PROP + rt.INCID_MULTA_FGTS_TERCO_PROP + rt.MULTA_FGTS_SALARIO, \n" +
                        "        rt.AUTORIZADO, \n" +
                        "        rt.RESTITUIDO \n" +
                        "   FROM tb_restituicao_rescisao rt \n" +
                        "    JOIN tb_terceirizado_contrato tc ON tc.cod = rt.cod_terceirizado_contrato \n" +
                        "    JOIN tb_funcao_terceirizado ft ON ft.cod_terceirizado_contrato = tc.cod \n" +
                        "    JOIN tb_terceirizado t ON t.cod = tc.cod_terceirizado \n" +
                        "    JOIN tb_contrato c ON c.cod = tc.cod_contrato \n" +
                        "    JOIN tb_tipo_restituicao tr ON tr.cod = rt.cod_tipo_restituicao \n" +
                        "    JOIN tb_tipo_rescisao trr ON trr.cod = rt.cod_tipo_rescisao \n" +
                        "    JOIN tb_historico_gestao_contrato hgc ON hgc.cod_contrato = c.cod \n" +
                        "    JOIN tb_usuario u ON u.cod = hgc.cod_usuario \n" +
                        "    JOIN tb_funcao_contrato fc ON fc.cod = ft.cod_funcao_contrato \n" +
                        "    JOIN tb_funcao f ON f.cod = fc.cod_funcao \n" +
                        "   WHERE tc.COD_CONTRATO = ? AND ((AUTORIZADO ='S' OR AUTORIZADO ='s') AND (RESTITUIDO = 'S' OR RESTITUIDO='s'))";
            try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, codigoContrato);
                try(ResultSet resultSet = preparedStatement.executeQuery()) {
                    while(resultSet.next()) {

                        CalcularRescisaoModel calcularRescisaoModel = new CalcularRescisaoModel(resultSet.getInt(2),
                                resultSet.getString(6),
                                resultSet.getString(7),
                                resultSet.getDate(10),
                                resultSet.getDate(11),
                                resultSet.getDate(12),
                                resultSet.getDate(13),
                                resultSet.getDate(14),
                                resultSet.getDate(15),
                                0,
                                0,
                                0,
                                resultSet.getFloat(16),
                                resultSet.getFloat(17),
                                resultSet.getFloat(18),
                                resultSet.getFloat(19),
                                resultSet.getFloat(20),
                                resultSet.getFloat(21),
                                resultSet.getFloat(22),
                                resultSet.getFloat(23),
                                resultSet.getFloat(24),
                                resultSet.getFloat(25),
                                resultSet.getFloat(26),
                                resultSet.getFloat(27),
                                resultSet.getFloat(28),
                                resultSet.getFloat(29),
                                resultSet.getFloat(30),
                                resultSet.getFloat(31));


                        CalculoPendenteRescisaoModel calculoPendenteModel = new CalculoPendenteRescisaoModel(resultSet.getInt(1),
                                calcularRescisaoModel,
                                resultSet.getString(8),
                                resultSet.getString(9),
                                null,
                                resultSet.getFloat(32));
                        lista.add(calculoPendenteModel);
                    }
                }
            }catch (SQLException slqe) {
                slqe.printStackTrace();
            }
        }
        return lista;
    }
}
