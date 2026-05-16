/*package com.mycompany.futbolito.utilidades;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class UtilidadesVista {

    public static void autoAjustarColumnas(JTable tabla) {
        TableColumnModel modeloColumna = tabla.getColumnModel();
        
        // Recorremos todas las columnas de la tabla
        for (int columna = 0; columna < tabla.getColumnCount(); columna++) {
            int anchoMinimo = 50; // Un ancho mínimo por si está vacía
            
            // 1. Revisamos el ancho del título de la columna
            TableCellRenderer renderCabecera = tabla.getTableHeader().getDefaultRenderer();
            Component compCabecera = renderCabecera.getTableCellRendererComponent(
                    tabla, modeloColumna.getColumn(columna).getHeaderValue(), false, false, 0, columna);
            int anchoCabecera = compCabecera.getPreferredSize().width + 15; // +15 de margen
            
            int anchoMaximoDatos = anchoMinimo;
            
            // 2. Revisamos el ancho de todos los datos en esa columna
            for (int fila = 0; fila < tabla.getRowCount(); fila++) {
                TableCellRenderer renderCelda = tabla.getCellRenderer(fila, columna);
                Component compCelda = tabla.prepareRenderer(renderCelda, fila, columna);
                anchoMaximoDatos = Math.max(compCelda.getPreferredSize().width + 15, anchoMaximoDatos);
            }
            
            // 3. Tomamos el mayor entre el título y los datos
            int anchoFinal = Math.max(anchoCabecera, anchoMaximoDatos);
            
            // (Opcional) Ponemos un límite máximo para que una columna no ocupe toda la pantalla
            if (anchoFinal > 500) {
                anchoFinal = 500;
            }
            
            // Aplicamos el ancho calculado
            modeloColumna.getColumn(columna).setPreferredWidth(anchoFinal);
        }
    }
}*/
package com.mycompany.futbolito.utilidades;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class UtilidadesVista {

    // 1. CREADO: Un renderizador personalizado basado en JTextArea para permitir saltos de línea
    private static class CellRendererMultiLinea extends JTextArea implements TableCellRenderer {
        public CellRendererMultiLinea() {
            setLineWrap(true);       // Activa el salto de línea automático
            setWrapStyleWord(true);  // Corta por palabras completas, no a mitad de una letra
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            // Respetamos los colores de selección del sistema visual
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }
            
            setFont(table.getFont());
            setText(value != null ? value.toString() : "");
            return this;
        }
    }

    // 2. NUEVO MÉTODO MAESTRO: Ajusta el ancho de columnas, activa el wrap y calcula la altura dynamic
    public static void autoAjustarTodo(JTable tabla) {
        // A) Primero calculamos los anchos horizontales con tu lógica original
        autoAjustarColumnas(tabla);
        
        // B) Aplicamos nuestro renderizador multilínea a todas las columnas del modelo
        CellRendererMultiLinea renderMultiLinea = new CellRendererMultiLinea();
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(renderMultiLinea);
        }
        
        // C) Recorremos la tabla verticalmente para calcular la altura perfecta de cada fila
        for (int fila = 0; fila < tabla.getRowCount(); fila++) {
            int alturaMaxima = 30; // Tu altura mínima por defecto
            
            for (int col = 0; col < tabla.getColumnCount(); col++) {
                // ¡AQUÍ ESTÁ EL FIX! Cambiamos getWidth() por getPreferredWidth()
                int anchoColumna = tabla.getColumnModel().getColumn(col).getPreferredWidth();
                
                if (anchoColumna <= 0) {
                    continue;
                }
                
                TableCellRenderer renderer = tabla.getCellRenderer(fila, col);
                Component comp = tabla.prepareRenderer(renderer, fila, col);
                
                // Le decimos al componente: "Este será tu ancho final, calcula tu altura libremente"
                comp.setSize(anchoColumna, Short.MAX_VALUE);
                
                int alturaCelda = comp.getPreferredSize().height + 6;
                alturaMaxima = Math.max(alturaMaxima, alturaCelda);
            }
            
            tabla.setRowHeight(fila, alturaMaxima);
        }
    }

    // Tu método original intacto, usado como soporte
    public static void autoAjustarColumnas(JTable tabla) {
        TableColumnModel modeloColumna = tabla.getColumnModel();
        
        for (int columna = 0; columna < tabla.getColumnCount(); columna++) {
            int anchoMinimo = 50; 
            
            TableCellRenderer renderCabecera = tabla.getTableHeader().getDefaultRenderer();
            Component compCabecera = renderCabecera.getTableCellRendererComponent(
                    tabla, modeloColumna.getColumn(columna).getHeaderValue(), false, false, 0, columna);
            int anchoCabecera = compCabecera.getPreferredSize().width + 15; 
            
            int anchoMaximoDatos = anchoMinimo;
            
            for (int fila = 0; fila < tabla.getRowCount(); fila++) {
                TableCellRenderer renderCelda = tabla.getCellRenderer(fila, columna);
                Component compCelda = tabla.prepareRenderer(renderCelda, fila, columna);
                anchoMaximoDatos = Math.max(compCelda.getPreferredSize().width + 15, anchoMaximoDatos);
            }
            
            int anchoFinal = Math.max(anchoCabecera, anchoMaximoDatos);
            
            if (anchoFinal > 500) {
                anchoFinal = 500;
            }
            
            modeloColumna.getColumn(columna).setPreferredWidth(anchoFinal);
        }
    }
}