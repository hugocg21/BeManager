package com.hugocg21.bemanager.Clases;

//Clase que define como es un Ejercicio
public class Ejercicio {
    //Creamos los Strings e int para almacenar el título, la descripción y la duración del ejercicio
    private String tituloEjercicio, descripcionEjercicio;
    private int duracionEjercicio;

    //Constructor que define como es un objeto Ejercicio e inicializa los datos
    public Ejercicio(String tituloEjercicio, String descripcionEjercicio, int duracionEjercicio) {
        this.tituloEjercicio = tituloEjercicio;
        this.descripcionEjercicio = descripcionEjercicio;
        this.duracionEjercicio = duracionEjercicio;
    }

    //Constructor de Ejercicio sin datos
    public Ejercicio() {
    }


    //Métodos Get y Set del título del ejercicio
    public String getTituloEjercicio() {
        return tituloEjercicio;
    }

    public void setTituloEjercicio(String tituloEjercicio) {
        this.tituloEjercicio = tituloEjercicio;
    }


    //Métodos Get y Set de la descripción del ejercicio
    public String getDescripcionEjercicio() {
        return descripcionEjercicio;
    }

    public void setDescripcionEjercicio(String descripcionEjercicio) {
        this.descripcionEjercicio = descripcionEjercicio;
    }


    //Métodos Get y Set de la duración del ejercicio
    public int getDuracionEjercicio() {
        return duracionEjercicio;
    }

    public void setDuracionEjercicio(int duracionEjercicio) {
        this.duracionEjercicio = duracionEjercicio;
    }
}
