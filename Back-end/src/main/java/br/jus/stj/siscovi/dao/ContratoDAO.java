package br.jus.stj.siscovi.dao;

import br.jus.stj.siscovi.dao.sql.ConsultaTSQL;
import br.jus.stj.siscovi.dao.sql.InsertTSQL;
import br.jus.stj.siscovi.dao.sql.UpdateTSQL;
import br.jus.stj.siscovi.model.*;
import com.sun.org.apache.regexp.internal.RESyntaxException;
import com.sun.scenario.effect.impl.prism.ps.PPSBlend_REDPeer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContratoDAO {
    private Connection connection;

    public ContratoDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * @param username
     * @return
     * @throws NullPointerException
     * @throws SQLException
     */
    public ArrayList<ContratoModel> retornaContratoDoUsuario(String username) throws NullPointerException, SQLException {
        ArrayList<ContratoModel> contratos = new ArrayList<ContratoModel>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT SIGLA FROM TB_PERFIL_USUARIO P JOIN tb_usuario U ON U.COD_PERFIL=P.cod WHERE U.LOGIN=?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            if (resultSet.getString("SIGLA").equals("ADMINISTRADOR")) {
                preparedStatement = connection.prepareStatement("SELECT DISTINCT C.COD, NOME_EMPRESA, CNPJ, NUMERO_CONTRATO, SE_ATIVO, " +
                        " EC.DATA_INICIO_VIGENCIA as DATA_INICIO, EC.DATA_FIM_VIGENCIA AS DATA_FIM, OBJETO" +
                        " FROM TB_CONTRATO C" +
                        " JOIN tb_evento_contratual EC ON EC.COD_CONTRATO=C.COD\n" +
                        " JOIN TB_TIPO_EVENTO_CONTRATUAL TEC ON TEC.COD=EC.COD_TIPO_EVENTO\n" +
                        " WHERE TEC.TIPO='CONTRATO';");
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    ContratoModel contrato = new ContratoModel(resultSet.getInt("COD"), resultSet.getString("NOME_EMPRESA"), resultSet.getString("CNPJ"));
                    contrato.setNumeroDoContrato(resultSet.getString("NUMERO_CONTRATO"));
                    contrato.setAnoDoContrato(resultSet.getDate("DATA_INICIO").toLocalDate().getYear()); // RECUPERA O ANO DA DATA INÍCIO DO CONTRATO
                    contrato.setDataInicio(resultSet.getDate("DATA_INICIO"));
                    contrato.setNomeDaEmpresa(contrato.getNomeDaEmpresa());
                    if (resultSet.getString("SE_ATIVO").equals("S")) {
                        contrato.setSeAtivo("Sim");
                    } else {
                        contrato.setSeAtivo("Não");
                    }
                    if (resultSet.getDate("DATA_FIM") != null) {
                        contrato.setDataFim(resultSet.getDate("DATA_FIM"));
                    } else {
                        contrato.setDataFim(null);
                    }
                    if (resultSet.getString("OBJETO") == null) {
                        contrato.setObjeto("-");
                    } else {
                        contrato.setObjeto(resultSet.getString("OBJETO"));
                    }
                    contratos.add(contrato);
                }
            } else {
                preparedStatement = connection.prepareStatement("SELECT DISTINCT C.COD , NOME_EMPRESA,CNPJ, NUMERO_CONTRATO,hgc.data_inicio, hgc.data_fim, SE_ATIVO, OBJETO  FROM TB_CONTRATO C" +
                        " JOIN tb_historico_gestao_contrato hgc ON hgc.cod_contrato = c.cod" +
                        " JOIN tb_usuario u ON u.cod = hgc.cod_usuario" +
                        " JOIN tb_perfil_usuario p ON p.cod = u.cod_perfil" +
                        " WHERE u.login = ?");
                preparedStatement.setString(1, username);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    ContratoModel contrato = new ContratoModel(resultSet.getInt("COD"), resultSet.getString("NOME_EMPRESA"), resultSet.getString("CNPJ"));
                    contrato.setNumeroDoContrato(resultSet.getString("NUMERO_CONTRATO"));
                    contrato.setAnoDoContrato(resultSet.getDate("DATA_INICIO").toLocalDate().getYear()); // RECUPERA O ANO DA DATA INÍCIO DO CONTRATO
                    contrato.setDataInicio(resultSet.getDate("DATA_INICIO"));
                    contrato.setSeAtivo(resultSet.getString("SE_ATIVO"));
                    if (resultSet.getDate("DATA_FIM") != null) {
                        contrato.setDataFim(resultSet.getDate("DATA_FIM"));
                    } else {
                        contrato.setDataFim(null);
                    }
                    if (resultSet.getString("OBJETO") == null) {
                        contrato.setObjeto("-");
                    } else {
                        contrato.setObjeto(resultSet.getString("OBJETO"));
                    }
                    contratos.add(contrato);
                }
            }
            return contratos;
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return null;
    }

    /**
     * @param codigo
     * @return
     */
    public String retornaNomeDoGestorDoContrato(int codigo) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            preparedStatement = connection.prepareStatement("SELECT U.NOME FROM TB_USUARIO U JOIN tb_historico_gestao_contrato HGC ON HGC.COD_USUARIO=U.cod " +
                    "JOIN TB_CONTRATO C ON  C.cod=HGC.COD_CONTRATO WHERE C.COD = ?");
            preparedStatement.setInt(1, codigo);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("NOME");
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return null;
    }

    /**
     * @param codigoUsuario
     * @param codigoContrato
     * @return
     */
    public int codigoGestorContrato(int codigoUsuario, int codigoContrato) {
        int codigoGestor = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT PU.SIGLA AS \"USUARIO\", COD_USUARIO FROM TB_USUARIO U" +
                " JOIN TB_PERFIL_USUARIO PU ON PU.COD=U.COD_PERFIL" +
                " JOIN tb_historico_gestao_contrato HGC ON HGC.COD_CONTRATO=?" +
                " JOIN TB_PERFIL_GESTAO PG ON PG.COD=HGC.COD_PERFIL_GESTAO WHERE U.COD=?")) {
            preparedStatement.setInt(1, codigoContrato);
            preparedStatement.setInt(2, codigoUsuario);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    if (resultSet.getInt("COD_USUARIO") == codigoUsuario) {
                        codigoGestor = codigoUsuario;
                    } else if (resultSet.getString("USUARIO").equals("USUÁRIO") || resultSet.getString(1).equals("GESTOR") || resultSet.getString(1).equals("1° SUBSTITUTO") ||
                            resultSet.getString(1).equals("2° SUBSTITUTO")) {
                        codigoGestor = resultSet.getInt("COD_USUARIO");
                    }
                    if (resultSet.getString(1).equals("ADMINISTRADOR")) {
                        codigoGestor = resultSet.getInt("COD_USUARIO");
                    }
                }
            }
        } catch (SQLException e) {
            throw new NullPointerException("Erro ao tentar recuperar cálculos anteriores. Erro na função: 'codigoGestorContrato em ContratoDao.class'");
        }
        return codigoGestor;
    }

    /**
     * Função que insere um contrato no sistema, isto é, insere o nome da empresa, o CNPJ, o número do contrato no STJ,
     * o número do processo no STJ, a descrição do objeto deste contrato, os gestores (O gestor em si, e até dois
     * substitutos), os percentuais, as funções com suas respectivas remunerações e convenções coletivas e a vigência do
     * contrato
     *
     * @param contrato
     * @param username
     * @return
     * @throws RuntimeException
     * @throws SQLException
     */
    public boolean cadastrarContrato(ContratoModel contrato, String username) throws RuntimeException, SQLException {
        this.connection.setAutoCommit(false);
        Savepoint savepoint = this.connection.setSavepoint("Savepoint1");
        InsertTSQL insertTSQL = new InsertTSQL(connection);
        ConsultaTSQL consultaTSQL = new ConsultaTSQL(connection);
        int vCodContrato = 0;
        int vCodUsuarioGestor = 0;
        float vPercentualTercoConstitucional = 0;
        Date vDataInicioPercentualTercoConstitucional = null;
        Date vDataFimPercentualTercoConstitucional = null;
        Date vDataAditamentoPercentualTercoConstitucional = null;
        try {
            vCodContrato = insertTSQL.InsertContrato(contrato.getNomeDaEmpresa(), contrato.getCnpj(), contrato.getNumeroDoContrato(), contrato.getNumeroProcessoSTJ(), contrato.getSeAtivo(),
                    contrato.getObjeto(), username);
            if (vCodContrato != 0) {
                for (HistoricoGestorModel hgc : contrato.getHistoricoGestao()) {
                    String sql = "SELECT COD FROM TB_USUARIO WHERE NOME=?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                        String nomeGestor = "";
                        preparedStatement.setString(1, hgc.getGestor());
                        try (ResultSet resultSet = preparedStatement.executeQuery()) {
                            if (resultSet.next()) {
                                vCodUsuarioGestor = resultSet.getInt("COD");
                            }
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException("Erro. Usuário indicado para gestor do contrato não existe no sistema !");
                    }
                    insertTSQL.InsertHistoricoGestaoContrato(vCodContrato, vCodUsuarioGestor, hgc.getCodigoPerfilGestao(), hgc.getInicio(), null, username);
                }
                for (PercentualModel pm : contrato.getPercentuais()) {
                    insertTSQL.InsertPercentualContrato(vCodContrato, pm.getRubrica().getCodigo(), pm.getPercentual(), pm.getDataInicio(), null, pm.getDataAditamento(), username);
                    if (pm.getRubrica().getNome().contains("Férias")) {
                        vPercentualTercoConstitucional = pm.getPercentual() / 3;
                        vDataInicioPercentualTercoConstitucional = pm.getDataInicio();
                        vDataFimPercentualTercoConstitucional = pm.getDataFim();
                        vDataAditamentoPercentualTercoConstitucional = pm.getDataAditamento();
                    }
                }
                // Inserir Percentual de Terço Constitucional de Férias no cadastro do contrato já que o objeto não é criado no Front e por tanto não é enviado na Requisição
                insertTSQL.InsertPercentualContrato(vCodContrato, 2, vPercentualTercoConstitucional, vDataInicioPercentualTercoConstitucional,
                        vDataFimPercentualTercoConstitucional, vDataAditamentoPercentualTercoConstitucional, username);
                for (CargoModel cm : contrato.getFuncoes()) {
                    int vCodFuncaoContrato = insertTSQL.InsertFuncaoContrato(vCodContrato, cm.getCodigo(), cm.getDescricao(), username);
                    insertTSQL.InsertRemuneracaoFunCon(vCodFuncaoContrato,
                            cm.getConvencao().getCodigo(),
                            contrato.getDataInicio(),
                            null,
                            contrato.getDataInicio(),
                            cm.getRemuneracao(),
                            cm.getAdicionais(),
                            cm.getTrienios(),
                            username);
                }
                if (vCodContrato != 0) {
                    insertTSQL.InsertEventoContratual(vCodContrato,
                            retornaCodEventoContratual("CONTRATO"),
                            "N",
                            null,
                            contrato.getDataInicio(),
                            contrato.getDataFim(),
                            contrato.getDataAssinatura(),
                            username);
                }
            }
            connection.commit();
            connection.setAutoCommit(true);
            return true;
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            connection.rollback(savepoint);
            throw new RuntimeException("" + npe.getMessage());
        }
    }

    /**
     * Retorna o código de um evento contratual pelo tipo de evento.
     *
     * @param tipoEventoContratual
     * @return
     * @throws RuntimeException
     */
    public int retornaCodEventoContratual(String tipoEventoContratual) {
        String sql = "SELECT COD FROM TB_TIPO_EVENTO_CONTRATUAL WHERE TIPO = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, tipoEventoContratual);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("COD");
                }
            }
        } catch (SQLException sqle) {
            throw new RuntimeException("Tipo de evento contratual não encontrado. " + sqle.getMessage());
        }
        return 0;
    }

    /**
     * @param username
     * @param codigoContrato
     * @return
     * @throws RuntimeException
     */
    public List<EventoContratualModel> retornaEventosContratuais(String username, int codigoContrato) throws RuntimeException {
        int vCodUsuario = 0;
        List<EventoContratualModel> lista = new ArrayList<>();
        String sql = "SELECT COD FROM TB_USUARIO WHERE LOGIN=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    vCodUsuario = resultSet.getInt("COD");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Usuário não encontrado.");
        }
        int codigo = new UsuarioDAO(connection).verifyPermission(vCodUsuario, codigoContrato);
        int codGestor = new ContratoDAO(connection).codigoGestorContrato(vCodUsuario, codigoContrato);
        if (codigo == codGestor) {
            sql = "SELECT EC.COD, EC.PRORROGACAO, EC.ASSUNTO, EC.DATA_INICIO_VIGENCIA, EC.DATA_FIM_VIGENCIA, EC.DATA_ASSINATURA, EC.LOGIN_ATUALIZACAO, EC.DATA_ATUALIZACAO," +
                    " TEC.TIPO, TEC.COD AS 'CODIGO', TEC.DATA_ATUALIZACAO AS 'DA', TEC.LOGIN_ATUALIZACAO AS 'LA'" +
                    " FROM TB_EVENTO_CONTRATUAL EC" +
                    " JOIN TB_TIPO_EVENTO_CONTRATUAL TEC ON EC.COD_TIPO_EVENTO=TEC.COD WHERE TEC.TIPO != 'CONTRATO' AND COD_CONTRATO = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, codigoContrato);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        TipoEventoContratualModel tipoEventoContratualModel = new TipoEventoContratualModel(resultSet.getInt("CODIGO"),
                                resultSet.getString("TIPO"), resultSet.getString("LA"), resultSet.getDate("DA"));
                        EventoContratualModel eventoContratualModel = new EventoContratualModel(resultSet.getInt("COD"),
                                tipoEventoContratualModel,
                                resultSet.getString("PRORROGACAO").charAt(0),
                                resultSet.getString("ASSUNTO"),
                                resultSet.getDate("DATA_INICIO_VIGENCIA"),
                                resultSet.getDate("DATA_FIM_VIGENCIA"),
                                resultSet.getDate("DATA_ASSINATURA"),
                                resultSet.getString("LOGIN_ATUALIZACAO"),
                                resultSet.getDate("DATA_ATUALIZACAO"));
                        lista.add(eventoContratualModel);

                    }
                }
                return lista;
            } catch (SQLException sqle) {
                throw new RuntimeException("");
            }
        }
        return null;
    }

    /**
     * Retorna todas as informações atuais do contrato.
     *
     * @param username
     * @param codContrato
     * @return
     * @throws RuntimeException
     */
    public ContratoModel getContratoCompleto(String username, int codContrato) throws RuntimeException {
        String sql = "SELECT COD FROM TB_USUARIO WHERE LOGIN=?";
        User user = new User();
        ContratoModel contrato = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    user.setId(resultSet.getInt("COD"));
                    user.setUsername(username);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("" + e.getMessage());
        }
        sql = "SELECT COD, NOME_EMPRESA, CNPJ, NUMERO_CONTRATO, NUMERO_PROCESSO_STJ, SE_ATIVO, OBJETO, LOGIN_ATUALIZACAO, DATA_ATUALIZACAO FROM TB_CONTRATO WHERE COD=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, codContrato);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    contrato = new ContratoModel(codContrato, resultSet.getString("NOME_EMPRESA"), resultSet.getString("CNPJ"));
                    contrato.setNumeroDoContrato(resultSet.getString("NUMERO_CONTRATO"));
                    contrato.setNumeroProcessoSTJ(resultSet.getString("NUMERO_PROCESSO_STJ"));
                    contrato.setSeAtivo(resultSet.getString("SE_ATIVO"));
                    contrato.setObjeto(resultSet.getString("OBJETO"));
                    contrato.setLoginAtualizacao(resultSet.getString("LOGIN_ATUALIZACAO"));
                    contrato.setDataAtualizacao(resultSet.getDate("DATA_ATUALIZACAO"));
                    contrato.setHistoricoGestao(new HistoricoDAO(connection).getHistoricoGestor(codContrato));
                    contrato.setPercentuais(new PercentualDAO(connection).getPercentuaisDoContrato(codContrato));
                    contrato.setFuncoes(new CargoDAO(connection).getFuncoesContrato(codContrato, user));
                }
            }
            return contrato;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao tentar recuperar informações do contrato: " + codContrato + ". Para o usuário " + username + ". " + e.getMessage());
        }
    }

    /**
     * Retorna verdadeiro se o ano passado como argumento da função está dentro do período de vigência do contrato que
     * está se requisitando através do código do contrato, e retorna falso caso contrário.
     *
     * @param ano
     * @param codigoContrato
     * @return
     * @throws RuntimeException
     */
    public boolean anoDentroPeriodoVigencia(int ano, int codigoContrato) throws RuntimeException {
        String sql = "SELECT MIN(YEAR(EC.DATA_INICIO_VIGENCIA)), MAX(YEAR(EC.DATA_FIM_VIGENCIA)) FROM TB_CONTRATO C JOIN TB_EVENTO_CONTRATUAL EC ON EC.COD_CONTRATO = C.COD WHERE C.COD= ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, codigoContrato);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    if (ano >= resultSet.getInt(1) && ano <= resultSet.getInt(2)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao tentar verificar período de vigência no contrato para a função de verificação de meses em Total Mensal a Reter. Causado por: "
                    + e.getMessage());
        }
        return false;
    }


    public List<TipoEventoContratualModel> getTiposEventosContratuais() throws RuntimeException {
        List<TipoEventoContratualModel> tiposEventosContratuais = new ArrayList<>();
        String sql = "SELECT * FROM TB_TIPO_EVENTO_CONTRATUAL TEC  WHERE TEC.TIPO !='CONTRATO'; ";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    TipoEventoContratualModel tipoEventoContratualModel = new TipoEventoContratualModel(resultSet.getInt("COD"), resultSet.getString("TIPO"),
                            resultSet.getString("LOGIN_ATUALIZACAO"), resultSet.getDate("DATA_ATUALIZACAO"));
                    tiposEventosContratuais.add(tipoEventoContratualModel);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao tentar recuperar os tipos de eventos contratuais. Entre em contato com o Administrador do Sistema. Causa: " + e.getMessage());
        }
        return tiposEventosContratuais;
    }

    /**
     * Função para cadastrar ajuste. Esta função criar o registro de evento contratual com informações inseridas pelo
     * usuário e atualizar as informações das outras tabelas como por exemplo definir a data fim para os percentuais
     * vigentes e acrescentar o novo registro para os novos percentuais
     *
     * @param contrato
     * @param username
     * @throws RuntimeException
     * @throws SQLException
     */
    public void cadastrarAjusteContrato(ContratoModel contrato, String username) throws RuntimeException, SQLException {
        this.connection.setAutoCommit(false);
        Savepoint savepoint = this.connection.setSavepoint("Savepoint1");

        InsertTSQL insertTSQL = new InsertTSQL(connection);
        ConsultaTSQL consultaTSQL = new ConsultaTSQL(connection);
        UpdateTSQL updateTSQL = new UpdateTSQL(connection);

        int vCodHistoricoGestaoVigente = 0;
        int vCodPercentualVigente = 0;
        int vCodFuncaoContrato = 0;
        int vCodRubrica = 0;

        CargoModel vFuncaoComRemuneracaoVigente = null;

        String sql = "";

        try {
            int vCodEventoContratual = insertTSQL.InsertEventoContratual(contrato.getCodigo(),
                    contrato.getEventoContratual().getTipo().getCod(),
                    String.valueOf(contrato.getEventoContratual().getProrrogacao()),
                    contrato.getEventoContratual().getAssunto(),
                    contrato.getEventoContratual().getDataInicioVigencia(),
                    contrato.getEventoContratual().getDataFimVigencia(),
                    contrato.getEventoContratual().getDataAssinatura(),
                    username);
            for (HistoricoGestorModel hgc : contrato.getHistoricoGestao()) {

                vCodHistoricoGestaoVigente = consultaTSQL.RetornaRegistroHistoricoGestaoVigente(contrato.getCodigo(),
                        hgc.getCodigoPerfilGestao());
                if (vCodHistoricoGestaoVigente != 0) {
                    updateTSQL.UpdateDataFimHistoricoGestaoContrato(vCodHistoricoGestaoVigente, hgc.getInicio(), username);
                    insereHistoricoGestaoContrato(contrato.getCodigo(), hgc.getGestor(),
                            hgc.getCodigoPerfilGestao(), hgc.getInicio(), username);
                } else {
                    throw new SQLException("Nenhum historico encontrado para ser atualizado");
                }
            }

            for (PercentualModel pcm : contrato.getPercentuais()) {
                vCodPercentualVigente = consultaTSQL.RetornaPercentualContratoVigente(contrato.getCodigo(),
                        pcm.getRubrica().getCodigo());
                if (vCodPercentualVigente != 0) {
                    updateTSQL.UpdateDataFimPercentualContrato(vCodPercentualVigente, pcm.getDataInicio(), username);
                    insertTSQL.InsertPercentualContrato(contrato.getCodigo(), pcm.getRubrica().getCodigo(),
                            pcm.getPercentual(), pcm.getDataInicio(), null, pcm.getDataAditamento(), username);

                } else {
                    throw new SQLException("Nenhum percentual no contrato com esta rubrica para ser atualizado !");
                }
            }

            for (CargoModel cm : contrato.getFuncoes()) {
                vCodFuncaoContrato = consultaTSQL.RetornaCodFuncaoContrato(contrato.getCodigo(), cm.getCodigo());
                if (vCodFuncaoContrato != 0) {
                    vFuncaoComRemuneracaoVigente = consultaTSQL.RetornaFuncaoRemuneracaoVigente(vCodFuncaoContrato);
                    if (vFuncaoComRemuneracaoVigente.getRemuneracao() != cm.getRemuneracao() ||
                            vFuncaoComRemuneracaoVigente.getTrienios() != cm.getTrienios() ||
                            vFuncaoComRemuneracaoVigente.getAdicionais() != cm.getAdicionais() ||
                            vFuncaoComRemuneracaoVigente.getConvencao().getCodigo() != cm.getConvencao().getCodigo()) {
                        updateTSQL.UpdateFimRemuneracaoFuncao(vCodFuncaoContrato,
                                contrato.getEventoContratual().getDataInicioVigencia(), username);
                        insertTSQL.InsertRemuneracaoFunCon(vCodFuncaoContrato, cm.getConvencao().getCodigo(),
                                contrato.getEventoContratual().getDataInicioVigencia(), null,
                                contrato.getEventoContratual().getDataAssinatura(), cm.getRemuneracao(),
                                cm.getAdicionais(), cm.getTrienios(), username);
                    }
                } else {
                    throw new SQLException("A função não foi encontrada neste contrato !");
                }
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (Exception ex) {
            connection.rollback(savepoint);
            ex.printStackTrace();
            throw new RuntimeException("Erro ao tentar cadastrar ajuste, contrato: " + contrato.getNomeDaEmpresa() + " . Causa :" + ex.getMessage());
        }
    }

    private void insereHistoricoGestaoContrato(int pCodContrato, String nomeGestor, int pCodPerfilGestao, Date pDataInicio, String pUsername) {
        InsertTSQL insertTSQL = new InsertTSQL(connection);
        int vCodUsuarioGestor = 0;
        String sql = "SELECT COD FROM TB_USUARIO WHERE NOME=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, nomeGestor);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    vCodUsuarioGestor = resultSet.getInt("COD");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro. Usuário indicado para gestor do contrato não existe no sistema !");
        }
        insertTSQL.InsertHistoricoGestaoContrato(pCodContrato, vCodUsuarioGestor, pCodPerfilGestao, pDataInicio, null, pUsername);
    }
}
