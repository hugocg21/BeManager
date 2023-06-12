package com.hugocg21.bemanager.Clases;

public class Ejercicio {
    private String tituloEjercicio, descripcionEjercicio;
    private int duracionEjercicio;

    public Ejercicio(String tituloEjercicio, String descripcionEjercicio, int duracionEjercicio) {
        this.tituloEjercicio = tituloEjercicio;
        this.descripcionEjercicio = descripcionEjercicio;
        this.duracionEjercicio = duracionEjercicio;
    }

    public Ejercicio() {
    }

    public String getTituloEjercicio() {
        return tituloEjercicio;
    }

    public void setTituloEjercicio(String tituloEjercicio) {
        this.tituloEjercicio = tituloEjercicio;
    }

    public String getDescripcionEjercicio() {
        return descripcionEjercicio;
    }

    public void setDescripcionEjercicio(String descripcionEjercicio) {
        this.descripcionEjercicio = descripcionEjercicio;
    }

    public int getDuracionEjercicio() {
        return duracionEjercicio;
    }

    public void setDuracionEjercicio(int duracionEjercicio) {
        this.duracionEjercicio = duracionEjercicio;
    }
}
