package com.mycompany.futbolito.modelos;

public class Jornada {
    private long idJornada;
    private long idTorneo;
    private int numeroJornada;

    public long getIdJornada() {
        return idJornada;
    }

    public void setIdJornada(long idJornada) {
        this.idJornada = idJornada;
    }

    public long getIdTorneo() {
        return idTorneo;
    }

    public void setIdTorneo(long idTorneo) {
        this.idTorneo = idTorneo;
    }

    public int getNumeroJornada() {
        return numeroJornada;
    }

    public void setNumeroJornada(int numeroJornada) {
        this.numeroJornada = numeroJornada;
    }
}
