package com.hugocg21.bemanager.Clases;

//Clase que define como es un Equipo
public class Equipo {
    //Creamos los Strings para almacenar el nombre, categoría y sede del equipo
    private String nombreEquipo, categoriaEquipo, sedeEquipo;

    //Constructor que define como es un objeto Equipo e inicializa los datos
    public Equipo(String nombreEquipo, String categoriaEquipo, String sedeEquipo, int numJugadoresEquipo) {
        this.nombreEquipo = nombreEquipo;
        this.categoriaEquipo = categoriaEquipo;
        this.sedeEquipo = sedeEquipo;
    }

    //Constructor de Equipo sin datos
    public Equipo() {
    }


    //Métodos Get y Set del nombre del equipo
    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }


    //Métodos Get y Set de la categoría del equipo
    public String getCategoriaEquipo() {
        return categoriaEquipo;
    }

    public void setCategoriaEquipo(String categoriaEquipo) {
        this.categoriaEquipo = categoriaEquipo;
    }


    //Métodos Get y Set de la sede del equipo
    public String getSedeEquipo() {
        return sedeEquipo;
    }

    public void setSedeEquipo(String sedeEquipo) {
        this.sedeEquipo = sedeEquipo;
    }
}
