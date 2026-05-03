package com.mycompany.futbolito.modelos;

public class Tarjeta {
    private long idTarjeta;
    private long idJugador;
    private long idPartido;
    private String tipoTarjeta;
    private int minuto;

    public long getIdTarjeta() {
        return idTarjeta;
    }

    public void setIdTarjeta(long idTarjeta) {
        this.idTarjeta = idTarjeta;
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

    public String getTipoTarjeta() {
        return tipoTarjeta;
    }

    public void setTipoTarjeta(String tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }

    public int getMinuto() {
        return minuto;
    }

    public void setMinuto(int minuto) {
        this.minuto = minuto;
    }
}
