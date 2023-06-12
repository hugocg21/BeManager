package com.hugocg21.bemanager.Clases;

public class Entrenamiento {
    String fechaEntrenamiento, horaEntrenamiento;

    public Entrenamiento(String fechaEntrenamiento, String horaEntrenamiento) {
        this.fechaEntrenamiento = fechaEntrenamiento;
        this.horaEntrenamiento = horaEntrenamiento;
    }

    public Entrenamiento() {
    }

    public String getFechaEntrenamiento() {
        return fechaEntrenamiento;
    }

    public void setFechaEntrenamiento(String fechaEntrenamiento) {
        this.fechaEntrenamiento = fechaEntrenamiento;
    }

    public String getHoraEntrenamiento() {
        return horaEntrenamiento;
    }

    public void setHoraEntrenamiento(String horaEntrenamiento) {
        this.horaEntrenamiento = horaEntrenamiento;
    }
}
