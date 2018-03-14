/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author jj
 */
public class ClaseDatos {

    private Connection conn = null; //objeto conection
    private ResultSet rs = null; //objeto dataset
    private final String _servidor;       // Servidor
    private final String _puerto;        //Puerto por el que escucha MySql
    private final String _basedatos;   // Base de datos
    private final String _usuario;    // Almacena el usuario de la base de datos
    private final String _clave;      // Almacena la clave de la base de datos

    public ClaseDatos() {

        _servidor = "localhost";
        _puerto="3306";
        _basedatos = "libros";
        _usuario = "root";
        _clave = "1234";
        conn = null;
    }

    public ClaseDatos(String servidor,String puerto, String basedatos, String usuario, String clave) {

        _servidor = servidor;
        _puerto=puerto;
        _basedatos = basedatos;
        _usuario = usuario;
        _clave = clave;
        conn = null;
    }

    private boolean AbrirConexion() throws Exception {
        // Comprobamos si la conexión actual es correcta.
        try {
            if ((conn != null) && conn.isValid(2000)) {
                // Como es correcta devolvemos verdadero y salimos
                return true;
            }
        } catch (SQLException e) {
            // No hacemos nada... se va a resetear la conexión
        }
        // No es una conexión correcta así que la volvemos a construir
        conn = null;

        /*
         *   // Es necesario useServerPrepStmts=true para que los PreparedStatement
             // se hagan en el servidor de bd. Si no lo ponemos, funciona todo
             // igual, pero los PreparedStatement se convertirán internamente a
             // Statements.
                Connection conexion = DriverManager.getConnection(
                "jdbc:mysql://servidor/basedatos?useServerPrepStmts=true",
                "usuario", "password");
         */
 
        // Creamos la conexión a la base de datos

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        String sUrl = "jdbc:mysql://" + _servidor + ":"+ _puerto +"/" + _basedatos + "?useServerPrepStmts=true&zeroDateTimeBehavior=convertToNull";
        conn = DriverManager.getConnection(sUrl, _usuario, _clave);
      
        // Comprobamos si es válida
        if (!conn.isValid(2000)) {
            // No es una conexión válida. La cerramos y devolvemos falso
            conn.close();
            conn = null;
        } else {
            // Es una conexión válida. Devolvemos verdadero
            return true;
        }

        return false;
    }

      public void Ejecutar_Consulta(String sql) throws Exception {
        if (AbrirConexion()) {
            //ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY'
                Statement stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);
            
           //  PreparedStatement pstmt=conn.prepareStatement(sql);
          //  rs=pstmt.executeQuery();
                
        }
    }

    public ResultSet getRs() {
        return rs;
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    public int Ejecutar_Actualizacion(String sql) throws Exception {
              
           //Arregla  la función
        if (AbrirConexion()) {
            // Es una conexión válida.
            // Creamos el Statement
            Statement stmt = conn.createStatement();
            return stmt.executeUpdate(sql);
            //stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            //ResultSet clave=stmt.getGeneratedKeys(); //Le asigno la clave autogenerada
            //int id = clave.getInt( 1 );
           
            //return id;
        }
        return -1;
    }
    //////////////////////////////////////////////////////////////////////////////////// 

 
      public int Ejecutar_Insercion(String sql) throws Exception {
        // Comprobamos la conexión
        if (AbrirConexion()) {
            // Es una conexión válida.
            // Creamos el Statement
            Statement stmt = conn.createStatement();
            // Ejecutamos la query
            stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS); //Para recuperar la clave autogenerada
            ResultSet clave=stmt.getGeneratedKeys(); //Le asigno la clave autogenerada
            int id = -1;
            if( clave.next() )
            {
                id = clave.getInt( 1 );
            }
            return id;
        }
        return -1;
    }
      

      
        public void Cerrar()
    {
        try {
            conn.close();
        }
        catch( SQLException e )
        {
     
        }
        conn = null;
    }
      
  // <editor-fold defaultstate="collapsed" desc="Poner la Solución para lo de inyección de código SQL">  
       public PreparedStatement PrepareSmt( String query ) throws Exception
    {
        // Comprobamos la conexión
        if( AbrirConexion() )
        {
            // Es una conexión válida.
            // Creamos el Statement y lo devolvemos
            return conn.prepareStatement( query );
        }
        return null;
    }

    /**
     * Preparamos una query
     * @param query la query a preparar
     * @return la query preparada
     * @throws java.lang.Exception
     */
    public PreparedStatement PrepareSmtAltas( String query ) throws Exception
    {
        // Comprobamos la conexión
        if( AbrirConexion() )
        {
            // Es una conexión válida.
            // Creamos el Statement y lo devolvemos
            return conn.prepareStatement( query, Statement.RETURN_GENERATED_KEYS );
        }
        return null;
    }

       // </editor-fold>   
    
    
     public Connection getConn() throws Exception 
    {
        //esto se ha añadido nuevo para poder utilizar esta conección con el listado
        AbrirConexion();
        return conn;
    }
}
