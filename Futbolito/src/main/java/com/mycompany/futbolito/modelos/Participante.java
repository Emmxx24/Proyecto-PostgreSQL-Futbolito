package com.mycompany.futbolito.modelos;

import java.sql.Date;

public class Participante {
    private long idParticipante;
    private String nombre;
    private String genero;
    private String telefono;
    private String correo;
    private Date fechaNacimiento;

    public long getIdParticipante() {
        return idParticipante;
    }
    
    public void setIdParticipante(long idParticipante){
        this.idParticipante = idParticipante;
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
}