package com.mycompany.futbolito.modelos;

public class Gol {
    private long idGol;
    private long idJugador;
    private long idPartido;
    private int minuto;

    public long getIdGol() {
        return idGol;
    }

    public void setIdGol(long idGol) {
        this.idGol = idGol;
    }

    public long getIdJugador() {
        return idJugador;
    }

    public void setIdJugador(long idJugador) {
        this.idJugador = idJugador;
    }

    public long getIdPartido() {
        return idPartido;
    }

    public void setIdPartido(long idPartido) {
        this.idPartido = idPartido;
    }

    public int getMinuto() {
        return minuto;
    }

    public void setMinuto(int minuto) {
        this.minuto = minuto;
    }
}
