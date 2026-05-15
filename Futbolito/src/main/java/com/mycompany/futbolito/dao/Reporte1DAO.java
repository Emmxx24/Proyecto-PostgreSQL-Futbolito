package com.mycompany.futbolito.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import com.mycompany.futbolito.conexion.ClaseConexion;

public class Reporte1DAO {

    public static class ItemCombo {
        public long id;
        public String texto;
        public ItemCombo(long id, String texto) { this.id = id; this.texto = texto; }
    }

    public List<ItemCombo> obtenerEquiposCombo() throws Exception {
        List<ItemCombo> lista = new ArrayList<>();
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        
        String sql = "SELECT IdEquipo, NombreEquipo FROM Club.Equipo ORDER BY NombreEquipo";
        PreparedStatement ps = cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        while(rs.next()) {
            lista.add(new ItemCombo(rs.getLong("IdEquipo"), rs.getString("NombreEquipo")));
        }
        return lista;
    }

    public DefaultTableModel obtenerReporte(long idEquipo) throws Exception {
        DefaultTableModel modelo = new DefaultTableModel();
        
        modelo.addColumn("ID Jugador");
        modelo.addColumn("Nombre de jugador");
        modelo.addColumn("Nombre del equipo");
        modelo.addColumn("Cantidad de goles");

        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        
        String sql = "SELECT j.IdJugador, par.NombreParticipante AS \"Nombre de jugador\", e.NombreEquipo AS \"Nombre del equipo\", " +
                     "COUNT(g.IdGol) AS \"Cantidad de goles\" " +
                     "FROM Persona.Jugador j " +
                     "INNER JOIN Persona.Participante par ON par.IdParticipante = j.IdParticipante " +
                     "INNER JOIN Evento.Gol g ON g.IdJugador = j.IdJugador " +
                     "INNER JOIN Evento.Partido p ON p.IdPartido = g.IdPartido " +
                     "INNER JOIN Club.Equipo e ON e.IdEquipo = p.IdLocal OR e.IdEquipo = p.IdVisitante " +
                     "INNER JOIN Club.DetalleEquipo de ON de.IdJugador = j.IdJugador AND de.IdEquipo = e.IdEquipo " +
                     "WHERE e.IdEquipo = ? " +
                     "GROUP BY j.IdJugador, par.NombreParticipante, e.NombreEquipo " +
                     "ORDER BY \"Cantidad de goles\" DESC";

        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idEquipo);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            modelo.addRow(new Object[]{
                rs.getLong(1), // IdJugador
                rs.getString(2), // Nombre de jugador
                rs.getString(3), // Nombre del equipo
                rs.getInt(4)     // Cantidad de goles
            });
        }
        return modelo;
    }
}