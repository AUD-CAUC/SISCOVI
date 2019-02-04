package br.jus.stj.siscovi.dao;
import br.jus.stj.siscovi.model.User;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class LoginDAO{
    private Connection connection;
    int i=0;
    public LoginDAO(Connection connection){

        this.connection = connection;
    }

    public boolean checkLoginCredentials (String username, String password) throws NullPointerException, SQLException,SQLServerException {
        ResultSet rs = null;
        PreparedStatement prpstm= null;

        try {
            prpstm = connection.prepareStatement("SELECT LOGIN FROM TB_USUARIO WHERE LOGIN = ?");
            prpstm.setString(1, username);

            rs = prpstm.executeQuery();
            //System.out.println("Deu ruim, rs é :"+rs.getString(1));
            if(!(rs.next())){
                prpstm = connection.prepareStatement("INSERT INTO TB_USUARIO (LOGIN, PASSWORD) VALUES (?,?)");
                prpstm.setString(1, username);
                prpstm.setString(2, password);
                prpstm.execute();
                return true;
            }else{
                return false;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            i = 2;

        } catch(SQLServerException exc){
            exc.printStackTrace();
            i=2;

        } catch (SQLException ex) {
            ex.printStackTrace();
            i = 2;
        }
        if(i == 2){
            return true;
        }else{
            return false;
        }
    }
    public boolean checkLogin (String username) throws NullPointerException, SQLException,SQLServerException {
        ResultSet rs = null;
        PreparedStatement prpstm= null;

        try {
            prpstm = connection.prepareStatement("SELECT LOGIN FROM TB_USUARIO WHERE LOGIN=?");
            prpstm.setString(1, username);

            rs = prpstm.executeQuery();
            //System.out.println("Deu ruim, rs é :"+rs.getString(1));
            if(!(rs.next())){  //Se não obtiver registros então não existe login com esse nome de usuário então pode retornar verdadeiro significando que este é válido
                return true;
            }else{
                return false;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();

        } catch(SQLServerException exc){
            exc.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean validateLogin (String username, String password) throws NullPointerException, SQLException,SQLServerException {
        ResultSet rs = null;
        PreparedStatement prpstm = null;
        try {
            prpstm = connection.prepareStatement("SELECT * FROM TB_USUARIO WHERE LOGIN=?");
            prpstm.setString(1, username);
            rs = prpstm.executeQuery();

            if(rs.next()){
                if (BCrypt.checkpw(password, rs.getString("PASSWORD"))) {
                    return true;
                }
            }else{
                return false;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();

        } catch(SQLServerException exc){
            exc.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public ResultSet getUser(String username, String password){
        ResultSet rs = null;
        PreparedStatement prpstm = null;
        User user = null;
        try {
            prpstm = connection.prepareStatement("SELECT * FROM TB_USUARIO WHERE LOGIN=?");
            prpstm.setString(1, username);
            rs = prpstm.executeQuery();
            if(rs.next()){
                if(BCrypt.checkpw(password, rs.getString("PASSWORD"))) {
                    return rs;
                }
            }else{
                return null;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();

        } catch(SQLServerException exc){
            exc.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}