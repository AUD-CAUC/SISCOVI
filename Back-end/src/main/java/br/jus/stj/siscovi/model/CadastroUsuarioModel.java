package br.jus.stj.siscovi.model;

public class CadastroUsuarioModel {
    private UsuarioModel usuario;
    private String password;
    private String currentUser;
    public UsuarioModel getUsuario() {
        return usuario;
    }

    public String getPassword() {
        return password;
    }

    public String getCurrentUser() {
        return currentUser;
    }
}
