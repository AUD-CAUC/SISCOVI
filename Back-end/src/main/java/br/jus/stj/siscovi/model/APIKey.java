package br.jus.stj.siscovi.model;

public class APIKey {

    public APIKey(){
        secret = "SISCOVI - CAUC - AUD";
    }
    private String secret;

    public String getSecret(){
        return secret;
    }
}
