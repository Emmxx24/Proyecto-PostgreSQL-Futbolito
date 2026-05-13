package com.mycompany.futbolito.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import com.mycompany.futbolito.conexion.ClaseConexion;

public class GolDAO {

    public static class ItemCombo {
        public long id;
        public String texto;
        public ItemCombo(long id, String texto) { this.id = id; this.texto = texto; }
    }

    public DefaultTableModel obtenerModeloGoles() throws Exception {
        DefaultTableModel modelo = new DefaultTableModel();
        
        modelo.addColumn("IdGol");     // 0 Oculto
        modelo.addColumn("IdJugador"); // 1 Oculto
        modelo.addColumn("IdPartido"); // 2 Oculto
        modelo.addColumn("Jugador");   // 3
        modelo.addColumn("Equipo Anotador"); // 4
        modelo.addColumn("Partido");   // 5
        modelo.addColumn("Minuto");    // 6

        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();

        // Consulta exacta de tu C#
        String sql = "SELECT g.IdGol, jug.IdJugador, p.IdPartido, " +
                     "CONCAT(jug.IdJugador, ' ', par.NombreParticipante, ' [', jug.Posicion, ' - ', jug.Numero, ']') AS Jugador, " +
                     "eqAnotador.NombreEquipo AS EquipoAnotador, " +
                     "CONCAT('EL: ', el.NombreEquipo, ' - EV: ', ev.NombreEquipo, ' - ', TO_CHAR(p.Fecha, 'DD/MM/YYYY'), ' [', l.Nombre, ']') AS Partido, " +
                     "g.Minuto " +
                     "FROM Evento.Gol g " +
                     "INNER JOIN Persona.Jugador jug ON g.IdJugador = jug.IdJugador " +
                     "INNER JOIN Persona.Participante par ON par.IdParticipante = jug.IdParticipante " +
                     "INNER JOIN Evento.Partido p ON g.IdPartido = p.IdPartido " +
                     "INNER JOIN Club.Equipo el ON p.IdLocal = el.IdEquipo " +
                     "INNER JOIN Club.Equipo ev ON p.IdVisitante = ev.IdEquipo " +
                     "INNER JOIN Club.DetalleEquipo de ON de.IdJugador = jug.IdJugador " +
                     "AND (de.IdEquipo = p.IdLocal OR de.IdEquipo = p.IdVisitante) " +
                     "INNER JOIN Club.Equipo eqAnotador ON eqAnotador.IdEquipo = de.IdEquipo " +
                     "INNER JOIN Juego.Lugar l ON p.IdLugar = l.IdLugar " +
                     "ORDER BY g.IdGol";
                     
        PreparedStatement ps = cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            modelo.addRow(new Object[]{
                rs.getLong("IdGol"), rs.getLong("IdJugador"), rs.getLong("IdPartido"),
                rs.getString("Jugador"), rs.getString("EquipoAnotador"), rs.getString("Partido"), rs.getInt("Minuto")
            });
        }
        return modelo;
    }

    public List<ItemCombo> obtenerJugadoresCombo(long idPartido) throws Exception {
        List<ItemCombo> lista = new ArrayList<>();
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        
        // Filtro por Género y Edad del Torneo
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

    public String obtenerDetallePartido(long idPartido) throws Exception {
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

    public int verificaEstadoEnMinuto(long idPartido, long idJugador, int minuto) throws Exception {
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        String sql = "SELECT COUNT(*) FROM Evento.Tarjeta WHERE IdPartido = ? AND IdJugador = ? AND Minuto <= ? " +
                     "AND (TipoTarjeta = 'Roja' OR (SELECT COUNT(*) FROM Evento.Tarjeta t2 WHERE t2.IdPartido = ? AND t2.IdJugador = ? AND t2.TipoTarjeta = 'Amarilla' AND t2.Minuto <= ?) >= 2)";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idPartido); ps.setLong(2, idJugador); ps.setInt(3, minuto);
        ps.setLong(4, idPartido); ps.setLong(5, idJugador); ps.setInt(6, minuto);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt(1) > 0 ? 1 : 0;
        return -1;
    }

    public int verificaMinutoDuplicado(long idPartido, int minuto, long idGolSeleccionado, int esModificacion) throws Exception {
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        String sql = "SELECT COUNT(*) FROM Evento.Gol WHERE IdPartido = ? AND Minuto = ? AND (? = 0 OR IdGol != ?)";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idPartido); ps.setInt(2, minuto); ps.setInt(3, esModificacion); ps.setLong(4, idGolSeleccionado);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt(1);
        return -1;
    }

    public void insertarGol(long idJugador, long idPartido, int minuto) throws Exception {
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        String sql = "INSERT INTO Evento.Gol (IdJugador, IdPartido, Minuto) VALUES (?, ?, ?)";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idJugador); ps.setLong(2, idPartido); ps.setInt(3, minuto);
        ps.executeUpdate();
    }

    public void modificarGol(long idGol, long idJugador, int minuto) throws Exception {
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        String sql = "UPDATE Evento.Gol SET IdJugador = ?, Minuto = ? WHERE IdGol = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idJugador); ps.setInt(2, minuto); ps.setLong(3, idGol);
        ps.executeUpdate();
    }

    public void eliminarGol(long idGol) throws Exception {
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        String sql = "DELETE FROM Evento.Gol WHERE IdGol = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idGol);
        ps.executeUpdate();
    }
}