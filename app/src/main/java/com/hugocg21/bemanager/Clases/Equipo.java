package com.hugocg21.bemanager.Clases;

public class Equipo {
    //Atributos de un equipo
    private String nombreEquipo, categoriaEquipo, sedeEquipo;
    private int numJugadoresEquipo;

    //Constructor que inicializa y crea un objeto de tipo Equipo
    public Equipo(String nombreEquipo, String categoriaEquipo, String sedeEquipo, int numJugadoresEquipo) {
        this.nombreEquipo = nombreEquipo;
        this.categoriaEquipo = categoriaEquipo;
        this.sedeEquipo = sedeEquipo;
        this.numJugadoresEquipo = numJugadoresEquipo;
    }

    public Equipo(){}

    //Getters y setters de todos los atributos de la clase
    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public String getCategoriaEquipo() {
        return categoriaEquipo;
    }

    public void setCategoriaEquipo(String categoriaEquipo) {
        this.categoriaEquipo = categoriaEquipo;
    }

    public String getSedeEquipo() {
        return sedeEquipo;
    }

    public void setSedeEquipo(String sedeEquipo) {
        this.sedeEquipo = sedeEquipo;
    }

    public int getNumJugadoresEquipo() {
        return numJugadoresEquipo;
    }

    public void setNumJugadoresEquipo(int numJugadoresEquipo) {
        this.numJugadoresEquipo = numJugadoresEquipo;
    }
}
