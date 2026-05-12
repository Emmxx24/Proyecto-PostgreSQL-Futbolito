package com.mycompany.futbolito.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import com.mycompany.futbolito.conexion.ClaseConexion;
import com.mycompany.futbolito.modelos.Partido;

public class PartidoDAO {

    public static class ItemCombo {
        public long id;
        public String texto;
        public ItemCombo(long id, String texto) { this.id = id; this.texto = texto; }
    }

    public List<ItemCombo> obtenerJornadasCombo() throws Exception {
        List<ItemCombo> lista = new ArrayList<>();
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        String sql = "SELECT J.IdJornada, CONCAT(T.NombreTorneo, ' (', TO_CHAR(T.FechaInicio, 'DD/MM/YYYY'), ' - ', " +
                     "TO_CHAR(T.FechaFin, 'DD/MM/YYYY'), ' Jornada ', J.NumeroJornada, ')') AS JornadaTorneo " +
                     "FROM Juego.Jornada J INNER JOIN Juego.Torneo T ON J.IdTorneo = T.IdTorneo " +
                     "ORDER BY T.NombreTorneo, J.NumeroJornada";
        PreparedStatement ps = cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) lista.add(new ItemCombo(rs.getLong("IdJornada"), rs.getString("JornadaTorneo")));
        return lista;
    }

    public List<ItemCombo> obtenerLugaresCombo() throws Exception {
        List<ItemCombo> lista = new ArrayList<>();
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        String sql = "SELECT IdLugar, CONCAT(Nombre, ' [', Capacidad, ']') AS Nombre FROM Juego.Lugar ORDER BY Nombre";
        PreparedStatement ps = cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) lista.add(new ItemCombo(rs.getLong("IdLugar"), rs.getString("Nombre")));
        return lista;
    }

    public List<ItemCombo> obtenerArbitrosCombo() throws Exception {
        List<ItemCombo> lista = new ArrayList<>();
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        String sql = "SELECT a.IdArbitro, CONCAT(a.IdArbitro, ' - ', p.NombreParticipante) AS NombreParticipante FROM Persona.Arbitro a " +
                     "INNER JOIN Persona.Participante p ON a.IdParticipante = p.IdParticipante " +
                     "ORDER BY p.NombreParticipante";
        PreparedStatement ps = cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) lista.add(new ItemCombo(rs.getLong("IdArbitro"), rs.getString("NombreParticipante")));
        return lista;
    }

    public List<ItemCombo> obtenerEquiposCombo() throws Exception {
        List<ItemCombo> lista = new ArrayList<>();
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        String sql = "SELECT IdEquipo, NombreEquipo FROM Club.Equipo ORDER BY NombreEquipo";
        PreparedStatement ps = cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) lista.add(new ItemCombo(rs.getLong("IdEquipo"), rs.getString("NombreEquipo")));
        return lista;
    }
    
    public List<ItemCombo> obtenerEquiposPorJornada(long idJornada) throws Exception {
        List<ItemCombo> lista = new ArrayList<>();
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        String sql = "SELECT E.IdEquipo, E.NombreEquipo " +
                     "FROM Club.Equipo E " +
                     "INNER JOIN Juego.DetalleTorneo DT ON E.IdEquipo = DT.IdEquipo " +
                     "WHERE DT.IdTorneo = (SELECT IdTorneo FROM Juego.Jornada WHERE IdJornada = ?) " +
                     "ORDER BY E.NombreEquipo";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idJornada);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) lista.add(new ItemCombo(rs.getLong("IdEquipo"), rs.getString("NombreEquipo")));
        return lista;
    }

    public DefaultTableModel obtenerModeloPartidos() throws Exception {
        DefaultTableModel modelo = new DefaultTableModel();
        
        modelo.addColumn("IdPartido");   // 0
        modelo.addColumn("IdJornada");   // 1
        modelo.addColumn("IdLugar");     // 2
        modelo.addColumn("IdArbitro");   // 3
        modelo.addColumn("IdLocal");     // 4
        modelo.addColumn("IdVisitante"); // 5
        
        modelo.addColumn("Jornada");      // 6
        modelo.addColumn("Lugar");        // 7
        modelo.addColumn("Árbitro");      // 8
        modelo.addColumn("Local");        // 9
        modelo.addColumn("Visitante");    // 10
        modelo.addColumn("Fecha");        // 11
        modelo.addColumn("Hora");         // 12
        modelo.addColumn("Estado");       // 13

        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();

        String sql = "SELECT P.IdPartido, P.IdJornada, P.IdLugar, P.IdArbitro, P.IdLocal, P.IdVisitante, " +
                     "CONCAT(T.NombreTorneo, ' (', TO_CHAR(T.FechaInicio, 'DD/MM/YYYY'), ' - ', TO_CHAR(T.FechaFin, 'DD/MM/YYYY'), ' Jornada ', J.NumeroJornada, ')') AS Jornada, " +
                     "CONCAT(L.Nombre, ' [', L.Capacidad, ']') AS Lugar, " +
                     "CONCAT(A.IdArbitro, ' - ', Part.NombreParticipante) AS Arbitro, " +
                     "EL.NombreEquipo AS Local, " +
                     "EV.NombreEquipo AS Visitante, " +
                     "P.Fecha, P.HoraInicio, P.Estado " +
                     "FROM Evento.Partido P " +
                     "INNER JOIN Juego.Jornada J ON P.IdJornada = J.IdJornada " +
                     "INNER JOIN Juego.Torneo T ON J.IdTorneo = T.IdTorneo " +
                     "INNER JOIN Juego.Lugar L ON P.IdLugar = L.IdLugar " +
                     "INNER JOIN Persona.Arbitro A ON P.IdArbitro = A.IdArbitro " +
                     "INNER JOIN Persona.Participante Part ON A.IdParticipante = Part.IdParticipante " +
                     "INNER JOIN Club.Equipo EL ON P.IdLocal = EL.IdEquipo " +
                     "INNER JOIN Club.Equipo EV ON P.IdVisitante = EV.IdEquipo " +
                     "ORDER BY P.IdPartido";
                     
        PreparedStatement ps = cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            modelo.addRow(new Object[]{
                rs.getLong("IdPartido"), rs.getLong("IdJornada"), rs.getLong("IdLugar"),
                rs.getLong("IdArbitro"), rs.getLong("IdLocal"), rs.getLong("IdVisitante"),
                rs.getString("Jornada"), rs.getString("Lugar"), rs.getString("Arbitro"),
                rs.getString("Local"), rs.getString("Visitante"), 
                rs.getDate("Fecha"), rs.getTime("HoraInicio"), rs.getString("Estado")
            });
        }
        return modelo;
    }

    // ==========================================
    // MÉTODOS DE VALIDACIÓN MANUALES EN JAVA
    // ==========================================

    // 1. Verifica si los equipos pertenecen al torneo de esa jornada
    public boolean verificarEquiposEnTorneo(long idLocal, long idVisitante, long idJornada) throws Exception {
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        String sql = "SELECT COUNT(*) FROM Juego.DetalleTorneo WHERE IdTorneo = (SELECT IdTorneo FROM Juego.Jornada WHERE IdJornada = ?) AND IdEquipo IN (?, ?)";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idJornada);
        ps.setLong(2, idLocal);
        ps.setLong(3, idVisitante);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) == 2;
        }
        return false;
    }

    // 2. Verifica que ninguno de los dos equipos esté ocupado exactamente a la misma hora y fecha
    public boolean verificarEquiposDisponibles(long idLocal, long idVisitante, java.sql.Date fecha, java.sql.Time horaInicio, long idPartido, boolean esModificacion) throws Exception {
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        String sql = "SELECT COUNT(*) FROM Evento.Partido WHERE Fecha = ? AND HoraInicio = ? AND (IdLocal IN (?, ?) OR IdVisitante IN (?, ?))";
        if (esModificacion) sql += " AND IdPartido != ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setDate(1, fecha); ps.setTime(2, horaInicio);
        ps.setLong(3, idLocal); ps.setLong(4, idVisitante);
        ps.setLong(5, idLocal); ps.setLong(6, idVisitante);
        if (esModificacion) ps.setLong(7, idPartido);
        ResultSet rs = ps.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }

    public boolean verificaPartidoInverso(long idLocal, long idVisitante, long idJornada, long idPartido, boolean esModificacion) throws Exception {
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        
        // Aquí hacemos el JOIN con Jornada para buscar en todo el Torneo
        String sql = "SELECT COUNT(*) FROM Evento.Partido P INNER JOIN Juego.Jornada J ON P.IdJornada = J.IdJornada " +
                     "WHERE J.IdTorneo = (SELECT IdTorneo FROM Juego.Jornada WHERE IdJornada = ?) " +
                     "AND ((P.IdLocal = ? AND P.IdVisitante = ?) OR (P.IdLocal = ? AND P.IdVisitante = ?))";
                     
        if (esModificacion) sql += " AND P.IdPartido != ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idJornada);
        ps.setLong(2, idLocal); ps.setLong(3, idVisitante);
        ps.setLong(4, idVisitante); ps.setLong(5, idLocal); 
        if (esModificacion) ps.setLong(6, idPartido);
        ResultSet rs = ps.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }

    // 4. Verifica que la cancha no esté ocupada en esa fecha y hora
    public boolean verificaLugar(long idLugar, java.sql.Date fecha, java.sql.Time horaInicio, long idPartido, boolean esModificacion) throws Exception {
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        String sql = "SELECT COUNT(*) FROM Evento.Partido WHERE IdLugar = ? AND Fecha = ? AND HoraInicio = ?";
        if (esModificacion) sql += " AND IdPartido != ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idLugar); ps.setDate(2, fecha); ps.setTime(3, horaInicio);
        if (esModificacion) ps.setLong(4, idPartido);
        ResultSet rs = ps.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }

    // 5. Verifica que el árbitro no ande pitando otro partido en esa fecha y hora
    public boolean verificarArbitro(long idArbitro, java.sql.Date fecha, java.sql.Time horaInicio, long idPartido, boolean esModificacion) throws Exception {
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        String sql = "SELECT COUNT(*) FROM Evento.Partido WHERE IdArbitro = ? AND Fecha = ? AND HoraInicio = ?";
        if (esModificacion) sql += " AND IdPartido != ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idArbitro); ps.setDate(2, fecha); ps.setTime(3, horaInicio);
        if (esModificacion) ps.setLong(4, idPartido);
        ResultSet rs = ps.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }

    public void insertarPartido(Partido p) throws Exception {
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        String sql = "INSERT INTO Evento.Partido (IdArbitro, IdJornada, IdLugar, IdLocal, IdVisitante, Fecha, HoraInicio, Estado) VALUES (?, ?, ?, ?, ?, ?, ?, 'Pendiente')";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, p.getIdArbitro());
        ps.setLong(2, p.getIdJornada());
        ps.setLong(3, p.getIdLugar());
        ps.setLong(4, p.getIdLocal());
        ps.setLong(5, p.getIdVisitante());
        ps.setDate(6, p.getFecha());
        ps.setTime(7, p.getHoraInicio());
        ps.executeUpdate();
    }

    public void modificarPartido(Partido p) throws Exception {
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        String sql = "UPDATE Evento.Partido SET IdArbitro=?, IdJornada=?, IdLugar=?, IdLocal=?, IdVisitante=?, Fecha=?, HoraInicio=? WHERE IdPartido=?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, p.getIdArbitro());
        ps.setLong(2, p.getIdJornada());
        ps.setLong(3, p.getIdLugar());
        ps.setLong(4, p.getIdLocal());
        ps.setLong(5, p.getIdVisitante());
        ps.setDate(6, p.getFecha());
        ps.setTime(7, p.getHoraInicio());
        ps.setLong(8, p.getIdPartido());
        ps.executeUpdate();
    }

    public void eliminarPartido(long idPartido) throws Exception {
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        String sql = "DELETE FROM Evento.Partido WHERE IdPartido = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idPartido);
        ps.executeUpdate();
    }
}