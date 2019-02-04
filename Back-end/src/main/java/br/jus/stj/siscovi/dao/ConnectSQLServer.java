package br.jus.stj.siscovi.dao;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolDataSource;

public class ConnectSQLServer {
    private DataSource dataSource;
    public ConnectSQLServer() {
        SQLServerConnectionPoolDataSource ds = new SQLServerConnectionPoolDataSource();
        ds.setURL("jdbc:sqlserver://localhost\\vsousa:1433;database=SISCOVI;integratedSecurity=true");
        dataSource = ds;

    }
    public Connection dbConnect() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();

            //System.out.println("Conectado");
           // Statement statement = conn.createStatement();
            //String queryString = "select * from TB_PERFIL";
            //ResultSet rs = statement.executeQuery(queryString);
            //while (rs.next()){
            //System.out.println(rs.getString(1)+" "+rs.getString(2)+" "+rs.getString(3)+" "+rs.getString(4)+" "+rs.getString(5));
            //}
        } catch (Exception e) {
            throw new NullPointerException("Erro ao se conectar com banco de dados SQL Server, na base SISCOVI.");
        }
        return conn;
         }
        //public static void main(String[] args){
        //ConnectSQLServer connServer = new ConnectSQLServer();
       // connServer.dbConnect();
       // }
        //return conn;

}