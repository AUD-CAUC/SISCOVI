package br.jus.stj.siscovi.dao.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteTSQL {

    private Connection connection;

    public DeleteTSQL(Connection connection) {

        this.connection = connection;

    }

    /**
     * Método que exclui um registro da tabela de restituição de férias.
     *
     * @param pCodRestituicaoFerias;
     */

    public void DeleteRestituicaoFerias (int pCodRestituicaoFerias) {

        PreparedStatement preparedStatement;

        String query = "DELETE FROM tb_restituicao_ferias WHERE cod = ?";

        try {

            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, pCodRestituicaoFerias);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            throw new RuntimeException("Não foi possível deletar o registro solicitado da restituição de férias.");

        }

    }

    /**
     * Método que exclui um registro da tabela de saldo residual de férias.
     *
     * @param pCodRestituicaoFerias;
     */

    public void DeleteSaldoResidualFerias (int pCodRestituicaoFerias) {

        PreparedStatement preparedStatement;

        String query = "DELETE FROM tb_saldo_residual_ferias WHERE cod_restituicao_ferias = ?";

        try {

            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, pCodRestituicaoFerias);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            throw new RuntimeException("Não foi possível deletar o registro solicitado do saldo residual de férias.");

        }

    }

    /**
     * Método que exclui um registro da tabela de restituição de décimo terceiro.
     *
     * @param pCodRestituicaoDecimoTerceiro;
     */

    public void DeleteRestituicaoDecimoTerceiro (int pCodRestituicaoDecimoTerceiro) {

        PreparedStatement preparedStatement;

        String query = "DELETE FROM tb_restituicao_decimo_terceiro WHERE cod = ?";

        try {

            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, pCodRestituicaoDecimoTerceiro);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            throw new RuntimeException("Não foi possível deletar o registro solicitado da restituição de décimo terceiro.");

        }

    }

    /**
     * Método que exclui um registro da tabela de saldo residual de décimo terceiro.
     *
     * @param pCodRestituicaoDecimoTerceiro;
     */

    public void DeleteSaldoResidualDecimoTerceiro (int pCodRestituicaoDecimoTerceiro) {

        PreparedStatement preparedStatement;

        String query = "DELETE FROM tb_saldo_residual_dec_ter WHERE COD_RESTITUICAO_DEC_TERCEIRO = ?";

        try {

            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, pCodRestituicaoDecimoTerceiro);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            throw new RuntimeException("Não foi possível deletar o registro solicitado do saldo residual de décimo terceiro.");

        }

    }

    /**
     * Método que exclui um registro da tabela de restituição de rescisão.
     *
     * @param pCodRestituicaoRescisao;
     */

    public void DeleteRestituicaoRescisao (int pCodRestituicaoRescisao) {

        PreparedStatement preparedStatement;

        String query = "DELETE FROM tb_restituicao_rescisao WHERE cod = ?";

        try {

            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, pCodRestituicaoRescisao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            throw new RuntimeException("Não foi possível deletar o registro solicitado da restituição de rescisão.");

        }

    }

    /**
     * Método que exclui um registro da tabela de saldo residual de rescisão.
     *
     * @param pCodRestituicaoRescisao;
     */

    public void DeleteSaldoResidualRescisao (int pCodRestituicaoRescisao) {

        PreparedStatement preparedStatement;

        String query = "DELETE FROM tb_saldo_residual_rescisao WHERE COD_RESTITUICAO_RESCISAO = ?";

        try {

            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, pCodRestituicaoRescisao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            throw new RuntimeException("Não foi possível deletar o registro solicitado do saldo residual de rescisão.");

        }

    }

    /**
     * Método que exclui um registro da tabela de histórico de restituição de férias.
     *
     * @param pCodRestituicaoFerias;
     */

    public void DeleteHistRestituicaoFerias (int pCodRestituicaoFerias) {

        PreparedStatement preparedStatement;

        String query = "DELETE FROM tb_hist_restituicao_ferias WHERE COD_RESTITUICAO_FERIAS = ?";

        try {

            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, pCodRestituicaoFerias);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            throw new RuntimeException("Não foi possível deletar o registro solicitado do histórico de restituição de férias.");

        }

    }

    /**
     * Método que exclui um registro da tabela de histórico de restituição de décimo terceiro.
     *
     * @param pCodRestituicaoDecimoTerceiro;
     */

    public void DeleteHistRestituicaoDecimoTerceiro (int pCodRestituicaoDecimoTerceiro) {

        PreparedStatement preparedStatement;

        String query = "DELETE FROM tb_hist_restituicao_dec_ter WHERE COD_RESTITUICAO_DEC_TERCEIRO = ?";

        try {

            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, pCodRestituicaoDecimoTerceiro);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            throw new RuntimeException("Não foi possível deletar o registro solicitado do histórico de restituição de décimo terceiro.");

        }

    }

    /**
     * Método que exclui um registro da tabela de histórico de restituição de rescisão.
     *
     * @param pCodRestituicaoRescisao;
     */

    public void DeleteHistRestituicaoRescisao (int pCodRestituicaoRescisao) {

        PreparedStatement preparedStatement;

        String query = "DELETE FROM tb_hist_restituicao_rescisao WHERE COD_RESTITUICAO_RESCISAO = ?";

        try {

            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, pCodRestituicaoRescisao);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            throw new RuntimeException("Não foi possível deletar o registro solicitado do histórico de restituição de rescisão.");

        }

    }

    /**
     * Método que exclui um registro de uma tabela.
     *
     * @param pCod;
     * @param pTabela;
     */

    public int DeleteRegistro (int pCod, String pTabela) {

        PreparedStatement preparedStatement;
        int vRetorno = 1;

        String query = "DELETE FROM " + pTabela + " WHERE COD = ?";

        try {

            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, pCod);

            preparedStatement.executeUpdate();

        } catch (SQLException sqle) {

            throw new RuntimeException("Não foi possível deletar o registro da tabela " + pTabela + ".");

        }

        vRetorno = 0;

        return vRetorno;

    }

}
