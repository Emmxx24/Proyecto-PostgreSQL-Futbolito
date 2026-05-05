package com.mycompany.futbolito.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;
import com.mycompany.futbolito.conexion.ClaseConexion;
import com.mycompany.futbolito.modelos.Equipo;

public class EquipoDAO {

    public DefaultTableModel obtenerModeloEquipos() throws Exception {
        // Creamos el modelo pero le sobreescribimos el tipo de clase para la columna de la imagen
        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) {
                    return javax.swing.ImageIcon.class; // Para que dibuje la imagen y no el texto
                }
                return super.getColumnClass(columnIndex);
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Que no editen la tabla haciendo doble clic
            }
        };
        
        modelo.addColumn("ID Equipo");
        modelo.addColumn("Nombre del Equipo");
        modelo.addColumn("Logo");
        modelo.addColumn("Cant. Jugadores");
        modelo.addColumn("URL_Oculta"); // Esta nos servirá para leer la URL y descargarla

        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        String sql = "SELECT * FROM Club.Equipo ORDER BY IdEquipo ASC";
                     
        PreparedStatement ps = cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            modelo.addRow(new Object[]{
                rs.getLong("IdEquipo"),
                rs.getString("NombreEquipo"),
                null, // Ponemos el icono en nulo, lo descargaremos asíncronamente
                rs.getInt("CantJugadores"),
                rs.getString("Logo") // Guardamos el texto de la URL en la columna oculta
            });
        }
        return modelo;
    }
    
    // Función para validar que no haya otro equipo con el mismo nombre
    public boolean existeNombreEquipo(String nombre, long idExcluir) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();
        
        String sql = "SELECT COUNT(*) FROM Club.Equipo WHERE NombreEquipo = ? AND IdEquipo != ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setString(1, nombre);
        ps.setLong(2, idExcluir);
        
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }

    public void insertarEquipo(Equipo e) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        // Insertamos CantJugadores como 0 por defecto
        String sql = "INSERT INTO Club.Equipo (NombreEquipo, Logo, CantJugadores) VALUES (?, ?, 0)";
        PreparedStatement ps = cn.prepareStatement(sql);
        
        ps.setString(1, e.getNombreEquipo());
        ps.setString(2, e.getLogo());

        ps.executeUpdate();
    }

    public void modificarEquipo(Equipo e) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        String sql = "UPDATE Club.Equipo SET NombreEquipo = ?, Logo = ? WHERE IdEquipo = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        
        ps.setString(1, e.getNombreEquipo());
        ps.setString(2, e.getLogo());
        ps.setLong(3, e.getIdEquipo()); 

        ps.executeUpdate();
    }

    public void eliminarEquipo(long idEquipo) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        String sql = "DELETE FROM Club.Equipo WHERE IdEquipo = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idEquipo);

        ps.executeUpdate();
    }
}
