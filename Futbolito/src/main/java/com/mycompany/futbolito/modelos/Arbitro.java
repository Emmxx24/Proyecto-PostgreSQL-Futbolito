package com.mycompany.futbolito.modelos;

public class Arbitro {
    private long idArbitro;
    private long idParticipante;
    private String cedulaArbitro;

    public long getIdArbitro() {
        return idArbitro;
    }

    public void setIdArbitro(long idArbitro) {
        this.idArbitro = idArbitro;
    }

    public long getIdParticipante() {
        return idParticipante;
    }

    public void setIdParticipante(long idParticipante) {
        this.idParticipante = idParticipante;
    }

    public String getCedulaArbitro() {
        return cedulaArbitro;
    }

    public void setCedulaArbitro(String CedulaArbitro) {
        this.cedulaArbitro = CedulaArbitro;
    }
}
