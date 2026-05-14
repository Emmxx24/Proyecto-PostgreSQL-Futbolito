package com.mycompany.futbolito.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import com.mycompany.futbolito.conexion.ClaseConexion;

public class TarjetaDAO {

    public static class ItemCombo {
        public long id;
        public String texto;
        public ItemCombo(long id, String texto) { this.id = id; this.texto = texto; }
    }

    public DefaultTableModel obtenerModeloTarjetas() throws Exception {
        DefaultTableModel modelo = new DefaultTableModel();
        
        modelo.addColumn("IdTarjeta");    // 0 Oculto
        modelo.addColumn("TipoTarjeta");  // 1
        modelo.addColumn("IdJugador");    // 2 Oculto
        modelo.addColumn("IdPartido");    // 3 Oculto
        modelo.addColumn("Jugador");      // 4
        modelo.addColumn("DatosPartido"); // 5
        modelo.addColumn("Minuto");       // 6
        modelo.addColumn("IdEqAfec");     // 7 Oculto
        modelo.addColumn("EquipoAfectado");// 8 Oculto

        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();

        // Consulta espejo de CapturaTarjeta.cs
        String sql = "SELECT tar.IdTarjeta, tar.TipoTarjeta, jug.IdJugador, p.IdPartido, " +
                     "CONCAT(jug.IdJugador, ' ', par.NombreParticipante, ' [', jug.Posicion, ' - ', jug.Numero, ']') AS Jugador, " +
                     "CONCAT('EL: ', el.NombreEquipo, ' - EV: ', ev.NombreEquipo, ' - ', TO_CHAR(p.Fecha, 'DD/MM/YYYY'), ' [', l.Nombre, ']') AS DatosPartido, " +
                     "tar.Minuto, eqAfectado.IdEquipo AS IdEqAfec, eqAfectado.NombreEquipo AS EquipoAfectado " +
                     "FROM Evento.Tarjeta tar " +
                     "INNER JOIN Persona.Jugador jug ON tar.IdJugador = jug.IdJugador " +
                     "INNER JOIN Persona.Participante par ON par.IdParticipante = jug.IdParticipante " +
                     "INNER JOIN Evento.Partido p ON tar.IdPartido = p.IdPartido " +
                     "INNER JOIN Juego.Lugar l ON p.IdLugar = l.IdLugar " +
                     "INNER JOIN Club.Equipo el ON p.IdLocal = el.IdEquipo " +
                     "INNER JOIN Club.Equipo ev ON p.IdVisitante = ev.IdEquipo " +
                     "INNER JOIN Club.DetalleEquipo de ON de.IdJugador = jug.IdJugador AND (de.IdEquipo = p.IdLocal OR de.IdEquipo = p.IdVisitante) " +
                     "INNER JOIN Club.Equipo eqAfectado ON eqAfectado.IdEquipo = de.IdEquipo " +
                     "INNER JOIN Juego.Jornada j ON j.IdJornada = p.IdJornada " +
                     "INNER JOIN Juego.Torneo t ON t.IdTorneo = j.IdTorneo " +
                     "ORDER BY tar.IdTarjeta";
                     
        PreparedStatement ps = cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            modelo.addRow(new Object[]{
                rs.getLong("IdTarjeta"), rs.getString("TipoTarjeta"), rs.getLong("IdJugador"),
                rs.getLong("IdPartido"), rs.getString("Jugador"), rs.getString("DatosPartido"),
                rs.getInt("Minuto"), rs.getLong("IdEqAfec"), rs.getString("EquipoAfectado")
            });
        }
        return modelo;
    }

    public List<ItemCombo> obtenerJugadoresCombo(long idPartido) throws Exception {
        List<ItemCombo> lista = new ArrayList<>();
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        
        // El mismo filtro por Género y Edad que en CapturaTarjeta.cs
        String sql = "SELECT j.IdJugador, CONCAT(j.IdJugador, ' ', p.NombreParticipante, ' [', j.Posicion, ' - ', j.Numero, '] - ', e.NombreEquipo) AS Jugador " +
                     "FROM Persona.Jugador j " +
                     "INNER JOIN Persona.Participante p ON p.IdParticipante = j.IdParticipante " +
                     "INNER JOIN Club.DetalleEquipo de ON j.IdJugador = de.IdJugador " +
                     "INNER JOIN Club.Equipo e ON e.IdEquipo = de.IdEquipo " +
                     "INNER JOIN Evento.Partido pa ON (e.IdEquipo = pa.IdLocal OR e.IdEquipo = pa.IdVisitante) " +
                     "INNER JOIN Juego.Jornada jo ON jo.IdJornada = pa.IdJornada " +
                     "INNER JOIN Juego.Torneo t ON t.IdTorneo = jo.IdTorneo " +
                     "WHERE pa.IdPartido = ? AND t.Genero = p.Genero AND p.Edad >= t.EdadMin AND p.Edad <= t.EdadMax";
                     
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idPartido);
        ResultSet rs = ps.executeQuery();
        
        while(rs.next()) {
            lista.add(new ItemCombo(rs.getLong("IdJugador"), rs.getString("Jugador")));
        }
        return lista;
    }

    public String obtenerDatosPartidoForaneo(long idPartido) throws Exception {
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        String sql = "SELECT CONCAT('EL: ', el.NombreEquipo, ' - EV: ', ev.NombreEquipo, ' - ', TO_CHAR(p.Fecha, 'DD/MM/YYYY'), ' [', l.Nombre, ']') AS Partido " +
                     "FROM Evento.Partido p " +
                     "INNER JOIN Club.Equipo el ON p.IdLocal = el.IdEquipo " +
                     "INNER JOIN Club.Equipo ev ON p.IdVisitante = ev.IdEquipo " +
                     "INNER JOIN Juego.Lugar l ON p.IdLugar = l.IdLugar " +
                     "WHERE p.IdPartido = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idPartido);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getString("Partido");
        return "";
    }

    public int verificaMinuto(long idPartido, int minuto) throws Exception {
        if (minuto <= 0) return 1;
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        String sql = "SELECT EXTRACT(EPOCH FROM (r.HoraFin - p.HoraInicio))/60 FROM Evento.Partido p INNER JOIN Evento.ResultadoPartido r ON p.IdPartido = r.IdPartido WHERE p.IdPartido = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idPartido);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int duracionTotal = rs.getInt(1);
            return minuto <= duracionTotal ? 0 : 1;
        }
        return -1;
    }

    public int verificaMinutoDuplicado(long idPartido, int minuto, long idTarjetaSeleccionada, int esModificacion) throws Exception {
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        String sql = "SELECT COUNT(*) FROM Evento.Tarjeta WHERE IdPartido = ? AND Minuto = ? AND (? = 0 OR IdTarjeta != ?)";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idPartido); ps.setInt(2, minuto); ps.setInt(3, esModificacion); ps.setLong(4, idTarjetaSeleccionada);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt(1);
        return -1;
    }

    public int verificaEstadoEnMinuto(long idPartido, long idJugador, int minuto, long idTarjetaIgnorar) throws Exception {
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        // Traducción de la lógica de línea de tiempo de C#
        String sql = "SELECT COUNT(*) FROM Evento.Tarjeta WHERE IdPartido = ? AND IdJugador = ? AND Minuto <= ? AND IdTarjeta != ? " +
                     "AND (TipoTarjeta = 'Roja' OR (SELECT COUNT(*) FROM Evento.Tarjeta t2 WHERE t2.IdPartido = ? AND t2.IdJugador = ? " +
                     "AND t2.TipoTarjeta = 'Amarilla' AND t2.Minuto <= ? AND t2.IdTarjeta != ?) >= 2)";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idPartido); ps.setLong(2, idJugador); ps.setInt(3, minuto); ps.setLong(4, idTarjetaIgnorar);
        ps.setLong(5, idPartido); ps.setLong(6, idJugador); ps.setInt(7, minuto); ps.setLong(8, idTarjetaIgnorar);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt(1) > 0 ? 1 : 0;
        return -1;
    }

// Ahora recibe idEquipo para que la validación sea solo sobre la línea temporal de ese equipo
    public int verificaTarjetasViejas(long idJugador, long idPartidoDeLaTarjeta, long idEquipo) throws Exception {
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        
        // SQL Corregido: Agregamos "AND de.IdEquipo = ?" para aislar la actividad del equipo específico
        String sql = "SELECT COUNT(*) FROM Evento.Partido p " +
                     "INNER JOIN Club.DetalleEquipo de ON (p.IdLocal = de.IdEquipo OR p.IdVisitante = de.IdEquipo) " +
                     "WHERE de.IdJugador = ? AND de.IdEquipo = ? AND p.Estado = 'Jugado' AND p.IdPartido != ? AND ( " +
                     "p.Fecha > (SELECT Fecha FROM Evento.Partido WHERE IdPartido = ?) OR ( " +
                     "p.Fecha = (SELECT Fecha FROM Evento.Partido WHERE IdPartido = ?) AND p.HoraInicio > (SELECT HoraInicio FROM Evento.Partido WHERE IdPartido = ?) " +
                     ") )";
                     
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idJugador);
        ps.setLong(2, idEquipo); // El filtro clave
        ps.setLong(3, idPartidoDeLaTarjeta);
        ps.setLong(4, idPartidoDeLaTarjeta);
        ps.setLong(5, idPartidoDeLaTarjeta);
        ps.setLong(6, idPartidoDeLaTarjeta);
        
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt(1) > 0 ? 1 : 0;
        return -1;
    }

    public void insertarTarjeta(long idJugador, long idPartido, int minuto, String tipoTarjeta) throws Exception {
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        String sql = "INSERT INTO Evento.Tarjeta (IdJugador, IdPartido, Minuto, TipoTarjeta) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idJugador); ps.setLong(2, idPartido); ps.setInt(3, minuto); ps.setString(4, tipoTarjeta);
        ps.executeUpdate();
    }

    public void modificarTarjeta(long idTarjeta, long idJugador, int minuto, String tipoTarjeta) throws Exception {
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        String sql = "UPDATE Evento.Tarjeta SET IdJugador = ?, Minuto = ?, TipoTarjeta = ? WHERE IdTarjeta = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idJugador); ps.setInt(2, minuto); ps.setString(3, tipoTarjeta); ps.setLong(4, idTarjeta);
        ps.executeUpdate();
    }

    public void eliminarTarjeta(long idTarjeta) throws Exception {
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        String sql = "DELETE FROM Evento.Tarjeta WHERE IdTarjeta = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idTarjeta);
        ps.executeUpdate();
    }
}