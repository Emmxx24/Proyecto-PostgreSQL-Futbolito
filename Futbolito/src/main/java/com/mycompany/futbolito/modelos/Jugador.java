package com.mycompany.futbolito.modelos;

public class Jugador {
    private long idJugador;
    private long idParticipante;
    private String posicion;
    private int numero;
    private String tipoSangre;
    private int acumuladorAmarillas;
    private String estado; 

    public long getIdJugador() {
        return idJugador;
    }

    public void setIdJugador(long idJugador) {
        this.idJugador = idJugador;
    }

    public long getIdParticipante() {
        return idParticipante;
    }

    public void setIdParticipante(long idParticipante) {
        this.idParticipante = idParticipante;
    }

    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getTipoSangre() {
        return tipoSangre;
    }

    public void setTipoSangre(String tipoSangre) {
        this.tipoSangre = tipoSangre;
    }

    public int getAcumuladorAmarillas() {
        return acumuladorAmarillas;
    }

    public void setAcumuladorAmarillas(int acumuladorAmarillas) {
        this.acumuladorAmarillas = acumuladorAmarillas;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
