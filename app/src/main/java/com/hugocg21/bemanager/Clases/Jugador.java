package com.hugocg21.bemanager.Clases;

public class Jugador {
    String nombreJugador, apellidosJugador, equipoJugador, posicionJugador;
    int dorsalJugador, posicionJugadorNumero;

    public Jugador(String nombreJugador, String apellidosJugador, String equipoJugador, String posicionJugador, int dorsalJugador, int posicionJugadorNumero) {
        this.nombreJugador = nombreJugador;
        this.apellidosJugador = apellidosJugador;
        this.equipoJugador = equipoJugador;
        this.posicionJugador = posicionJugador;
        this.dorsalJugador = dorsalJugador;
        this.posicionJugadorNumero = posicionJugadorNumero;
    }

    public Jugador() {
    }

    public String getNombreJugador() {
        return nombreJugador;
    }

    public void setNombreJugador(String nombreJugador) {
        this.nombreJugador = nombreJugador;
    }

    public String getApellidosJugador() {
        return apellidosJugador;
    }

    public void setApellidosJugador(String apellidosJugador) {
        this.apellidosJugador = apellidosJugador;
    }

    public String getEquipoJugador() {
        return equipoJugador;
    }

    public void setEquipoJugador(String equipoJugador) {
        this.equipoJugador = equipoJugador;
    }

    public String getPosicionJugador() {
        return posicionJugador;
    }

    public void setPosicionJugador(String posicionJugador) {
        this.posicionJugador = posicionJugador;
    }

    public int getDorsalJugador() {
        return dorsalJugador;
    }

    public void setDorsalJugador(int dorsalJugador) {
        this.dorsalJugador = dorsalJugador;
    }

    public int getPosicionJugadorNumero() {
        return posicionJugadorNumero;
    }

    public void setPosicionJugadorNumero(int posicionJugadorNumero) {
        this.posicionJugadorNumero = posicionJugadorNumero;
    }
}
