package com.hugocg21.bemanager.Clases;

//Clase que define como es un Entrenamiento
public class Entrenamiento {
    //Creamos los Strings para almacenar la fecha y hora del entrenamiento
    private String fechaEntrenamiento, horaEntrenamiento;

    //Constructor que define como es un objeto Entrenamiento e inicializa los datos
    public Entrenamiento(String fechaEntrenamiento, String horaEntrenamiento) {
        this.fechaEntrenamiento = fechaEntrenamiento;
        this.horaEntrenamiento = horaEntrenamiento;
    }

    //Constructor de Entrenamiento sin datos
    public Entrenamiento() {
    }


    //Métodos Get y Set de la fecha del entrenamiento
    public String getFechaEntrenamiento() {
        return fechaEntrenamiento;
    }

    public void setFechaEntrenamiento(String fechaEntrenamiento) {
        this.fechaEntrenamiento = fechaEntrenamiento;
    }


    //Métodos Get y Set de la hora del entrenamiento
    public String getHoraEntrenamiento() {
        return horaEntrenamiento;
    }

    public void setHoraEntrenamiento(String horaEntrenamiento) {
        this.horaEntrenamiento = horaEntrenamiento;
    }
}
