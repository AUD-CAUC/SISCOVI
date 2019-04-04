package br.jus.stj.siscovi.model;

import java.sql.Date;
import java.sql.Timestamp;

public class RegistroUsuario {

    private int pCod;
    private String pCodPerfil;
    private String pNome;
    private String pLogin;
    private String pLoginAtualizacao;
    private Date pDataAtualizacao;

    public RegistroUsuario(int pCod,
                           String pCodPerfil,
                           String pNome,
                           String pLogin,
                           String pLoginAtualizacao,
                           Date pDataAtualizacao) {

        this.pCod = pCod;
        this.pCodPerfil = pCodPerfil;
        this.pNome = pNome;
        this.pLogin = pLogin;
        this.pLoginAtualizacao = pLoginAtualizacao;
        this.pDataAtualizacao = pDataAtualizacao;

    }

    public int getpCod() { return pCod; }

    public String getpCodPerfil() { return pCodPerfil; }

    public String getpNome() { return pNome; }

    public String getpLogin() { return pLogin; }

    public String getpLoginAtualizacao() { return pLoginAtualizacao; }

    public Date getpDataAtualizacao() { return pDataAtualizacao; }

}
