package br.jus.stj.siscovi.model;


import java.util.Date;

public class User {
    private int id;
    private String username;
    private AreaModel area;
    private int area_id;
    private boolean active;
    private Date created;
    private Date modified;
    private AreaModel areaPai;
    private AreaModel areaChild;
    private PerfilModel perfil;

    public PerfilModel getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilModel perfil) {
        this.perfil = perfil;
    }

    public int getId(){
        return id;
    }

    public AreaModel getArea() {
        return area;
    }

    public AreaModel getAreaChild() {
        return areaChild;
    }

    public AreaModel getAreaPai() {
        return areaPai;
    }

    public boolean getActive() {
        return active;
    }

    public Date getCreated() {
        return created;
    }

    public Date getModified() {
        return modified;
    }

    public int getArea_id() {
        return area_id;
    }

    public String getUsername() {
        return username;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setArea(AreaModel area) {
        this.area = area;
    }

    public void setArea_id(int area_id) {
        this.area_id = area_id;
    }

    public void setAreaChild(AreaModel areaChild) {
        this.areaChild = areaChild;
    }

    public void setAreaPai(AreaModel areaPai) {
        this.areaPai = areaPai;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
