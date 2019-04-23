package br.jus.stj.siscovi.model;

public class CadastroUsuarioModel {
    private UsuarioModel usuario;
    private String password;
    private String newPassword;
    private String currentUser;


    public UsuarioModel getUsuario() {
        return usuario;
    }

    public String getPassword() {
        return password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getCurrentUser() {
        return currentUser;
    }

}
