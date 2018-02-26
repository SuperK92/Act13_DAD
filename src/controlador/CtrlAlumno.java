/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import conexion.ClaseDatos;
import modelo.NegAlumnos;
import datos.Alumno;
import vista.DlgAlumno;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author jjrodmar
 */
public class CtrlAlumno implements ActionListener {

    private static final int TIEMPOBUSCAR = 300;
    private Timer timerbuscar; //Se establece un tiempo para realizar buscar datos en la tabla alumnos
    private CtrlTablaAlumno rsmodel;
    private ClaseDatos _cldatos = null; //Clase de conexión a la base de datos
    private NegAlumnos negalumnos; //La clase del Modelo
    private Alumno alumno; //Clase define la estructura de los campos que necesitamos del alumno
    private Alumno currentAlumno; //Define al alumno actual;
    private boolean _activoSeleccionar;
    //private boolean _hayseleccion;
    private DlgAlumno dlgalumno = null; //Indica una variable de la vista del formulario de alumnos

    //Constructor de la clase
    public CtrlAlumno(java.awt.Frame parent, ClaseDatos cldatos, boolean activoSeleccionar) {

        IniciarFormulario(parent);
        IniciarEventos();
        InicializarFormulario(cldatos, activoSeleccionar);
        IncializarTabla();

        this.dlgalumno.setVisible(true);

    }

    private void IniciarFormulario(java.awt.Frame parent) {
        this.dlgalumno = new DlgAlumno(parent);
        this.dlgalumno.setLocationRelativeTo(null);
        this.dlgalumno.setResizable(false);
        this.dlgalumno.setTitle("Alumnos");

    }

    private void InicializarFormulario(ClaseDatos cldatos, boolean activoSeleccionar) {
        this._cldatos = cldatos;
        this._activoSeleccionar = activoSeleccionar;
        this.dlgalumno.btnSeleccionar.setEnabled(_activoSeleccionar);
        this.currentAlumno = null;

    }

    private void IniciarEventos() {
        // Los eventos de documento ocurren cuando el contenido de un documento cambia de alguna manera
        // en este caso  para habilitar los campos o no
        //Implemento el listener de comuent para txtDni, txtNombre, txtApellido1, txtApellido2 
        this.dlgalumno.txtDni.getDocument().addDocumentListener(HabilitarBotonAltas());
        this.dlgalumno.txtNombre.getDocument().addDocumentListener(HabilitarBotonAltas());
        this.dlgalumno.txtApellido1.getDocument().addDocumentListener(HabilitarBotonAltas());
        this.dlgalumno.txtApellido2.getDocument().addDocumentListener(HabilitarBotonAltas());

        //Implemento el listener de document para txtbuscar. Activa la busqueda de datos
        dlgalumno.txtBuscar.getDocument().addDocumentListener(ActivarBusqueda());

        //Indico la ActionListener que deseo eschucar los eventos de los diferentes botones
        dlgalumno.btnSalir.addActionListener(this);
        dlgalumno.btnAltas.addActionListener(this);
        dlgalumno.btnBajas.addActionListener(this);
        dlgalumno.btnModificaciones.addActionListener(this);
        dlgalumno.btnSeleccionar.addActionListener(this);
        dlgalumno.getBtnImprimir().addActionListener(this); 

        //Agrega un oyente a la lista que se notifica cada vez que se produce un cambio en el modelo de datos.
        dlgalumno.TablaAlumnos.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) { //Cambios que se produce en el Jtable
                selectionChanged();
            }
        });
    }

    private void IncializarTabla() {
        try {
            alumno = new Alumno();
            negalumnos = new NegAlumnos(_cldatos); //Llamo al constructor de la clase negalumnos que contiene el modelo de datos de alumno
            negalumnos.Actualizar();//hace la consulta y vuelca resulset a negocio de todo el contenido de la tabla alumnos

            rsmodel = new CtrlTablaAlumno(negalumnos); //Asigna la clase CtrlTablaAlumno a la variable rsmodel del mismo tipo
            dlgalumno.TablaAlumnos.setModel(rsmodel); //Cargo en el Jtable TablaAlumnos el contenido de select realizado en negalumnos.actualizar

        } catch (Exception ex) {
            VentanaMensajeError(ex.toString());
        }

    }

    //El procedimiento ocurre cuando selecciono un registro del Jtable
    private void selectionChanged() {

        int fila = dlgalumno.TablaAlumnos.getSelectedRow();
        //System.out.println( fila );
        try {
            currentAlumno = negalumnos.getAlumno(fila + 1);  //Es en base 1
        } catch (Exception e) {
            currentAlumno = null;
        }
        MostrarDatos();

        dlgalumno.btnBajas.setEnabled(fila != -1);
        dlgalumno.btnModificaciones.setEnabled(fila != -1);

    }

    //Muestra en los JtextField los datos que hemos seleccionado en el Jtable.
    private void MostrarDatos() {

        dlgalumno.txtDni.setText(currentAlumno != null ? currentAlumno.getDni() : "");
        dlgalumno.txtRegistro.setText(currentAlumno != null ? "" + currentAlumno.getRegistro() : "");
        dlgalumno.txtNombre.setText(currentAlumno != null ? currentAlumno.getNombre() : "");
        dlgalumno.txtApellido1.setText(currentAlumno != null ? currentAlumno.getApellido1() : "");
        dlgalumno.txtApellido2.setText(currentAlumno != null ? currentAlumno.getApellido2() : "");

    }

    //Es función controla si el botón btnAltas esta activo o no  si los jtextfiel estan vacios o no.
//https://docs.oracle.com/javase/tutorial/uiswing/events/documentlistener.html
    private DocumentListener HabilitarBotonAltas() {

        DocumentListener documento;

        documento = new DocumentListener() {
            public void HayCambio() {

                dlgalumno.btnAltas.setEnabled(
                        !dlgalumno.txtDni.getText().isEmpty() && (!dlgalumno.txtNombre.getText().isEmpty()
                        || !dlgalumno.txtApellido1.getText().isEmpty()
                        || !dlgalumno.txtApellido2.getText().isEmpty()));
            }

            @Override
            public void insertUpdate(DocumentEvent e) { //Se invoca cuando  se inserta en el documento escuchado
                HayCambio();
            }

            @Override
            public void removeUpdate(DocumentEvent e) { //Se invoca cuando se elimina texto del documento escuchado
                HayCambio();
            }

            @Override
            public void changedUpdate(DocumentEvent e) { //Se invoca cuando cambia el documento que escucha
                HayCambio();
            }
        };
        return documento;
    }

//Cuando existe un cambio txt_buscar realiza la busqueda en la tabla alumnos
//https://docs.oracle.com/javase/tutorial/uiswing/events/documentlistener.html
    private DocumentListener ActivarBusqueda() {
        DocumentListener documento;

        documento = new DocumentListener() {

            private void ActivoTimer() {

                if ((timerbuscar != null) && timerbuscar.isRunning()) {
                    timerbuscar.restart();
                } else {
                    timerbuscar = new Timer(TIEMPOBUSCAR, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            timerbuscar = null;
                            BuscarAhora();
                        }
                    });
                    timerbuscar.setRepeats(false); //no queremos que sea repetitivo
                    timerbuscar.start();// el timer comienza a contar
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {//Se invoca cuando  se inserta en el documento escuchado
                ActivoTimer();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {//Se invoca cuando se elimina texto del documento escuchado
                ActivoTimer();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {//Se invoca cuando cambia el documento que escucha
                ActivoTimer();
            }
        };

        return documento;

    }

    //Procedimiento que busca en la tabla algun valor introducido en el jtexfield txt_buscar
    private void BuscarAhora() {
        try {
            negalumnos.setBuscar(dlgalumno.txtBuscar.getText());
            //Notifica a todos los oyentes que todos los valores de celda en las filas de la tabla pueden haber cambiado.
            rsmodel.fireTableDataChanged();

        } catch (Exception e) {
            VentanaMensajeError(e.toString());
        }

    }

    public Alumno getAlumno() {
        //currentAlumno.setNombre(dlgalumno.getTxtNombre().getText());
        return currentAlumno;
    }

    private void Cerrar() {
        dlgalumno.setVisible(false);
    }

    private void VentanaMensajeError(String ex) {
        JOptionPane.showMessageDialog(null, ex, "Excepción", JOptionPane.ERROR_MESSAGE);

    }

    //Control de los ActionListener que estamos controlando (btnAltas, btnSalir, btnBajas, btnModificaciones)
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == dlgalumno.btnAltas) {
            try {
                alumno.setDni(dlgalumno.txtDni.getText());
                alumno.setNombre(dlgalumno.txtNombre.getText());
                alumno.setApellido1(dlgalumno.txtApellido1.getText());
                alumno.setApellido2(dlgalumno.txtApellido2.getText());

                int id = negalumnos.Altas(alumno);
                dlgalumno.txtRegistro.setText(String.valueOf(id));
                //rsmodel.fireTableDataChanged();

            } catch (Exception ex) {
                VentanaMensajeError(ex.toString());
            }

        } else if (e.getSource() == dlgalumno.btnSalir) {
            Cerrar();
        } else if (e.getSource() == dlgalumno.btnBajas) {
            try {
                if (JOptionPane.showConfirmDialog(dlgalumno,
                        "¿Está seguro que desea borrar el registro seleccionado ?",
                        "Atención",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {

                    negalumnos.Borrar(currentAlumno);
                    rsmodel.fireTableDataChanged(); //Notifica a todos los oyentes que todos los valores de celda en las filas de la tabla pueden haber cambiado.

                } else {
                    JOptionPane.showMessageDialog(dlgalumno, "La baja no se ha realizado");
                }

            } catch (Exception ex) {
                VentanaMensajeError(ex.toString());
            }
        } else if (e.getSource() == dlgalumno.btnModificaciones) {
            try {
                /////////////////////////////////////////////////////////////////////////////
                currentAlumno.setDni(dlgalumno.getTxtDni().getText());
                currentAlumno.setNombre(dlgalumno.getTxtNombre().getText());
                currentAlumno.setApellido1(dlgalumno.getTxtApellido1().getText());
                currentAlumno.setApellido2(dlgalumno.getTxtApellido2().getText());
             
                 negalumnos.Modificar(currentAlumno);
                 rsmodel.fireTableDataChanged(); //Notifica a todos los oyentes que todos los valores de celda en las filas de la tabla pueden haber cambiado.
                //también puedo solo actualizar la fila en concreto
                
          

                //Arregla esta funcion
                /////////////////////////////////////////////////////////////////////////////
            } catch (Exception ex) {
                VentanaMensajeError(ex.toString());
            }
        } else if (e.getSource() == dlgalumno.btnSeleccionar) {
            Cerrar();

        } else if (e.getSource() == dlgalumno.getBtnSeleccionar()) {

            dlgalumno.setVisible(false);

        }

    }
}
