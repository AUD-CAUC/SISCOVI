package br.jus.stj.siscovi.model;
import java.sql.Date;

public class UsuarioModel {

    private int codigo;
    private String perfil;
    private String nome;
    private String login;
    private String loginAtualizacao;
    private Date ultimaAtualizacao;

    public UsuarioModel(int codigo, String nome, String login, String loginAtualizacao, Date ultimaAtualizacao) {

        this.codigo = codigo;
        this.codigo = codigo;
        this.nome = nome;
        this.login = login;
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

}
