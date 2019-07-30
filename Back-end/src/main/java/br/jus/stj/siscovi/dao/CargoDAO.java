package br.jus.stj.siscovi.dao;

import br.jus.stj.siscovi.model.*;
import com.sun.org.apache.regexp.internal.RE;
import com.sun.scenario.effect.impl.prism.ps.PPSBlend_REDPeer;

import javax.xml.ws.Response;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CargoDAO {
    private Connection connection;
    public CargoDAO(Connection connection) {
        this.connection = connection;
    }

    public ArrayList<CargoModel> getAllCargos() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<CargoModel> cargos = new ArrayList<>();
        try{
            preparedStatement = connection.prepareStatement("SELECT * FROM TB_FUNCAO ORDER BY NOME");
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                CargoModel cargo = new CargoModel(resultSet.getInt("COD"),
                        resultSet.getString("NOME"),
                        resultSet.getString("LOGIN_ATUALIZACAO"),
                        resultSet.getDate("DATA_ATUALIZACAO"));
                if (resultSet.getString("DESCRICAO") ==  null) {
                    cargo.setDescricao("-");
                }else {
                    cargo.setDescricao(resultSet.getString("DESCRICAO"));
                }
                cargos.add(cargo);
            }
            return cargos;
        }catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return null;
    }
    public ArrayList<CargoModel> getCargosDeUmContrato (int codigo) throws RuntimeException{
        ArrayList<CargoModel> cargos = new ArrayList<>();
            String sql = "SELECT CC.COD_FUNCAO,CA.NOME,CA.DESCRICAO,CA.LOGIN_ATUALIZACAO,CA.DATA_ATUALIZACAO" +
                    " FROM tb_funcao_contrato CC" +
                    " JOIN tb_FUNCAO CA ON CA.cod=CC.COD_FUNCAO" +
                    " WHERE CC.COD_CONTRATO=?" +
                    " ORDER BY CA.NOME";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, codigo);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        CargoModel cargo = new CargoModel(resultSet.getInt("COD_FUNCAO"),
                                resultSet.getString("NOME"),
                                resultSet.getString("LOGIN_ATUALIZACAO"),
                                resultSet.getDate("DATA_ATUALIZACAO"));
                        if (resultSet.getString("DESCRICAO") != null) {
                            cargo.setDescricao(resultSet.getString("DESCRICAO"));
                        } else {
                            cargo.setDescricao("-");
                        }
                        cargos.add(cargo);
                    }
                }
                return cargos;
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        return null;
    }
    public boolean cadastroCargos(ArrayList<CargoModel> cargos, String currentUser) {
        PreparedStatement preparedStatement = null;
        int a = 1;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("INSERT INTO TB_FUNCAO (NOME, DESCRICAO, LOGIN_ATUALIZACAO, DATA_ATUALIZACAO) VALUES (?, ?, ?, CURRENT_TIMESTAMP)");
            for (CargoModel cargo : cargos) {
                if ((a % 4) == 0) {
                    a = 1;
                }
                preparedStatement.setString(a++, cargo.getNome());
                preparedStatement.setString(a++, cargo.getDescricao());
                preparedStatement.setString(a++, currentUser);
                preparedStatement.addBatch();
            }
            int [] updateCounts = preparedStatement.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
            return true;
        }catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return false;
    }

    public List<CargoModel> getFuncoesContrato(int codigo, User user) throws RuntimeException {
        List<CargoModel> cargos = new ArrayList<>();
        UsuarioDAO usuarioDAO = new UsuarioDAO(connection);
        int cod = usuarioDAO.verifyPermission(user.getId(), codigo);
        String sql = "SELECT CC.COD_FUNCAO, CA.NOME, CA.DESCRICAO, RFC.REMUNERACAO, RFC.TRIENIOS, RFC.ADICIONAIS, CC.LOGIN_ATUALIZACAO, CC.DATA_ATUALIZACAO, RFC.COD_CONVENCAO_COLETIVA" +
                " FROM tb_funcao_contrato CC" +
                " JOIN tb_FUNCAO CA ON CA.cod=CC.COD_FUNCAO" +
                " JOIN tb_remuneracao_fun_con RFC ON CC.COD = RFC.COD_FUNCAO_CONTRATO" +
                " WHERE CC.COD_CONTRATO=? AND DATA_FIM IS NULL" +
                " ORDER BY CA.NOME;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, codigo);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    CargoModel cargo = new CargoModel(resultSet.getInt("COD_FUNCAO"),
                            resultSet.getString("NOME"),
                            resultSet.getString("LOGIN_ATUALIZACAO"),
                            resultSet.getDate("DATA_ATUALIZACAO"));
                    cargo.setDescricao(resultSet.getString("DESCRICAO"));
                    cargo.setRemuneracao(resultSet.getFloat("REMUNERACAO"));
                    cargo.setTrienios(resultSet.getFloat("TRIENIOS"));
                    cargo.setAdicionais(resultSet.getFloat("ADICIONAIS"));
                    cargo.setConvencao(new ConvencoesDAO(connection).getConvencaoColetiva(resultSet.getInt("COD_CONVENCAO_COLETIVA")));
                    cargos.add(cargo);
                }
            }
            return cargos;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new RuntimeException(sqle.getMessage());
        }
    }

    public List<CargoModel> getFuncoesAjuste(int codigoContrato, int codigoAjuste, User user) throws RuntimeException {
        List<CargoModel> cargos = new ArrayList<>();
        UsuarioDAO usuarioDAO = new UsuarioDAO(connection);
        int cod = usuarioDAO.verifyPermission(user.getId(), codigoContrato);
        String sql = "SELECT EC.cod, CC.COD_FUNCAO, CA.NOME, CA.DESCRICAO, RFC.REMUNERACAO, RFC.TRIENIOS, RFC.ADICIONAIS, CC.LOGIN_ATUALIZACAO, CC.DATA_ATUALIZACAO, RFC.COD_CONVENCAO_COLETIVA, DATA_INICIO, DATA_FIM" +
                " FROM tb_funcao_contrato CC " +
                " JOIN tb_FUNCAO CA ON CA.cod=CC.COD_FUNCAO " +
                " JOIN tb_remuneracao_fun_con RFC ON CC.COD = RFC.COD_FUNCAO_CONTRATO" +
                "  JOIN tb_evento_contratual EC ON EC.COD_CONTRATO = CC.COD_CONTRATO" +
                "  JOIN TB_TIPO_EVENTO_CONTRATUAL TEC ON EC.COD_TIPO_EVENTO=TEC.COD" +
                " WHERE TEC.TIPO != 'CONTRATO' AND CC.COD_CONTRATO=? AND EC.cod=?" +
                "       AND ((EC.DATA_INICIO_VIGENCIA BETWEEN RFC.DATA_INICIO AND (CASE WHEN RFC.DATA_FIM IS NULL THEN (SELECT MAX(DATA_FIM_VIGENCIA) FROM tb_evento_contratual WHERE COD_CONTRATO = ?) ELSE DATA_FIM END))" +
                "       OR EC.DATA_INICIO_VIGENCIA = RFC.DATA_INICIO)" +
                " ORDER BY CA.NOME;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, codigoContrato);
            preparedStatement.setInt(2, codigoAjuste);
            preparedStatement.setInt(3, codigoContrato);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    CargoModel cargo = new CargoModel(resultSet.getInt("COD_FUNCAO"),
                            resultSet.getString("NOME"),
                            resultSet.getString("LOGIN_ATUALIZACAO"),
                            resultSet.getDate("DATA_ATUALIZACAO"));
                    cargo.setDescricao(resultSet.getString("DESCRICAO"));
                    cargo.setRemuneracao(resultSet.getFloat("REMUNERACAO"));
                    cargo.setTrienios(resultSet.getFloat("TRIENIOS"));
                    cargo.setAdicionais(resultSet.getFloat("ADICIONAIS"));
                    cargo.setConvencao(new ConvencoesDAO(connection).getConvencaoColetiva(resultSet.getInt("COD_CONVENCAO_COLETIVA")));
                    cargos.add(cargo);
                }
            }
            return cargos;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new RuntimeException(sqle.getMessage());
        }
    }

    public List<CargosFuncionariosModel> getTerceirizadosFuncao(int codigo, User user) {
        int codigoGestor = new UsuarioDAO(this.connection).verifyPermission(user.getId(), codigo);
        int codGestor = new ContratoDAO(this.connection).codigoGestorContrato(user.getId(), codigo);
        List<CargosFuncionariosModel> lista = new ArrayList<>();
        if(codigoGestor == codGestor) {
            String sql = "SELECT T.cod, T.NOME, T.CPF ,T.ATIVO, F.COD as CODIGO,F.NOME AS CARGO, TC.DATA_DISPONIBILIZACAO, TC.DATA_DESLIGAMENTO, T.LOGIN_ATUALIZACAO, T.DATA_ATUALIZACAO," +
                    " F.DATA_ATUALIZACAO AS DATAATUALIZACAO, F.LOGIN_ATUALIZACAO AS LOGINATUALIZACAO" +
                    " FROM TB_TERCEIRIZADO_CONTRATO TC JOIN TB_TERCEIRIZADO T ON T.COD=TC.COD_TERCEIRIZADO JOIN TB_FUNCAO_TERCEIRIZADO FT ON FT.COD_TERCEIRIZADO_CONTRATO=TC.COD" +
                    " JOIN TB_FUNCAO_CONTRATO FC ON FC.COD=FT.COD_FUNCAO_CONTRATO JOIN TB_FUNCAO F ON F.COD=FC.COD_FUNCAO WHERE TC.COD_CONTRATO=? AND FT.DATA_FIM IS NULL ORDER BY T.NOME ASC";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, codigo);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while(resultSet.next()) {
                        FuncionarioModel terceirizado = new FuncionarioModel(resultSet.getInt("COD"), resultSet.getString("NOME"), resultSet.getString("CPF"),
                                resultSet.getString("ATIVO").charAt(0));
                        CargoModel cargoModel = new CargoModel(resultSet.getInt("CODIGO"), resultSet.getString("CARGO"),
                                resultSet.getString("LOGINATUALIZACAO"), resultSet.getDate("DATAATUALIZACAO"));
                        CargosFuncionariosModel cfm = new CargosFuncionariosModel();
                        cfm.setFuncionario(terceirizado);
                        cfm.setFuncao(cargoModel);
                        cfm.setDataDisponibilizacao(resultSet.getDate("DATA_DISPONIBILIZACAO"));
                        cfm.setDataDesligamento(resultSet.getDate("DATA_DESLIGAMENTO"));
                        lista.add(cfm);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return lista;
    }
    public int recuperaCodigoFuncaoContrato(int codigoContrato, int codFuncao) {
        String sql = "SELECT COD FROM tb_funcao_contrato WHERE COD_FUNCAO=? AND COD_CONTRATO=?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, codFuncao);
            preparedStatement.setInt(2, codigoContrato);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()) {
                    return resultSet.getInt("COD");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        return 0;
    }

    public boolean desligaTerceirizado(int codContrato, int codTerceirizado, int codFuncao, Date dataDesligamento, String username) {
        String sql = "";
        int codTerceirizadoContrato = 0;
        int codUltimaFuncaoTerceirizado = 0;
        sql = "SELECT COD FROM TB_TERCEIRIZADO_CONTRATO WHERE COD_CONTRATO=? AND COD_TERCEIRIZADO=? AND DATA_DESLIGAMENTO IS NULL"; // Busca o código do terceirizado em um contrato
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, codContrato);
            preparedStatement.setInt(2, codTerceirizado);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    codTerceirizadoContrato = resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (codTerceirizadoContrato != 0) {
            // codFuncaoContrato = recuperaCodigoFuncaoContrato(codContrato, codFuncaoAnterior);
            sql = "SELECT COD FROM TB_FUNCAO_TERCEIRIZADO WHERE cod_terceirizado_contrato = ?" +
                    " AND data_fim IS NULL";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, codTerceirizadoContrato);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        codUltimaFuncaoTerceirizado = resultSet.getInt(1);
                    }
                }
            } catch (SQLException e) {
                System.out.println(e.getStackTrace());
            }
            if (codUltimaFuncaoTerceirizado != 0) {
                // Atualiza a função atual do terceirizado no contrato alterando a data fim para um dia antes da data de início na nova função
                sql = "UPDATE tb_funcao_terceirizado SET LOGIN_ATUALIZACAO=?, DATA_FIM=? WHERE COD=?";

                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setDate(2, dataDesligamento);
                    preparedStatement.setInt(3, codUltimaFuncaoTerceirizado);
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    try {
                        connection.rollback();
                    } catch (SQLException e1) {

                        throw new RuntimeException("Falha ao tentar se recuperar de um erro");
                    }
                    throw new RuntimeException("Erro ao tentar alterar a função de um terceirizado.");
                }

                sql = "UPDATE tb_terceirizado_contrato SET LOGIN_ATUALIZACAO=?, DATA_DESLIGAMENTO=?, DATA_ATUALIZACAO=CURRENT_TIMESTAMP where COD=?";

                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setDate(2, dataDesligamento);
                    preparedStatement.setInt(3, codTerceirizadoContrato);
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    try {
                        connection.rollback();
                    } catch (SQLException e1) {

                        throw new RuntimeException("Falha ao tentar se recuperar de um erro");
                    }
                    throw new RuntimeException("Erro ao tentar alterar a função de um terceirizado.");
                }
                return true;
            }
        }
        return false;
    }

    public boolean alterarFuncaoTerceirizado(int codContrato, int codTerceirizado, int codFuncao, Date dataInicio, String username) {
        String sql = "";
        int codTerceirizaContrato = 0;
        int codFuncaoTerceirizadoAnterior = 0;
        sql = "SELECT COD FROM TB_TERCEIRIZADO_CONTRATO WHERE COD_CONTRATO=? AND COD_TERCEIRIZADO=? AND DATA_DESLIGAMENTO IS NULL"; // Busca o código do terceirizado em um contrato
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, codContrato);
            preparedStatement.setInt(2, codTerceirizado);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    codTerceirizaContrato = resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (codTerceirizaContrato != 0) {
            // codFuncaoContrato = recuperaCodigoFuncaoContrato(codContrato, codFuncaoAnterior);
            sql = "SELECT COD FROM TB_FUNCAO_TERCEIRIZADO WHERE cod_terceirizado_contrato = ?" +
                    " AND data_fim IS NULL";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, codTerceirizaContrato);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        codFuncaoTerceirizadoAnterior = resultSet.getInt(1);
                    }
                }
            } catch (SQLException e) {
                System.out.println(e.getStackTrace());
            }
            if (codFuncaoTerceirizadoAnterior != 0) {

                Date dataFim = Date.valueOf(dataInicio.toLocalDate().minusDays(1));

                // Atualiza a função atual do terceirizado no contrato alterando a data fim para um dia antes da data de início na nova função
                sql = "UPDATE tb_funcao_terceirizado SET LOGIN_ATUALIZACAO=?, DATA_FIM=? WHERE COD=?";

                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setDate(2, dataFim);
                    preparedStatement.setInt(3, codFuncaoTerceirizadoAnterior);
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    try {
                        connection.rollback();
                    } catch (SQLException e1) {

                        throw new RuntimeException("Falha ao tentar se recuperar de um erro");
                    }
                    throw new RuntimeException("Erro ao tentar alterar a função de um terceirizado.");
                }
                int codFuncaoContrato = recuperaCodigoFuncaoContrato(codContrato, codFuncao);
                if (codFuncaoContrato != 0) {
                    sql = "INSERT INTO tb_funcao_terceirizado (COD_TERCEIRIZADO_CONTRATO, COD_FUNCAO_CONTRATO, DATA_INICIO, LOGIN_ATUALIZACAO, DATA_ATUALIZACAO) VALUES" +
                            " (?, ?, ?, ?, CURRENT_TIMESTAMP)"; // insere novo registro de função na tabela "função_terceirizado"
                    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                        preparedStatement.setInt(1, codTerceirizaContrato);
                        preparedStatement.setInt(2, codFuncaoContrato);
                        preparedStatement.setDate(3, dataInicio);
                        preparedStatement.setString(4, username);
                        preparedStatement.executeUpdate();
                    } catch (SQLException e) {
                        try {
                            connection.rollback();
                        } catch (SQLException e1) {
                            throw new RuntimeException("Falha ao tentar se recuperar de um erro");
                        }
                        throw new RuntimeException("Erro ao tentar inserir nova função para um terceirizado !");
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
