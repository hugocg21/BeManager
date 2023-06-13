package com.hugocg21.bemanager.Clases;

//Clase que define como es un Usuario
public class Usuario {
    //Creamos los String para almacenar el correo, el nombre de usuario y la contraseña del usuario
    private String correoElectronico, nombreUsuario, password;

    //Constructor que define como es un objeto Usuario e inicializa los datos
    public Usuario(String correoElectronico, String nombreUsuario, String password) {
        this.correoElectronico = correoElectronico;
        this.nombreUsuario = nombreUsuario;
        this.password = password;
    }

    //Constructor de Usuario sin datos
    public Usuario() {
    }


    //Métodos Get y Set del correo electrónico del usuario
    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }


    //Métodos Get y Set del nombre de usuario
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }


    //Métodos Get y Set de la contraseña del usuario
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
