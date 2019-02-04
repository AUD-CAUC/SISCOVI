package br.jus.stj.siscovi.calculos;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Periodos {
    private Connection connection;
    Periodos(Connection connection) {
        this.connection = connection;
    }
    public String RetornaAnoContrato(int cod) throws SQLException {
        String dataI;
        Date dataInicio;
        dataInicio = RetornaAnoContratoD(cod);
        if(dataInicio == null){
            return null;
        }else {
            dataI = dataInicio.toString();
            return dataI;
        }
    }
    public Date RetornaAnoContratoD(int cod) throws SQLException{
        ResultSet rs;
        Date dataInicio;
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT DATA_INICIO FROM TB_CONTRATO WHERE COD=?");
        preparedStatement.setInt(1,cod);
        rs = preparedStatement.executeQuery();
        if(rs.next()){
            dataInicio = rs.getDate(1);
            return dataInicio;
        }
        return null;
    }

    /**
     * Função que retorna o número de dias que um funcionário trabalhou em determinado período do mês.
     * @param pCodFuncaoContrato
     * @param pCodFuncaoTerceirizado
     * @param pMes
     * @param pAno
     * @param pOperacao
     * @return vRetorno
     */
    public int DiasTrabalhadosMesParcial(int pCodFuncaoContrato, int pCodFuncaoTerceirizado, int pMes, int pAno, int pOperacao) {

        int vCodContrato = 0;
        Date vDataReferencia = Date.valueOf(pAno + "-" + pMes + "-01"); // --Definição da data de referência como primeiro dia do mês de acordo com os argumentos passados.
        Date vDataFim = null;
        Date vDataInicioFuncao = null;
        Date vDataFimFuncao = null;
        Date vDataInicio = null;
        int vRetorno = 0;

        /*
         --Operação 1: Primeira metade da convenção.
         --Operação 2: Segunda metade da convenção.
         --Operação 3: Primeira metade do percentual.
         --Operação 4: Segunda metade do percentual.
         */

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // --Carregamento do código do contrato.
            preparedStatement = connection.prepareStatement("SELECT COD_CONTRATO FROM TB_CARGO_CONTRATO WHERE COD=?");
            preparedStatement.setInt(1, pCodFuncaoContrato);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                vCodContrato = resultSet.getInt("COD_CONTRATO");
            }
            // --Carregamento das datas de disponibilização e desligamento do terceirizado.
            preparedStatement = connection.prepareStatement("SELECT DATA_DISPONIBILIZACAO, DATA_DESLIGAMENTO FROM TB_CARGO_FUNCIONARIO WHERE COD=?");
            preparedStatement.setInt(1, pCodFuncaoTerceirizado);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                vDataInicioFuncao = resultSet.getDate("DATA_DISPONIBILIZACAO");
                vDataFimFuncao = resultSet.getDate("DATA_DESLIGAMENTO");
            }
        }catch(SQLException sqle) {
            throw new NullPointerException("Erro ao tentar buscar um contrato na função 'dias trabalhados mês parcial'. COD: " + pCodFuncaoContrato);
        }

        /* --Primeira metade da convenção (a convenção anterior tem data fim naquele mês). */

        if(pOperacao == 1){
            try {
                preparedStatement = connection.prepareStatement("SELECT DATA_FIM, ? FROM TB_REMUNERACAO_FUN_CON WHERE DATA_ADITAMENTO IS NOT NULL " +
                        "AND (DATEPART(MONTH, DATA_FIM))=(DATEPART(MONTH, ?)) AND (DATEPART(YEAR, DATA_FIM_CONVENCAO)) = (DATEPART(YEAR, ?))" +
                        " AND COD_FUNCAO_CONTRATO = ?");
                preparedStatement.setDate(1, vDataReferencia);
                preparedStatement.setDate(2, vDataReferencia);
                preparedStatement.setDate(3, vDataReferencia);
                preparedStatement.setInt(4, pCodFuncaoContrato);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    vDataFim = resultSet.getDate("DATA_FIM");
                    vDataInicio = resultSet.getDate(2);
                }
            }catch(SQLException sqle) {
                throw new NullPointerException("");
            }
        }

        /* --Segunda metade da convenção (a convenção mais recente tem data inicio naquele mês). */

        if(pOperacao == 2){
            try {
                preparedStatement = connection.prepareStatement("SELECT (EOMONTH(?)), MAX(DATA_INICIO) FROM TB_REMUNERACAO_FUN_CON WHERE DATA_ADITAMENTO IS NOT NULL " +
                        "AND (DATEPART(MONTH, DATA_INICIO))=(DATEPART(MONTH, ?)) AND (DATEPART(YEAR, DATA_INICIO))=(DATEPART(YEAR, ?))" +
                        " AND COD_FUNCAO_CONTRATO=?");
                preparedStatement.setDate(1, vDataReferencia);
                preparedStatement.setDate(2, vDataReferencia);
                preparedStatement.setDate(3, vDataReferencia);
                preparedStatement.setInt(4, pCodFuncaoContrato);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    vDataFim = resultSet.getDate(1);
                    vDataInicio = resultSet.getDate("DATA_INICIO_CONVENCAO");
                }
            }catch(SQLException sqle) {
                throw new NullPointerException("");
            }
            LocalDate date = vDataFim.toLocalDate(); // Converte vDataFim para a nova API de Datas do Java 8
            int dataFim = date.getDayOfMonth(); // Extrai o dia do mês da data segundo a nova API
            if(dataFim == 31){
                vDataFim = Date.valueOf(date.minusDays(1)); // Converte de volta o dia do mês menos 1 para a data em sql
            }
        }

        /* --Primeira metade do percentual (último percentual não tem data fim). */

        if(pOperacao == 3){
            try {
                preparedStatement = connection.prepareStatement("SELECT MIN(pc.DATA_FIM), ? FROM TB_PERCENTUAL_CONTRATO pc WHERE COD_CONTRATO=? " +
                        "AND pc.DATA_ADITAMENTO IS NOT NULL AND (DATEPART(MONTH, pc.DATA_FIM))=(DATEPART(MONTH, ?)) AND (DATEPART(YEAR, DATA_FIM))=(DATEPART(YEAR, ?))");
                preparedStatement.setDate(1, vDataReferencia);
                preparedStatement.setInt(2, vCodContrato);
                preparedStatement.setDate(3, vDataReferencia);
                preparedStatement.setDate(4, vDataReferencia);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    vDataFim = resultSet.getDate(1);
                    vDataInicio = resultSet.getDate(2);
                }
            }catch(SQLException sqle) {
                throw new NullPointerException("");
            }
        }

        /* -Segunda metade do percentual. */

        if(pOperacao == 4) {
            try {
                preparedStatement = connection.prepareStatement("SELECT MAX(PC.DATA_INICIO), EOMONTH(?) FROM TB_PERCENTUAL_CONTRATO PC WHERE COD_CONTRATO=?" +
                        " AND PC.DATA_ADITAMENTO IS NOT NULL AND (DATEPART(MONTH, PC.DATA_INICIO))=(DATEPART(MONTH,?)) AND" +
                        " (DATEPART(YEAR, DATA_INICIO))=(DATEPART(YEAR, ?))");
                preparedStatement.setDate(1, vDataReferencia);
                preparedStatement.setInt(2, vCodContrato);
                preparedStatement.setDate(3, vDataReferencia);
                preparedStatement.setDate(4, vDataReferencia);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    vDataInicio = resultSet.getDate(1);
                    vDataReferencia = resultSet.getDate(2);
                    /* --Ajuste do último dia para que o mês contenha apenas 30 dias. */

                    LocalDate date = vDataInicio.toLocalDate(); // Converte vDataInicio para a nova API de Datas do Java 8
                    int dataInicio = date.getDayOfMonth(); // Extrai o dia do mês da data segundo a nova API
                    if (dataInicio == 31) {
                        vDataInicio = Date.valueOf(date.minusDays(1)); // Converte de volta o dia do mês menos 1 para a data em sql
                    }
                }
            }catch(SQLException sqle) {
                throw new NullPointerException("");
            }
        }

        /* --Definição do número de dias trabalhados para o caso de primeira metade do mês. */
        if((pOperacao == 1) || (pOperacao == 3)){
            if(vDataFimFuncao == null){
                if(vDataInicioFuncao.before(vDataReferencia)){
                    LocalDate dataFim = vDataFim.toLocalDate();
                    LocalDate dataReferencia = vDataReferencia.toLocalDate();
                    long numeroDeDias = ChronoUnit.DAYS.between(dataReferencia, dataFim) + 1;
                    vRetorno = (int) numeroDeDias;
                }

                if((vDataInicioFuncao.after(vDataReferencia) || vDataInicioFuncao.equals(vDataReferencia)) && (vDataInicioFuncao.equals(vDataFim) || vDataInicioFuncao.before(vDataFim))){
                    LocalDate dataFim = vDataFim.toLocalDate();
                    LocalDate dataDisponibilizacao = vDataInicioFuncao.toLocalDate();
                    long numeroDeDias = ChronoUnit.DAYS.between(dataDisponibilizacao, dataFim) + 1;
                    vRetorno = (int) numeroDeDias;
                }

            }
            /* --Caso exista uma data de desligamento do terceirizado.*/
            if(vDataFimFuncao != null){
                /* Se a data de disponibilização é inferior a data referência e a data de
                 * desligamento é superior ao último dia do mês referência então o
                 * funcionário trabalhou os dias entre o inicio do mes e a data fim.
                 */

                if((vDataInicioFuncao.before(vDataReferencia)) && (vDataFimFuncao.after(vDataFim))){
                    LocalDate dataFim = vDataFim.toLocalDate();
                    LocalDate dataReferencia = vDataReferencia.toLocalDate();
                    long numeroDeDias = ChronoUnit.DAYS.between(dataReferencia, dataFim) + 1;
                    vRetorno = (int) numeroDeDias;
                }

                /* Se a data de disponibilização está no mês referência e a data de
                 * desligamento é superior mês referência, então se verifica a quantidade
                 * de dias trabalhados pelo funcionário entre a data fim e a disponibilização.
                 */

                if((vDataInicioFuncao.after(vDataReferencia) || vDataInicioFuncao.equals(vDataReferencia)) && (vDataInicioFuncao.before(vDataFim) || vDataInicioFuncao.equals(vDataFim))
                        && (vDataFimFuncao.after(vDataFim))){
                    LocalDate dataFim = vDataFim.toLocalDate();
                    LocalDate dataDisponibilizacao = vDataInicioFuncao.toLocalDate();
                    long numeroDeDias = ChronoUnit.DAYS.between(dataDisponibilizacao, dataFim) + 1;
                    vRetorno = (int) numeroDeDias;
                }

                /* Se a data de disponibilização está na primeira metade do mês referência
                 * e também a data de desligamento, então contam-se os dias trabalhados
                 * pelo funcionário entre o desligamento e a disponibilização.
                 */

                if((vDataInicioFuncao.equals(vDataReferencia) || vDataInicioFuncao.after(vDataReferencia)) && (vDataInicioFuncao.before(vDataFim) || vDataInicioFuncao.equals(vDataFim)) &&
                        (vDataFimFuncao.after(vDataReferencia) || vDataFimFuncao.equals(vDataReferencia)) && (vDataFimFuncao.before(vDataFim) || vDataFimFuncao.equals(vDataFim))) {
                    LocalDate dataFimFuncao = vDataFimFuncao.toLocalDate();
                    LocalDate dataInicioFuncao = vDataInicioFuncao.toLocalDate();
                    long numeroDeDias = ChronoUnit.DAYS.between(dataInicioFuncao, dataFimFuncao) + 1;
                    vRetorno = (int) numeroDeDias;
                }

                /*
                 * Se a data da disponibilização for inferior ao mês de cálculo e
                 * o funcionário tiver desligamento antes da data fim, então contam-se
                 * os dias trabalhados nesse período.
                 */

                if(vDataInicioFuncao.before(vDataReferencia) && (vDataFimFuncao.after(vDataReferencia) || vDataInicioFuncao.equals(vDataReferencia))
                        && (vDataFimFuncao.before(vDataFim) || vDataFimFuncao.before(vDataFim))){
                    LocalDate dataFimFuncao = vDataFimFuncao.toLocalDate();
                    LocalDate dataReferencia = vDataReferencia.toLocalDate();
                    long numeroDeDias = ChronoUnit.DAYS.between(dataReferencia, dataFimFuncao) + 1;
                    vRetorno = (int) numeroDeDias;
                }
            }
        }

        /* --Cálculo para a segunda metade do mês. */

        if((pOperacao == 2) || (pOperacao == 4)){
            /* Se o terceirizado não possui data de desligamento. */
            if(vDataFimFuncao == null){
                /*--Se a disponibilização é inferior a data referência.*/
                if(vDataReferencia.after(vDataInicioFuncao)) {
                    LocalDate dataFim = vDataFim.toLocalDate();
                    LocalDate dataInicio = vDataInicio.toLocalDate();
                    long numeroDeDias = ChronoUnit.DAYS.between(dataFim, dataInicio) + 1;
                    vRetorno = (int) numeroDeDias;
                }
                /* Caso a disponibilização esteja dentro do período.*/
                if((vDataInicioFuncao.after(vDataInicio) || vDataInicioFuncao.equals(vDataInicio)) && (vDataInicioFuncao.before(vDataFim) || vDataInicioFuncao.equals(vDataFim))){
                    LocalDate dataFim = vDataFim.toLocalDate();
                    LocalDate dataInicioFuncao = vDataInicioFuncao.toLocalDate();
                    long numeroDeDias = ChronoUnit.DAYS.between( dataInicioFuncao, dataFim) + 1;
                    vRetorno = (int) numeroDeDias;
                }
            }

            if(vDataFimFuncao != null){

                /* Se a data de disponibilização é inferior a data referência e a data de
                 * desligamento é superior ao último dia do mês referência então o
                 * funcionário trabalhou os dias entre o início e o fim.
                 */

                if(vDataInicioFuncao.before(vDataReferencia) && vDataFimFuncao.after(vDataFim)){
                    LocalDate dataFim = vDataFim.toLocalDate();
                    LocalDate dataInicio = vDataInicio.toLocalDate();
                    long numeroDeDias = ChronoUnit.DAYS.between(dataInicio, dataFim) + 1;
                    vRetorno = (int) numeroDeDias;
                }

                /* Se a data de disponibilização é maior que a data de inicio
                 * e a data de desligamento superior a data de fim então
                 * conta-se o período entre data fim e data de disponibilização.
                 */

                if((vDataInicioFuncao.after(vDataInicio) || vDataInicioFuncao.equals(vDataInicio)) && (vDataInicioFuncao.before(vDataFim) || vDataInicioFuncao.equals(vDataFim))
                        && (vDataFimFuncao.after(vDataFim))) {
                   vRetorno = (int) ChronoUnit.DAYS.between(vDataInicioFuncao.toLocalDate(), vDataFim.toLocalDate()) + 1;
                }

                /* Se a data de disponibilização é maior que a data de inicio
                 * e a data de desligamento inferior a data de fim e superior
                 * a data de inicio então conta-se este período.
                 */

                if((vDataInicioFuncao.after(vDataInicio) || vDataInicioFuncao.equals(vDataInicio)) && (vDataInicioFuncao.before(vDataFim) || vDataInicioFuncao.equals(vDataFim))
                        && (vDataFimFuncao.after(vDataInicio) || vDataFimFuncao.equals(vDataInicio)) && (vDataFimFuncao.before(vDataFim) || vDataFimFuncao.equals(vDataFim))) {
                    LocalDate dataFimFuncao = vDataFimFuncao.toLocalDate();
                    LocalDate dataInicioFuncao= vDataInicioFuncao.toLocalDate();
                    long numeroDeDias = ChronoUnit.DAYS.between(dataInicioFuncao, dataFimFuncao) + 1;
                    return (int) numeroDeDias;
                }

                /* Se a data da disponibilização for inferior ao mês de cálculo e
                 * o funcionário tiver desligamento no mês referência, então contam-se
                 * os dias trabalhados.
                 */

                if(vDataInicioFuncao.before(vDataInicio) && (vDataFimFuncao.after(vDataInicio) || vDataFimFuncao.equals(vDataInicio))
                        && (vDataFimFuncao.before(vDataFim) || vDataFimFuncao.equals(vDataFim))){
                    LocalDate dataFimFuncao= vDataFimFuncao.toLocalDate();
                    LocalDate dataInicio = vDataInicio.toLocalDate();
                    long numeroDeDias = ChronoUnit.DAYS.between(dataInicio, dataFimFuncao) + 1;
                   vRetorno = (int) numeroDeDias;
                }
            }
        }

        return vRetorno;
    }
    public long RetornaNumeroDeMeses(Date pDataInicio, Date pDataFim){

        /**
         * --Função que retorna o número de mêses que deverão ser levados em consideração entre duas datas.
         */

        long vRetorno = 0;

        /** --Se as datas estão no mesmo ano. */

        if(pDataFim.toLocalDate().getYear() == pDataInicio.toLocalDate().getYear()) {
            vRetorno = ChronoUnit.MONTHS.between(pDataInicio.toLocalDate(), pDataFim.toLocalDate()) + 1;
        }

        /** --Caso sejam anos diferentes. */

        if(pDataInicio.toLocalDate().getYear() != pDataFim.toLocalDate().getYear()) {

            /**--Loop para o período de anos determinado. */

            for(int i = pDataInicio.toLocalDate().getYear(); i > pDataFim.toLocalDate().getYear(); i++) { // Verificar se a data fim é Maior que a data de Inicio no front-end ?

                /**--O primeiro ano é calculado em relação ao fim dele (mês 12). */
                if(i == pDataInicio.toLocalDate().getYear()) {
                 vRetorno = 12 - pDataInicio.toLocalDate().getMonthValue() + 1;
                }

                /**--Os anos que estiverem entre o primeiro e o último acrescem 12 ao resultado final.*/
                if((i != pDataFim.toLocalDate().getYear()) && (i != pDataInicio.toLocalDate().getYear())){
                    vRetorno = vRetorno + 12;
                }
                /**--O último ano soma o número de meses da data fim.*/
                if(i == pDataFim.toLocalDate().getYear()) {
                    vRetorno = vRetorno + pDataFim.toLocalDate().getMonthValue();
                }
            }

        }
        return vRetorno;
    }

    /**
     *--Função que retorna o número de dias que um terceirizado trabalhou em determinado mês em uma função.
     * @param pCodFuncaoTerceirizado
     * @param pMes
     * @param pAno
     * @return 0 - 30
     */

    public int DiasTrabalhadosMes (int pCodFuncaoTerceirizado, int pMes, int pAno) {

        //Checked.

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        Date vDataInicio = null;
        Date vDataFim = null;
        Date vFimDoMes = null;
        int vContagemDeDias = 0;

        /**Data de referência e fim do mês definidas como o primeiro e o último dia
         do mês correspondente aos argumentos passados.*/

        Date vDataReferencia = Date.valueOf(pAno + "-" + pMes + "-01");

        if (pMes != 2) {

            vFimDoMes = Date.valueOf(pAno + "-" + pMes + "-30");

        } else {

            vFimDoMes = Date.valueOf(vDataReferencia.toLocalDate().withDayOfMonth(vDataReferencia.toLocalDate().lengthOfMonth()));

        }

        /**Carregamento das datas de disponibilização e desligamento do terceirizado.*/

        try {

            preparedStatement = connection.prepareStatement("SELECT DATA_INICIO," +
                                                                       " DATA_FIM" +
                                                                 " FROM TB_FUNCAO_TERCEIRIZADO" +
                                                                 " WHERE COD = ?");

            preparedStatement.setInt(1, pCodFuncaoTerceirizado);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                vDataInicio = resultSet.getDate("DATA_INICIO");
                vDataFim = resultSet.getDate("DATA_FIM");

            }

        } catch (SQLException sqle) {

            throw new NullPointerException("Erro ao carregar datas de disponibilização e desligamento de um funcionario para calcular seus dias trabalhados no mês: " + pMes +
                    ". CÓDIGO da Função do Terceirizado: " + pCodFuncaoTerceirizado);

        }

        /**Caso não possua data de desligamento.*/

        Date ultimoDiaDataRef = Date.valueOf(vDataReferencia.toLocalDate().withDayOfMonth(vDataReferencia.toLocalDate().lengthOfMonth()));

        if (vDataFim == null) {

            /**Se a data de disponibilização é inferior a data referência então o
             terceirizado trabalhou os 30 dias do mês referência pois, a data
             referência é sempre o primeiro dia do mês.*/

            if (vDataInicio.before(vDataReferencia)) {

                return 30;

            }

            /**Se a data de disponibilização está no mês referência enão se verifica
             a quantidade de dias trabalhados pelo terceirizado.*/

            if ((vDataInicio.after(vDataReferencia) || vDataInicio.equals(vDataReferencia)) && (vDataInicio.before(vFimDoMes) || (vDataInicio.equals(vFimDoMes)))) {

                vContagemDeDias = ContaDias(vDataInicio.toLocalDate(), vFimDoMes.toLocalDate());

            }

        }

        /**Caso possua data de desligamento.*/

        if (vDataFim != null) {

            /**Se a data de disponibilização é inferior a data referência e a data de
             desligamento é superior ao último dia do mês referência então o
             terceirizado trabalhou os 30 dias.*/

            if (vDataInicio.before(vDataReferencia) && vDataFim.after(vFimDoMes)) {

                return 30;

            }

            /**Se a data de disponibilização está no mês referência e a data de
             desligamento é superior mês referência, então se verifica a quantidade
             de dias trabalhados pelo terceirizado.*/

            if ((vDataInicio.after(vDataReferencia) || vDataInicio.equals(vDataReferencia)) && (vDataInicio.before(vFimDoMes) || vDataInicio.equals(vFimDoMes)) && (vDataFim.after(vFimDoMes))) {

                vContagemDeDias = ContaDias(vDataInicio.toLocalDate(), vFimDoMes.toLocalDate());

            }

            /**Se a data de disponibilização está no mês referência e também a data de desligamento, então contam-se os dias trabalhados pelo terceirizado.*/

            if ((vDataInicio.after(vDataReferencia) || vDataInicio.equals(vDataReferencia)) && (vDataInicio.before(vFimDoMes) || vDataInicio.equals(vFimDoMes)) &&
                (vDataFim.after(vDataReferencia) || (vDataFim.equals(vDataReferencia))) && (vDataFim.before(vFimDoMes) || vDataFim.equals(vFimDoMes))) {

                vContagemDeDias = ContaDias(vDataInicio, vDataFim);

            }

            /**Se a data da disponibilização for inferior ao mês de cálculo e o terceirizado tiver desligamento no mês referência, então contam-se os dias trabalhados.*/

            if (vDataInicio.before(vDataReferencia) && (vDataFim.after(vDataReferencia) || vDataFim.equals(vDataReferencia)) && (vDataFim.before(vFimDoMes) || vDataFim.equals(vFimDoMes))) {

                vContagemDeDias = ContaDias(vDataReferencia, vDataFim);

            }

        }

        /**Para o mês de fevereiro se equaliza o número de dias contados.*/

        if (pMes == 2) {

            /**Se o mês for de 28 dias então soma-se 2 a contagem.*/

            if (vFimDoMes.toLocalDate().getDayOfMonth() == 28) {

                vContagemDeDias = vContagemDeDias + 2;

            } else {

                /**Se o mês não for de 28 dias ele é de 29.
                 Então soma-se 1a contagem.*/

                vContagemDeDias = vContagemDeDias + 1;

            }

        }

        return vContagemDeDias;

    }


    int ContaDias (Date dataInicial, Date dataFinal) {

        //Checked.

        return (int) ChronoUnit.DAYS.between(dataInicial.toLocalDate(), dataFinal.toLocalDate()) + 1;

    }

    int ContaDias (LocalDate dataInicial, LocalDate dataFinal) {

        //Checked.

        return (int) ChronoUnit.DAYS.between(dataInicial, dataFinal) + 1;

    }

    /**
     * Função que retorna o número de dias trabalhados correspondentes a metade de um percentual ou remuneração em um mês de mudança.
     * Ex.: 13 dias primeirametade remuneração e 17 dias segunda metada da remuneração.
     * @param pCodFuncaoContrato
     * @param pMes
     * @param pAno
     * @param pOperacao
     * @return vRetorno
     */
    public int RetornaNumeroDiasMesParcial(int pCodFuncaoContrato, int pMes, int pAno, int pOperacao) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;

        int vRetorno = 0;
        Date vDataReferencia = Date.valueOf(pAno + "-" + pMes + "-01"); // --Definição da data referência como primeiro dia do mês de acordo com os argumentos passados.
        Date vDataFimPercentual = null;
        Date vDataInicioPercentual = null;
        int vCodContrato = 0;

        /*
          Operação 1: Primeira metade da remuneração.
          Operação 2: Segunda metade da remuneração.
          Operação 3: Primeira metade do percentual.
          Operação 4: Segunda metade do percentual.
         */

        // Carrega o código do contrato.
        try {
            preparedStatement = connection.prepareStatement("SELECT COD_CONTRATO FROM TB_FUNCAO_CONTRATO WHERE COD=?");
            preparedStatement.setInt(1, pCodFuncaoContrato);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                vCodContrato = resultSet.getInt(1);
            }
        }catch(SQLException sqle) {
            throw new NullPointerException("Erro ao tentar carregar o contrato de retorno de dias do numero de dias do mês parcial. COD: " + pCodFuncaoContrato);
        }
        // --Primeira metade da remuneração.
        if(pOperacao == 1) {
            try {
                preparedStatement = connection.prepareStatement("SELECT DATEDIFF(DAY, DATA_FIM, ?) + 1 FROM TB_REMUNERACAO_FUN_CON WHERE DATA_ADITAMENTO IS NOT NULL AND COD_FUNCAO_CONTRATO = ?" +
                        " AND MONTH(DATA_FIM)=MONTH(?) AND YEAR(DATA_FIM)=YEAR(?)");
                preparedStatement.setDate(1, vDataReferencia);
                preparedStatement.setInt(2, pCodFuncaoContrato);
                preparedStatement.setDate(3, vDataReferencia);
                preparedStatement.setDate(4, vDataReferencia);
                resultSet = preparedStatement.executeQuery();
                if(resultSet.next()) {
                    vRetorno = resultSet.getInt(1);
                }
            } catch (SQLException e) {
                throw new NullPointerException("Erro ao tentar calcular o período dpara a primeira metade da remuneração !");
            }
        }
        // --Segunda metade da remuneração.
        if(pOperacao == 2) {
            try {
                preparedStatement = connection.prepareStatement("SELECT DATEDIFF(DAY, DATA_FIM, EOMONTH(?)) + 1 FROM TB_REMUNERACAO_FUN_CON WHERE DATA_ADITAMENTO IS NOT NULL " +
                        " AND COD_FUNCAO_CONTRATO=? AND MONTH(DATA_INICIO)=MONTH(?) AND YEAR(DATA_INICIO)=YEAR(?)");
                preparedStatement.setDate(1, vDataReferencia);
                preparedStatement.setInt(2, pCodFuncaoContrato);
                preparedStatement.setDate(3, vDataReferencia);
                preparedStatement.setDate(4, vDataReferencia);
                resultSet = preparedStatement.executeQuery();
                if(resultSet.next()) {
                    vRetorno = resultSet.getInt(1);
                }

            } catch (SQLException e) {
                throw new NullPointerException("Erro ao tentar calcular o período dpara a primeira metade da remuneração !");
            }
            if(vDataReferencia.toLocalDate().getDayOfMonth() == 31) {
                vRetorno = vRetorno - 1;
            }
        }
        // --Primeira metade do percentual.
        if(pOperacao == 3) {
            try {
                preparedStatement = connection.prepareStatement("SELECT MIN(PC.DATA_FIM) FROM TB_PERCENTUAL_CONTRATO PC WHERE COD_CONTRATO=? AND PC.DATA_ADITAMENTO IS NOT NULL" +
                        " AND MONTH(PC.DATA_FIM)=MONTH(?) AND YEAR(PC.DATA_FIM)=YEAR(?)");
                preparedStatement.setInt(1, vCodContrato);
                preparedStatement.setDate(2, vDataReferencia);
                preparedStatement.setDate(3, vDataReferencia);
                resultSet = preparedStatement.executeQuery();
                if(resultSet.next()) {
                    vDataFimPercentual = resultSet.getDate(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            vRetorno = (int)(ChronoUnit.DAYS.between(vDataReferencia.toLocalDate(), vDataFimPercentual.toLocalDate()));
        }
        if(pOperacao == 4) {
            try {
                preparedStatement = connection.prepareStatement("SELECT MAX(PC.DATA_INICIO) FROM TB_PERCENTUAL_CONTRATO PC WHERE COD_CONTRATO=? AND PC.DATA_ADITAMENTO IS NOT NULL" +
                        " AND MONTH(PC.DATA_INICIO)=MONTH(?) AND YEAR(PC.DATA_INICIO)=YEAR(?)");
                preparedStatement.setInt(1, vCodContrato);
                preparedStatement.setDate(2, vDataReferencia);
                preparedStatement.setDate(3, vDataReferencia);
                resultSet = preparedStatement.executeQuery();
                if(resultSet.next()) {
                    vDataFimPercentual = resultSet.getDate(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            vRetorno = (int) (ChronoUnit.DAYS.between(vDataInicioPercentual.toLocalDate(), vDataReferencia.toLocalDate().withDayOfMonth(vDataReferencia.toLocalDate().lengthOfMonth())));
            if(vDataReferencia.toLocalDate().getDayOfMonth() == 31) {
                vRetorno = vRetorno - 1;
            }
        }
        return vRetorno;
    }

    /**
     * Função que retorna o número de dias que um terceirizado trabalhou em uma função em um determinado período do mês.
     * @param pCodFuncaoTerceirizado
     * @param pDataInicio
     * @param pDataFim
     * @return
     */

    public int DiasTrabalhadosPeriodo(int pCodFuncaoTerceirizado, Date pDataInicio, Date pDataFim) {

        //Checked.

        Date vDataInicio = null;
        Date vDataFim = null;

        /**Carregamento das datas de disponibilização e desligamento do terceirizado na função.*/

        String sql = "SELECT DATA_INICIO, DATA_FIM FROM TB_FUNCAO_TERCEIRIZADO WHERE COD = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, pCodFuncaoTerceirizado);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {

                    vDataInicio = resultSet.getDate("DATA_INICIO");
                    vDataFim = resultSet.getDate("DATA_FIM");

                }

            }

        } catch (SQLException e) {

            throw new NullPointerException("Erro ao tentar carregar as datas de disponibilização e de desligamento do terceirizado na função: " + pCodFuncaoTerceirizado +
                    ", no período especificado. Data Inicio: " + pDataInicio.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                    ". Data Fim: " + pDataFim.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        }

        /**Caso não possua data de desligamento.*/

        if(vDataFim == null) {

            /**Se a data de disponibilização é inferior a data referência então o terceirizado trabalhou o período completo, a data
             referência é sempre a data inicio argumento da função.*/


            if (vDataInicio.before(pDataInicio)) {

                return (int) ChronoUnit.DAYS.between(pDataInicio.toLocalDate(), pDataFim.toLocalDate()) + 1;

            }

            /**Se a data de disponibilização está no mês referência enão se verifica
             a quantidade de dias trabalhados pelo terceirizado no período.*/

            if ((vDataInicio.after(pDataInicio) || vDataInicio.equals(pDataInicio)) && (vDataInicio.before(pDataFim) || vDataInicio.equals(pDataFim))) {

                return (int) ChronoUnit.DAYS.between(vDataInicio.toLocalDate(), pDataFim.toLocalDate()) + 1;

            }

        }

        /**Caso possua data de desligamento.*/

        if (vDataFim != null) {

            /**Se a data de disponibilização é inferior a data referência e a data de desligamento é superior ao último
             dia do mês referência então o terceirizado trabalhou os 30 dias.*/

            if (vDataInicio.before(pDataInicio) && vDataFim.after(pDataFim)) {

                return (int) ChronoUnit.DAYS.between(pDataInicio.toLocalDate(), pDataFim.toLocalDate()) + 1;

            }

            /**Se a data de disponibilização está no mês referência e a data de
             desligamento é superior mês referência, então se verifica a quantidade
             de dias trabalhados pelo terceirizado.*/

            if ((vDataInicio.after(pDataInicio) || vDataInicio.equals(pDataInicio)) && (vDataInicio.before(pDataFim) || vDataInicio.equals(pDataFim)) &&
                vDataFim.after(pDataFim)) {

                return (int) ChronoUnit.DAYS.between(vDataInicio.toLocalDate(), pDataFim.toLocalDate()) + 1;

            }

            /**Se a data de disponibilização está no mês referência e também a data de
             desligamento, então contam-se os dias trabalhados pelo terceirizado.*/

            if ((vDataInicio.after(pDataInicio) || vDataInicio.equals(pDataInicio)) && (vDataInicio.before(pDataFim) || vDataInicio.equals(pDataFim)) &&
                    (vDataFim.after(pDataInicio) || vDataFim.equals(pDataInicio)) && (vDataFim.before(pDataFim) || vDataFim.equals(pDataFim))) {

                return (int) ChronoUnit.DAYS.between(vDataInicio.toLocalDate(), vDataFim.toLocalDate()) + 1;

            }

            /**Se a data da disponibilização for inferior ao mês de cálculo e
             o terceirizado tiver desligamento no mês referência, então contam-se
             os dias trabalhados.*/

            if (vDataInicio.before(pDataInicio)  && (vDataFim.after(pDataInicio) || vDataFim.equals(pDataInicio)) &&
                    (vDataFim.before(pDataFim)) || vDataFim.equals(pDataFim)) {

                return (int) ChronoUnit.DAYS.between(pDataInicio.toLocalDate(), vDataFim.toLocalDate()) + 1;

            }

        }

        return 0;

    }

    /**
     * Método que equaliza o total de dias em um subperíodo de fevereiro somando 1 ou dois dias a última parcela.
     *
     * @param pDataReferencia;
     * @param pDataFim;
     * @param pDiasSubperiodo;
     *
     * @return Número inteiro de dias de um subperíodo.
     */

    int AjusteDiasSubperiodoFevereiro (Date pDataReferencia,
                                       Date pDataFim,
                                       int pDiasSubperiodo) {

        if (pDataFim.toLocalDate().getDayOfMonth() == Date.valueOf(pDataReferencia.toLocalDate().withDayOfMonth(pDataReferencia.toLocalDate().lengthOfMonth())).toLocalDate().getDayOfMonth()) {

            if (pDataFim.toLocalDate().getDayOfMonth() == 28) {

                pDiasSubperiodo = pDiasSubperiodo + 2;

            } else {

                pDiasSubperiodo = pDiasSubperiodo + 1;

            }

        }

        return pDiasSubperiodo;

    }

}
