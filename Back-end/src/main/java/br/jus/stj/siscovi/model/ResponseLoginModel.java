package br.jus.stj.siscovi.model;

public class ResponseLoginModel {
    private boolean success;
    private DataModel data;


    public DataModel getData() {
        return data;
    }

    public boolean getSuccess(){
        return success;
    }

    public void setData(DataModel data) {
        this.data = data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
