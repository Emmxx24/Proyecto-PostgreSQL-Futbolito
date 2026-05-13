package com.mycompany.futbolito.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import com.mycompany.futbolito.conexion.ClaseConexion;

public class DetalleEquipoDAO {

    // Ya no traemos el torneo, solo el Equipo
    public static class ItemEquipo {
        public long idEquipo;
        public String texto;
        public ItemEquipo(long idEquipo, String texto) { 
            this.idEquipo = idEquipo; this.texto = texto; 
        }
    }

    public static class ItemJugador {
        public long idJugador;
        public String texto;
        public ItemJugador(long idJugador, String texto) { 
            this.idJugador = idJugador; this.texto = texto; 
        }
    }

    public List<ItemEquipo> obtenerEquiposCombo() throws Exception {
        List<ItemEquipo> lista = new ArrayList<>();
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        // Cargamos todos los equipos sin importar en qué torneo estén
        String sql = "SELECT IdEquipo, NombreEquipo FROM Club.Equipo ORDER BY NombreEquipo";
        PreparedStatement ps = cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            lista.add(new ItemEquipo(rs.getLong("IdEquipo"), rs.getString("NombreEquipo")));
        }
        return lista;
    }

    public List<ItemJugador> obtenerJugadoresCombo() throws Exception {
        List<ItemJugador> lista = new ArrayList<>();
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        String sql = "SELECT j.IdJugador, CONCAT(j.IdJugador, ' - ', p.NombreParticipante, ' [', j.Posicion, ' - ', j.Numero, ']') AS NombreJugador " +
                     "FROM Persona.Jugador j " +
                     "INNER JOIN Persona.Participante p ON j.IdParticipante = p.IdParticipante " +
                     "ORDER BY p.IdParticipante";
        PreparedStatement ps = cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            lista.add(new ItemJugador(rs.getLong("IdJugador"), rs.getString("NombreJugador")));
        }
        return lista;
    }

    public DefaultTableModel obtenerModeloDetalleEquipo() throws Exception {
        DefaultTableModel modelo = new DefaultTableModel();
        
        modelo.addColumn("IdEquipo"); // Oculta 0
        modelo.addColumn("IdJugador"); // Oculta 1
        modelo.addColumn("Equipo");
        modelo.addColumn("Jugador");

        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        // Sin duplicados, una sola fila por jugador en el equipo
        String sql = "SELECT de.IdEquipo, de.IdJugador, e.NombreEquipo, " +
                     "CONCAT(j.IdJugador, ' ', p.NombreParticipante, ' [', j.Posicion, ' - ', j.Numero, ']') AS DetalleJugador " +
                     "FROM Club.DetalleEquipo de " +
                     "INNER JOIN Club.Equipo e ON de.IdEquipo = e.IdEquipo " +
                     "INNER JOIN Persona.Jugador j ON de.IdJugador = j.IdJugador " +
                     "INNER JOIN Persona.Participante p ON j.IdParticipante = p.IdParticipante " +
                     "ORDER BY e.NombreEquipo, j.IdJugador";
                     
        PreparedStatement ps = cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            modelo.addRow(new Object[]{
                rs.getLong("IdEquipo"),
                rs.getLong("IdJugador"),
                rs.getString("NombreEquipo"),
                rs.getString("DetalleJugador")
            });
        }
        return modelo;
    }

    public String validarInscripcion(long idEquipo, long idJugador, long excluirEquipoAnterior, long excluirJugadorAnterior) throws Exception {
        boolean esModificacion = (excluirEquipoAnterior != -1 && excluirJugadorAnterior != -1);
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        // 1. Validar duplicados exactos (el mismo jugador en el mismo equipo)
        String qDup = esModificacion ? 
            "SELECT COUNT(*) FROM Club.DetalleEquipo WHERE IdEquipo=? AND IdJugador=? AND NOT (IdEquipo=? AND IdJugador=?)" : 
            "SELECT COUNT(*) FROM Club.DetalleEquipo WHERE IdEquipo=? AND IdJugador=?";
        try (PreparedStatement ps = cn.prepareStatement(qDup)) {
            ps.setLong(1, idEquipo); ps.setLong(2, idJugador);
            if (esModificacion) { ps.setLong(3, excluirEquipoAnterior); ps.setLong(4, excluirJugadorAnterior); }
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return "Este jugador ya está inscrito en el equipo seleccionado.";
        }

        // 2. Validar que el dorsal no se repita en el mismo equipo
        String qDorsal = esModificacion ? 
            "SELECT COUNT(*) FROM Club.DetalleEquipo de INNER JOIN Persona.Jugador j ON de.IdJugador=j.IdJugador WHERE de.IdEquipo=? AND j.Numero=(SELECT Numero FROM Persona.Jugador WHERE IdJugador=?) AND de.IdJugador<>? AND NOT (de.IdEquipo=? AND de.IdJugador=?)" : 
            "SELECT COUNT(*) FROM Club.DetalleEquipo de INNER JOIN Persona.Jugador j ON de.IdJugador=j.IdJugador WHERE de.IdEquipo=? AND j.Numero=(SELECT Numero FROM Persona.Jugador WHERE IdJugador=?) AND de.IdJugador<>?";
        try (PreparedStatement ps = cn.prepareStatement(qDorsal)) {
            ps.setLong(1, idEquipo); ps.setLong(2, idJugador); ps.setLong(3, idJugador);
            if (esModificacion) { ps.setLong(4, excluirEquipoAnterior); ps.setLong(5, excluirJugadorAnterior); }
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return "Ya existe un jugador con el mismo número de dorsal en este equipo.";
        }

        return null; // Si pasa las validaciones, regresa null
    }

    public void insertarDetalle(long idEquipo, long idJugador) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();
        String sql = "INSERT INTO Club.DetalleEquipo (IdEquipo, IdJugador) VALUES (?, ?)";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idEquipo);
        ps.setLong(2, idJugador);
        ps.executeUpdate();
    }

    public void modificarDetalle(long idEquipoNuevo, long idJugadorNuevo, long idEquipoViejo, long idJugadorViejo) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();
        String sql = "UPDATE Club.DetalleEquipo SET IdEquipo = ?, IdJugador = ? WHERE IdEquipo = ? AND IdJugador = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idEquipoNuevo);
        ps.setLong(2, idJugadorNuevo);
        ps.setLong(3, idEquipoViejo);
        ps.setLong(4, idJugadorViejo);
        ps.executeUpdate();
    }

    public void eliminarDetalle(long idEquipo, long idJugador) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();
        String sql = "DELETE FROM Club.DetalleEquipo WHERE IdEquipo = ? AND IdJugador = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idEquipo);
        ps.setLong(2, idJugador);
        ps.executeUpdate();
    }
}