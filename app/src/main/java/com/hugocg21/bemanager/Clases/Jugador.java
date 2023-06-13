package com.hugocg21.bemanager.Clases;

//Clase que define como es un Jugador
public class Jugador {
    //Creamos los String e ints para almacenar el nombre, apellidos, equipo, posicion, dorsal y el número entero de la posición del jugador
    private String nombreJugador, apellidosJugador, equipoJugador, posicionJugador;
    private int dorsalJugador, posicionJugadorNumero;

    //Constructor que define como es un objeto Jugador e inicializa los datos
    public Jugador(String nombreJugador, String apellidosJugador, String equipoJugador, String posicionJugador, int dorsalJugador, int posicionJugadorNumero) {
        this.nombreJugador = nombreJugador;
        this.apellidosJugador = apellidosJugador;
        this.equipoJugador = equipoJugador;
        this.posicionJugador = posicionJugador;
        this.dorsalJugador = dorsalJugador;
        this.posicionJugadorNumero = posicionJugadorNumero;
    }

    //Constructor de Jugador sin datos
    public Jugador() {
    }


    //Métodos Get y Set del nombre del jugador
    public String getNombreJugador() {
        return nombreJugador;
    }

    public void setNombreJugador(String nombreJugador) {
        this.nombreJugador = nombreJugador;
    }


    //Métodos Get y Set de los apellidos del jugador
    public String getApellidosJugador() {
        return apellidosJugador;
    }

    public void setApellidosJugador(String apellidosJugador) {
        this.apellidosJugador = apellidosJugador;
    }


    //Métodos Get y Set del equipo del jugador
    public String getEquipoJugador() {
        return equipoJugador;
    }

    public void setEquipoJugador(String equipoJugador) {
        this.equipoJugador = equipoJugador;
    }


    //Métodos Get y Set de la posición del jugador
    public String getPosicionJugador() {
        return posicionJugador;
    }

    public void setPosicionJugador(String posicionJugador) {
        this.posicionJugador = posicionJugador;
    }


    //Métodos Get y Set del dorsal del jugador
    public int getDorsalJugador() {
        return dorsalJugador;
    }

    public void setDorsalJugador(int dorsalJugador) {
        this.dorsalJugador = dorsalJugador;
    }


    //Métodos Get y Set del valor numérico de la posición del jugador
    public int getPosicionJugadorNumero() {
        return posicionJugadorNumero;
    }

    public void setPosicionJugadorNumero(int posicionJugadorNumero) {
        this.posicionJugadorNumero = posicionJugadorNumero;
    }
}
