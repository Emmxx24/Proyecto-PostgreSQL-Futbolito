package com.mycompany.futbolito.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import com.mycompany.futbolito.conexion.ClaseConexion;

public class DetalleTorneoDAO {

    // Clase auxiliar para llenar los ComboBox
    public static class ItemCombo {
        public long id;
        public String texto;
        public ItemCombo(long id, String texto) { this.id = id; this.texto = texto; }
    }

    public List<ItemCombo> obtenerTorneosCombo() throws Exception {
        List<ItemCombo> lista = new ArrayList<>();
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        // Usamos TO_CHAR en PostgreSQL equivalente al CONVERT de C#
        String sql = "SELECT T.IdTorneo, CONCAT(T.NombreTorneo, ' (', TO_CHAR(T.FechaInicio, 'DD/MM/YYYY'), ' - ', TO_CHAR(T.FechaFin, 'DD/MM/YYYY'), ')') AS NombreCompleto FROM Juego.Torneo T";
        PreparedStatement ps = cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            lista.add(new ItemCombo(rs.getLong("IdTorneo"), rs.getString("NombreCompleto")));
        }
        return lista;
    }

    public List<ItemCombo> obtenerEquiposCombo() throws Exception {
        List<ItemCombo> lista = new ArrayList<>();
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        String sql = "SELECT IdEquipo, NombreEquipo FROM Club.Equipo";
        PreparedStatement ps = cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            lista.add(new ItemCombo(rs.getLong("IdEquipo"), rs.getString("NombreEquipo")));
        }
        return lista;
    }

    public DefaultTableModel obtenerModeloDetalleTorneo() throws Exception {
        DefaultTableModel modelo = new DefaultTableModel();
        
        modelo.addColumn("IdTorneo"); // Oculta 0
        modelo.addColumn("IdEquipo"); // Oculta 1
        modelo.addColumn("Torneo");
        modelo.addColumn("Equipo");
        modelo.addColumn("Número de Jornadas"); // Oculta 4
        modelo.addColumn("Cantidad de Equipos"); // Oculta 5

        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        String sql = "SELECT DT.IdTorneo, DT.IdEquipo, " +
                     "CONCAT(T.NombreTorneo, ' (', TO_CHAR(T.FechaInicio, 'DD/MM/YYYY'), ' - ', TO_CHAR(T.FechaFin, 'DD/MM/YYYY'), ')') AS Torneo, " +
                     "E.NombreEquipo AS Equipo, " +
                     "T.NumJornadas, T.CantEquipos " +
                     "FROM Juego.DetalleTorneo DT " +
                     "INNER JOIN Juego.Torneo T ON DT.IdTorneo = T.IdTorneo " +
                     "INNER JOIN Club.Equipo E ON DT.IdEquipo = E.IdEquipo";
                     
        PreparedStatement ps = cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            modelo.addRow(new Object[]{
                rs.getLong("IdTorneo"),
                rs.getLong("IdEquipo"),
                rs.getString("Torneo"),
                rs.getString("Equipo"),
                rs.getInt("NumJornadas"),
                rs.getInt("CantEquipos")
            });
        }
        return modelo;
    }

    public boolean existeDetalle(long idTorneo, long idEquipo) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();
        
        String sql = "SELECT COUNT(*) FROM Juego.DetalleTorneo WHERE IdTorneo = ? AND IdEquipo = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idTorneo);
        ps.setLong(2, idEquipo);
        
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt(1) > 0;
        return false;
    }

    public boolean existeDetalleModificar(long idTorneoNuevo, long idEquipoNuevo, long idTorneoViejo, long idEquipoViejo) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();
        
        String sql = "SELECT COUNT(*) FROM Juego.DetalleTorneo WHERE IdTorneo = ? AND IdEquipo = ? AND NOT (IdTorneo = ? AND IdEquipo = ?)";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idTorneoNuevo);
        ps.setLong(2, idEquipoNuevo);
        ps.setLong(3, idTorneoViejo);
        ps.setLong(4, idEquipoViejo);
        
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt(1) > 0;
        return false;
    }

    public void insertarDetalle(long idTorneo, long idEquipo) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        String sql = "INSERT INTO Juego.DetalleTorneo (IdTorneo, IdEquipo) VALUES (?, ?)";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idTorneo);
        ps.setLong(2, idEquipo);
        ps.executeUpdate();
    }

    public void modificarDetalle(long idTorneoNuevo, long idEquipoNuevo, long idTorneoViejo, long idEquipoViejo) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        String sql = "UPDATE Juego.DetalleTorneo SET IdTorneo = ?, IdEquipo = ? WHERE IdTorneo = ? AND IdEquipo = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idTorneoNuevo);
        ps.setLong(2, idEquipoNuevo);
        ps.setLong(3, idTorneoViejo);
        ps.setLong(4, idEquipoViejo);
        ps.executeUpdate();
    }

    public void eliminarDetalle(long idTorneo, long idEquipo) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        String sql = "DELETE FROM Juego.DetalleTorneo WHERE IdTorneo = ? AND IdEquipo = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idTorneo);
        ps.setLong(2, idEquipo);
        ps.executeUpdate();
    }
}