package com.mycompany.futbolito.modelos;

import java.sql.Date;
import java.sql.Time;

public class Partido {
    private long idPartido;
    private long idArbitro;
    private long idJornada;
    private long idLugar;
    private long idLocal;
    private long idVisitante;
    private Date fecha;
    private Time horaInicio;
    private String estado;

    public long getIdPartido() {
        return idPartido;
    }

    public void setIdPartido(long idPartido) {
        this.idPartido = idPartido;
    }

    public long getIdArbitro() {
        return idArbitro;
    }

    public void setIdArbitro(long idArbitro) {
        this.idArbitro = idArbitro;
    }

    public long getIdJornada() {
        return idJornada;
    }

    public void setIdJornada(long idJornada) {
        this.idJornada = idJornada;
    }

    public long getIdLugar() {
        return idLugar;
    }

    public void setIdLugar(long idLugar) {
        this.idLugar = idLugar;
    }

    public long getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(long idLocal) {
        this.idLocal = idLocal;
    }

    public long getIdVisitante() {
        return idVisitante;
    }

    public void setIdVisitante(long idVisitante) {
        this.idVisitante = idVisitante;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Time getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Time horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
