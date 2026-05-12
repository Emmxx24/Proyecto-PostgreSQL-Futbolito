/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.mycompany.futbolito.vistas;

import com.mycompany.futbolito.dao.PartidoDAO;
import com.mycompany.futbolito.modelos.Partido;
import com.mycompany.futbolito.utilidades.ManejadorErroresBD;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Calendar;
import javax.swing.SpinnerDateModel;
import javax.swing.JSpinner;

/**
 *
 * @author Usuario
 */
public class VistaPartido extends javax.swing.JInternalFrame {

    private long idPartidoSeleccionado = -1;
    private List<PartidoDAO.ItemCombo> listaJornadas = new ArrayList<>();
    private List<PartidoDAO.ItemCombo> listaLugares = new ArrayList<>();
    private List<PartidoDAO.ItemCombo> listaArbitros = new ArrayList<>();
    private List<PartidoDAO.ItemCombo> listaEquipos = new ArrayList<>();
    /**
     * Creates new form VistaPartidos
     */
    public VistaPartido() {
        initComponents();
        
        spinnerHora.setModel(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spinnerHora, "HH:mm");
        spinnerHora.setEditor(timeEditor);

        tablaPartidos.setRowHeight(30); 
        
        cbJornada.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actualizarEquiposPorJornada();
            }
        });
        
        cargarCombos();
        cargarTabla();
        limpiarCampos();
    }
    
    private void cargarCombos() {
        try {
            PartidoDAO dao = new PartidoDAO();
            
            cbJornada.removeAllItems();
            listaJornadas = dao.obtenerJornadasCombo();
            for (PartidoDAO.ItemCombo i : listaJornadas) cbJornada.addItem(i.texto);
            
            cbLugar.removeAllItems();
            listaLugares = dao.obtenerLugaresCombo();
            for (PartidoDAO.ItemCombo i : listaLugares) cbLugar.addItem(i.texto);
            
            cbArbitro.removeAllItems();
            listaArbitros = dao.obtenerArbitrosCombo();
            for (PartidoDAO.ItemCombo i : listaArbitros) cbArbitro.addItem(i.texto);
            
        } catch (Exception ex) {
            ManejadorErroresBD.mostrarErrorAmigable(ex);
        }
    }

    private void cargarTabla() {
        try {
            PartidoDAO dao = new PartidoDAO();
            tablaPartidos.setModel(dao.obtenerModeloPartidos());
            
            for (int i = 1; i <= 5; i++) {
                tablaPartidos.getColumnModel().getColumn(i).setMinWidth(0);
                tablaPartidos.getColumnModel().getColumn(i).setMaxWidth(0);
                tablaPartidos.getColumnModel().getColumn(i).setWidth(0);
            }
            tablaPartidos.getTableHeader().setReorderingAllowed(false);
            com.mycompany.futbolito.utilidades.UtilidadesVista.autoAjustarColumnas(tablaPartidos);

        } catch (Exception ex) {
            ManejadorErroresBD.mostrarErrorAmigable(ex);
        }
    }

    private void limpiarCampos() {
        cbJornada.setSelectedIndex(-1);
        cbLugar.setSelectedIndex(-1);
        cbArbitro.setSelectedIndex(-1);
        cbEquiLoc.setSelectedIndex(-1);
        cbEquiVisi.setSelectedIndex(-1);
        fecha.setDate(new Date());
        
        // ¡LA MAGIA DE LA HORA! Borramos segundos y milisegundos al inicializar
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0); 
        spinnerHora.setValue(cal.getTime());
        
        idPartidoSeleccionado = -1;
        actualizarBotones(false);
    }
    
    private void actualizarBotones(boolean mostrar) {
        btnRegResultado.setEnabled(mostrar);
        btnRegResultado.setVisible(mostrar);
    }
    
    private void actualizarEquiposPorJornada() {
        if (cbJornada.getSelectedIndex() == -1) {
            cbEquiLoc.removeAllItems();
            cbEquiVisi.removeAllItems();
            listaEquipos.clear();
            return;
        }

        try {
            long idJornada = listaJornadas.get(cbJornada.getSelectedIndex()).id;
            PartidoDAO dao = new PartidoDAO();

            listaEquipos = dao.obtenerEquiposPorJornada(idJornada);

            cbEquiLoc.removeAllItems();
            cbEquiVisi.removeAllItems();
            for (PartidoDAO.ItemCombo i : listaEquipos) {
                cbEquiLoc.addItem(i.texto);
                cbEquiVisi.addItem(i.texto);
            }

            cbEquiLoc.setSelectedIndex(-1);
            cbEquiVisi.setSelectedIndex(-1);

        } catch (Exception ex) {
            ManejadorErroresBD.mostrarErrorAmigable(ex);
        }
    }

    // MÉTODO AUXILIAR PARA LIMPIAR LA HORA ANTES DE ENVIAR A LA BASE DE DATOS
    private java.sql.Time extraerHoraLimpia() {
        Calendar cal = Calendar.getInstance();
        cal.setTime((Date) spinnerHora.getValue());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new java.sql.Time(cal.getTimeInMillis());
    }
    
    // ==========================================
    // LÓGICA DE VALIDACIÓN 
    // ==========================================
    private boolean esValidoParaGuardar(long idLocal, long idVisitante, long idJornada, long idLugar, long idArbitro, java.sql.Date sqlFecha, java.sql.Time sqlHora, int esModificacion) {
        if (idLocal == idVisitante) {
            JOptionPane.showMessageDialog(this, "El equipo local y visitante no pueden ser el mismo.", "Atención", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            PartidoDAO dao = new PartidoDAO();
            boolean mod = (esModificacion == 1);

            if (!dao.verificarEquiposEnTorneo(idLocal, idVisitante, idJornada)) {
                JOptionPane.showMessageDialog(this, "Uno o ambos equipos no están inscritos en el torneo de esta jornada.", "Atención", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            if (dao.verificarEquiposDisponibles(idLocal, idVisitante, sqlFecha, sqlHora, idPartidoSeleccionado, mod)) {
                JOptionPane.showMessageDialog(this, "Uno o ambos equipos ya tienen un partido en esta misma fecha y hora.", "Atención", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            if (dao.verificaPartidoInverso(idLocal, idVisitante, idJornada, idPartidoSeleccionado, mod)) {
                JOptionPane.showMessageDialog(this, "Estos equipos ya se enfrentan en este torneo.", "Atención", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            if (dao.verificaLugar(idLugar, sqlFecha, sqlHora, idPartidoSeleccionado, mod)) {
                JOptionPane.showMessageDialog(this, "El lugar seleccionado ya está ocupado en esa fecha y hora.", "Atención", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            if (dao.verificarArbitro(idArbitro, sqlFecha, sqlHora, idPartidoSeleccionado, mod)) {
                JOptionPane.showMessageDialog(this, "El árbitro seleccionado ya tiene asignado un partido en esa fecha y hora.", "Atención", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            return true; 
            
        } catch (Exception ex) {
            ManejadorErroresBD.mostrarErrorAmigable(ex);
            return false;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cbJornada = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        cbArbitro = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        cbEquiLoc = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cbLugar = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        cbEquiVisi = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        btnAgregar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        fecha = new com.toedter.calendar.JDateChooser();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaPartidos = new javax.swing.JTable();
        spinnerHora = new javax.swing.JSpinner();
        btnRegResultado = new javax.swing.JButton();

        setTitle("Partido");
        try {
            setSelected(true);
        } catch (java.beans.PropertyVetoException e1) {
            e1.printStackTrace();
        }

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("Jornada:");

        cbJornada.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("Árbitro:");

        cbArbitro.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel4.setText("Equipo local:");

        cbEquiLoc.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel6.setText("Fecha:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText("Lugar:");

        cbLugar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel5.setText("Equipo visitante:");

        cbEquiVisi.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel7.setText("Hora de inicio:");

        btnAgregar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnAgregar.setText("Agregar");
        btnAgregar.addActionListener(this::btnAgregarActionPerformed);

        btnModificar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnModificar.setText("Modificar");
        btnModificar.addActionListener(this::btnModificarActionPerformed);

        btnEliminar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(this::btnEliminarActionPerformed);

        fecha.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        tablaPartidos.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tablaPartidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tablaPartidos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaPartidosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaPartidos);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        spinnerHora.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        btnRegResultado.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnRegResultado.setText("Registrar resultado");
        btnRegResultado.addActionListener(this::btnRegResultadoActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(btnRegResultado)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel2)
                                .addComponent(cbArbitro, 0, 300, Short.MAX_VALUE)
                                .addComponent(cbEquiLoc, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel1)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6)
                            .addComponent(fecha, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 91, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel3)
                                        .addComponent(cbLugar, 0, 250, Short.MAX_VALUE)
                                        .addComponent(cbEquiVisi, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel7))
                                .addGap(167, 167, 167)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnModificar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnAgregar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(spinnerHora, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(69, 69, 69))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cbJornada, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(344, 344, 344))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbJornada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(1, 1, 1)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbArbitro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbLugar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbEquiLoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbEquiVisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spinnerHora)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(fecha, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(71, 71, 71)
                        .addComponent(btnAgregar)
                        .addGap(43, 43, 43)
                        .addComponent(btnModificar)
                        .addGap(34, 34, 34)
                        .addComponent(btnEliminar)
                        .addGap(68, 68, 68)))
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRegResultado)
                .addGap(17, 17, 17))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        if (cbJornada.getSelectedIndex() == -1 || cbLugar.getSelectedIndex() == -1 || cbArbitro.getSelectedIndex() == -1 || cbEquiLoc.getSelectedIndex() == -1 || cbEquiVisi.getSelectedIndex() == -1 || fecha.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long idJornada = listaJornadas.get(cbJornada.getSelectedIndex()).id;
        long idLugar = listaLugares.get(cbLugar.getSelectedIndex()).id;
        long idArbitro = listaArbitros.get(cbArbitro.getSelectedIndex()).id;
        long idLocal = listaEquipos.get(cbEquiLoc.getSelectedIndex()).id;
        long idVisitante = listaEquipos.get(cbEquiVisi.getSelectedIndex()).id;
        
        java.sql.Date sqlFecha = new java.sql.Date(fecha.getDate().getTime());
        java.sql.Time sqlHora = extraerHoraLimpia(); // Usamos nuestra función limpia

        if (!esValidoParaGuardar(idLocal, idVisitante, idJornada, idLugar, idArbitro, sqlFecha, sqlHora, 0)) return;
        
        Partido p = new Partido();
        p.setIdJornada(idJornada);
        p.setIdLugar(idLugar);
        p.setIdArbitro(idArbitro);
        p.setIdLocal(idLocal);
        p.setIdVisitante(idVisitante);
        p.setFecha(sqlFecha);
        p.setHoraInicio(sqlHora);

        try {
            PartidoDAO dao = new PartidoDAO();
            dao.insertarPartido(p);
            cargarTabla();
            limpiarCampos();
        } catch (Exception ex) {
            ManejadorErroresBD.mostrarErrorAmigable(ex);
        }
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        if (idPartidoSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un Partido para modificar", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (cbJornada.getSelectedIndex() == -1 || cbLugar.getSelectedIndex() == -1 || cbArbitro.getSelectedIndex() == -1 || cbEquiLoc.getSelectedIndex() == -1 || cbEquiVisi.getSelectedIndex() == -1 || fecha.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long idJornada = listaJornadas.get(cbJornada.getSelectedIndex()).id;
        long idLugar = listaLugares.get(cbLugar.getSelectedIndex()).id;
        long idArbitro = listaArbitros.get(cbArbitro.getSelectedIndex()).id;
        long idLocal = listaEquipos.get(cbEquiLoc.getSelectedIndex()).id;
        long idVisitante = listaEquipos.get(cbEquiVisi.getSelectedIndex()).id;
        
        java.sql.Date sqlFecha = new java.sql.Date(fecha.getDate().getTime());
        java.sql.Time sqlHora = extraerHoraLimpia();

        if (!esValidoParaGuardar(idLocal, idVisitante, idJornada, idLugar, idArbitro, sqlFecha, sqlHora, 1)) return;
        
        Partido p = new Partido();
        p.setIdPartido(idPartidoSeleccionado);
        p.setIdJornada(idJornada);
        p.setIdLugar(idLugar);
        p.setIdArbitro(idArbitro);
        p.setIdLocal(idLocal);
        p.setIdVisitante(idVisitante);
        p.setFecha(sqlFecha);
        p.setHoraInicio(sqlHora);

        try {
            PartidoDAO dao = new PartidoDAO();
            dao.modificarPartido(p);
            cargarTabla();
            limpiarCampos();
        } catch (Exception ex) {
            ManejadorErroresBD.mostrarErrorAmigable(ex);
        }
    }//GEN-LAST:event_btnModificarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        if (idPartidoSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un Partido para eliminar", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            PartidoDAO dao = new PartidoDAO();
            dao.eliminarPartido(idPartidoSeleccionado);
            cargarTabla();
            limpiarCampos();
        } catch (Exception ex) {
            ManejadorErroresBD.mostrarErrorAmigable(ex);
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void tablaPartidosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaPartidosMouseClicked
        int fila = tablaPartidos.getSelectedRow();
        if (fila >= 0) {
            try {
                idPartidoSeleccionado = Long.parseLong(tablaPartidos.getValueAt(fila, 0).toString());
                long idJornada = Long.parseLong(tablaPartidos.getValueAt(fila, 1).toString());
                long idLugar = Long.parseLong(tablaPartidos.getValueAt(fila, 2).toString());
                long idArbitro = Long.parseLong(tablaPartidos.getValueAt(fila, 3).toString());
                long idLocal = Long.parseLong(tablaPartidos.getValueAt(fila, 4).toString());
                long idVisitante = Long.parseLong(tablaPartidos.getValueAt(fila, 5).toString());
                
                for (int i = 0; i < listaJornadas.size(); i++) if (listaJornadas.get(i).id == idJornada) { cbJornada.setSelectedIndex(i); break; }
                for (int i = 0; i < listaLugares.size(); i++) if (listaLugares.get(i).id == idLugar) { cbLugar.setSelectedIndex(i); break; }
                for (int i = 0; i < listaArbitros.size(); i++) if (listaArbitros.get(i).id == idArbitro) { cbArbitro.setSelectedIndex(i); break; }
                
                // Los equipos dependen de la jornada seleccionada, por lo que el ActionListener los rellenó, ahora los seleccionamos
                for (int i = 0; i < listaEquipos.size(); i++) if (listaEquipos.get(i).id == idLocal) { cbEquiLoc.setSelectedIndex(i); break; }
                for (int i = 0; i < listaEquipos.size(); i++) if (listaEquipos.get(i).id == idVisitante) { cbEquiVisi.setSelectedIndex(i); break; }
                
                java.sql.Date fechaSQL = (java.sql.Date) tablaPartidos.getValueAt(fila, 11);
                java.sql.Time horaSQL = (java.sql.Time) tablaPartidos.getValueAt(fila, 12);
                
                fecha.setDate(new java.util.Date(fechaSQL.getTime()));
                spinnerHora.setValue(new java.util.Date(horaSQL.getTime())); 
                actualizarBotones(true);
            } catch (Exception ex) {
                limpiarCampos();
            }
        }
    }//GEN-LAST:event_tablaPartidosMouseClicked

    private void btnRegResultadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegResultadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRegResultadoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnRegResultado;
    private javax.swing.JComboBox<String> cbArbitro;
    private javax.swing.JComboBox<String> cbEquiLoc;
    private javax.swing.JComboBox<String> cbEquiVisi;
    private javax.swing.JComboBox<String> cbJornada;
    private javax.swing.JComboBox<String> cbLugar;
    private com.toedter.calendar.JDateChooser fecha;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner spinnerHora;
    private javax.swing.JTable tablaPartidos;
    // End of variables declaration//GEN-END:variables
}
