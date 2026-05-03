package com.mycompany.futbolito.utilidades;

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
            if (anchoFinal > 300) {
                anchoFinal = 300;
            }
            
            // Aplicamos el ancho calculado
            modeloColumna.getColumn(columna).setPreferredWidth(anchoFinal);
        }
    }
}