package com.mycompany.futbolito.modelos;

import java.sql.Time;

public class ResultadoPartido {
    private long idResultado;
    private long idPartido;
    private int golesLocal;
    private int golesVisitante;
    private Time horaFin;

    public long getIdResultado() {
        return idResultado;
    }

    public void setIdResultado(long idResultado) {
        this.idResultado = idResultado;
    }

    public long getIdPartido() {
        return idPartido;
    }

    public void setIdPartido(long idPartido) {
        this.idPartido = idPartido;
    }

    public int getGolesLocal() {
        return golesLocal;
    }

    public void setGolesLocal(int golesLocal) {
        this.golesLocal = golesLocal;
    }

    public int getGolesVisitante() {
        return golesVisitante;
    }

    public void setGolesVisitante(int golesVisitante) {
        this.golesVisitante = golesVisitante;
    }

    public Time getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(Time horaFin) {
        this.horaFin = horaFin;
    }
}
