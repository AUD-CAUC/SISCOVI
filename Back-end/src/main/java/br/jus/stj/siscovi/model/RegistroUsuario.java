package br.jus.stj.siscovi.model;

import java.sql.Timestamp;

public class RegistroUsuario {

    private int pCod;
    private int pCodPerfil;
    private String pNome;
    private String pLogin;
    private String pPassword;
    private String pLoginAtualizacao;
    private Timestamp pDataAtualizacao;

    public RegistroUsuario(int pCod,
                           int pCodPerfil,
                           String pNome,
                           String pLogin,
                           String pPassword,
                           String pLoginAtualizacao,
                           Timestamp pDataAtualizacao) {

        this.pCod = pCod;
        this.pCodPerfil = pCodPerfil;
        this.pNome = pNome;
        this.pLogin = pLogin;
        this.pPassword = pPassword;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod() { return pCod; }

    public int getpCodPerfil() { return pCodPerfil; }

    public String getpNome() { return pNome; }

    public String getpLogin() { return pLogin; }

    public String getpPassword() { return pPassword; }

    public String getpLoginAtualizacao() { return pLoginAtualizacao; }

    public Timestamp getpDataAtualizacao() { return pDataAtualizacao; }

}
