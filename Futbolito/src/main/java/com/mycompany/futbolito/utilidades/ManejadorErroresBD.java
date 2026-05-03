package com.mycompany.futbolito.utilidades;

import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ManejadorErroresBD {

    public static void mostrarErrorAmigable(Exception ex) {
        // Verificamos si es un error de Base de Datos
        if (ex instanceof SQLException) {
            SQLException sqlEx = (SQLException) ex;
            String estado = sqlEx.getSQLState();
            
            // Si el estado es nulo, evitamos un NullPointerException
            if (estado == null) estado = "";

            switch (estado) {
                case "23505": // Violación de Primary Key o Unique
                    JOptionPane.showMessageDialog(null, 
                        "Ya existe un registro con esos datos. Verifica que el identificador, teléfono o correo no estén repetidos.", 
                        "Dato Duplicado", JOptionPane.WARNING_MESSAGE);
                    break;
                    
                case "23503": // Violación de Foreign Key
                    JOptionPane.showMessageDialog(null, 
                        "No se puede eliminar o modificar este registro porque está siendo usado en otra parte del sistema (ej. tiene partidos o jugadores asignados).", 
                        "Registro en Uso", JOptionPane.ERROR_MESSAGE);
                    break;
                    
                case "23502": // Violación de Not Null
                    JOptionPane.showMessageDialog(null, 
                        "Faltan datos obligatorios por llenar en el formulario.", 
                        "Datos Incompletos", JOptionPane.WARNING_MESSAGE);
                    break;
                    
                case "23514": // Violación de restricción CHECK
                    JOptionPane.showMessageDialog(null, 
                        "Uno de los valores ingresados no cumple con las reglas del sistema (ej. edades, capacidades, posiciones válidas o números negativos).", 
                        "Regla de Negocio Inválida", JOptionPane.WARNING_MESSAGE);
                    break;
                    
                case "22001": // String data right truncation
                    JOptionPane.showMessageDialog(null, 
                        "Uno de los textos ingresados es demasiado largo. Revisa el tamaño máximo permitido.", 
                        "Texto Demasiado Largo", JOptionPane.WARNING_MESSAGE);
                    break;
                    
                case "22003": // Numeric value out of range
                    JOptionPane.showMessageDialog(null, 
                        "Uno de los números ingresados es demasiado grande para ser guardado.", 
                        "Número Fuera de Rango", JOptionPane.WARNING_MESSAGE);
                    break;

                case "28P01": // Invalid password
                case "3D000": // Invalid catalog name (Base de datos no existe)
                case "08001": // Error de conexión
                case "08006": // Error de conexión
                    JOptionPane.showMessageDialog(null, 
                        "No se pudo conectar a la base de datos PostgreSQL. Verifica que el servidor esté encendido y tus credenciales sean correctas.", 
                        "Error de Conexión", JOptionPane.ERROR_MESSAGE);
                    break;
                    
                default: // Cualquier otro error SQL no mapeado
                    JOptionPane.showMessageDialog(null, 
                        "Error en la base de datos (Estado " + estado + "): \n" + sqlEx.getMessage(), 
                        "Error SQL", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        } else {
            // Errores generales de Java (ej. NumberFormatException al intentar convertir letras a números)
            JOptionPane.showMessageDialog(null, 
                "Ocurrió un error inesperado en la aplicación: \n" + ex.getMessage(), 
                "Error del Sistema", JOptionPane.ERROR_MESSAGE);
        }
    }
}