package com.hugocg21.bemanager.Clases;

public class Usuario {
    String correoElectronico, nombreUsuario, password;

    public Usuario(String correoElectronico, String nombreUsuario, String password) {
        this.correoElectronico = correoElectronico;
        this.nombreUsuario = nombreUsuario;
        this.password = password;
    }

    public Usuario() {
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
