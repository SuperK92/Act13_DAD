/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Informes;

import conexion.ClaseDatos;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JDialog;
import javax.swing.JFrame;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author alumno_n
 */
public class Informes {

    //private ClaseDatos _cldatos;

//    public void generarReporte(String titulo, String pathinfo) {
//        try {
//            Connection a;
//            a = _cldatos.getConn();
//
//            JasperReport jasperReport = (JasperReport) JRLoader.loadObjectFromFile(pathinfo);
//
//            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, a);
//            JasperViewer ver = new JasperViewer(jasperPrint);
//            ver.setTitle(titulo);
//            ver.setVisible(true);
//            //this.vista_preliminar(ver);
//
//        } catch (Exception e) {
//            Logger.getLogger(Informes.class.getName()).log(Level.SEVERE, null, e);
//
//        }
//    }
//    
    public void generarReporte(String titulo, String pathinfo) throws SQLException, JRException {
       
            Connection a;
            a = DriverManager.getConnection("jdbc:mysql://localhost:3306/libros","root","1234");

            JasperReport jasperReport = (JasperReport) JRLoader.loadObjectFromFile(pathinfo);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, a);
            JasperViewer ver = new JasperViewer(jasperPrint);
            ver.setTitle(titulo);
            this.vista_preliminar(ver);

        
    }

    private void vista_preliminar(JasperViewer ver) {
        JDialog viewer = new JDialog(new JFrame(), "Vista previa del reporte", true);
        viewer.setSize(900, 600);
        viewer.setLocationRelativeTo(null);
        viewer.getContentPane().add(ver.getContentPane());
        viewer.setVisible(true);
    }
}
