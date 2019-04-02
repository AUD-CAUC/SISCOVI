package br.jus.stj.siscovi.model;
import java.sql.Date;

public class UsuarioModel {

    private int codigo;
    private String nome;
    private String perfil;
    private String login;
    private String password;
    private String loginAtualizacao;
    private Date ultimaAtualizacao;

    public UsuarioModel(int codigo, String perfil, String nome, String login, String password, String loginAtualizacao, Date ultimaAtualizacao) {
           this.nome = nome;
           this.perfil = perfil;
           this.codigo = codigo;
           this.login = login;
           this.password = password;
           this.loginAtualizacao = loginAtualizacao;
           this.ultimaAtualizacao = ultimaAtualizacao;
    }

    public String getNome() {
        return nome;
    }
    public String getLogin() {
        return login;
    }
    public String getLoginAtualizacao() {
        return loginAtualizacao;
    }
    public Date getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }
    public String getPerfil() {
        return perfil;
    }
    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }
    public int getCodigo() {
        return codigo;
    }

    public String getPassword() {
        return password;
    }

}
