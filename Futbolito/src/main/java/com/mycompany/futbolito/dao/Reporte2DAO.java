package com.mycompany.futbolito.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import com.mycompany.futbolito.conexion.ClaseConexion;

public class Reporte2DAO {

    public static class ItemCombo {
        public long id;
        public String texto;
        public ItemCombo(long id, String texto) { this.id = id; this.texto = texto; }
    }

    // Cargar Torneos ordenados
    public List<ItemCombo> obtenerTorneosCombo() throws Exception {
        List<ItemCombo> lista = new ArrayList<>();
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        String sql = "SELECT IdTorneo, NombreTorneo FROM Juego.Torneo ORDER BY NombreTorneo";
        PreparedStatement ps = cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            lista.add(new ItemCombo(rs.getLong("IdTorneo"), rs.getString("NombreTorneo")));
        }
        return lista;
    }

    // Ejecutar el Reporte con Subconsulta 
    public DefaultTableModel obtenerReporte(long idTorneo, String tipoTarjeta, int cantidadMinima) throws Exception {
        DefaultTableModel modelo = new DefaultTableModel();
        
        modelo.addColumn("Nombre del jugador");
        modelo.addColumn("Nombre del equipo");
        modelo.addColumn("Cantidad de tarjetas");

        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        
        String sql = "SELECT NombreJugador AS \"Nombre del jugador\", NombreEquipo AS \"Nombre del equipo\", TotalTarjetas AS \"Cantidad de tarjetas\" " +
                     "FROM( " +
                     "    SELECT par.NombreParticipante AS NombreJugador, e.NombreEquipo, COUNT(tar.IdTarjeta) AS TotalTarjetas " +
                     "    FROM Persona.Participante par " +
                     "    INNER JOIN Persona.Jugador j ON j.IdParticipante = par.IdParticipante " +
                     "    INNER JOIN Evento.Tarjeta tar ON tar.IdJugador = j.IdJugador " +
                     "    INNER JOIN Evento.Partido p ON p.IdPartido = tar.IdPartido " +
                     "    INNER JOIN Juego.Jornada jor ON jor.IdJornada = p.IdJornada " +
                     "    INNER JOIN Juego.Torneo t ON t.IdTorneo = jor.IdTorneo " +
                     "    INNER JOIN Club.Equipo e ON (e.IdEquipo = p.IdLocal OR e.IdEquipo = p.IdVisitante) " +
                     "    INNER JOIN Club.DetalleEquipo de ON de.IdEquipo = e.IdEquipo AND de.IdJugador = j.IdJugador " +
                     "    WHERE t.IdTorneo = ? AND tar.TipoTarjeta = ? " +
                     "    GROUP BY j.IdJugador, par.NombreParticipante, e.NombreEquipo " +
                     ") AS Indisciplinados " +
                     "WHERE TotalTarjetas >= ? " +
                     "ORDER BY TotalTarjetas DESC";

        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idTorneo);
        ps.setString(2, tipoTarjeta);
        ps.setInt(3, cantidadMinima);
        
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            modelo.addRow(new Object[]{
                rs.getString(1), // Nombre del jugador
                rs.getString(2), // Nombre del equipo
                rs.getInt(3)     // Cantidad de tarjetas
            });
        }
        return modelo;
    }
}