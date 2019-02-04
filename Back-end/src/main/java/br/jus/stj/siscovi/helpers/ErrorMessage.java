package br.jus.stj.siscovi.helpers;

public class ErrorMessage {
    public String error;

    public static ErrorMessage handleError(Exception e) {
        e.printStackTrace();
        System.err.println(e.toString());
        System.err.println(e.getStackTrace());
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.error = e.getMessage();
        return errorMessage;
    }
}
