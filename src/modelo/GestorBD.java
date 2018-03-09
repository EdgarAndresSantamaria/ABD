package modelo;

import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import controlador.Fachada;
import interfaz.Cliente;

public class GestorBD {

	/**
	 * atributos ConexiónBD debe contener la <ip de la máquina
	 * virtual>:<puerto de escucha mysql>/<BD utilizada> 
	 * user: no utilizar root
	 * y tener en cuenta que debe tener permisos sobre la BD a utilizar
	 * 
	 */
	private static GestorBD miGestorBD;
	private ResultSet resultado;
	private Connection connection;
	private Statement instruccion;
	private String bd = "auditingAB";

	public String getBD(){
		return bd;
	}
	
	/**
	 * constructora
	 */
	private GestorBD() {

	}

	/**
	 * metodo para recuperar la instancia unica del GestorBD
	 * @return
	 */
	public static GestorBD getGestorBD() {
		if (miGestorBD == null) {
			miGestorBD = new GestorBD();
		}
		return miGestorBD;
	}

	
	/**
	 * metodo para abrir una nueva conexion hacia la BD
	 * @param serverAddress
	 * @param port
	 * @param user
	 * @param password
	 */
	public void OpenConnection(String serverAddress, String port, String user, String password) throws SQLException {
			connection= DriverManager.getConnection("jdbc:mysql://"+serverAddress+":"+port+"/"+bd, user, password);
			instruccion = connection.createStatement();
	}

	/**
	 * metodo para cerrar la actual conexion hacia la BD
	 */
	public void CloseConnection()  throws SQLException  {	
			connection.close();
	}

	/**
	 * metodo update que ejecuta sentencias SQL del tipo update,insert,delete
	 * @param SentenciaSQL
	 */
	public void Update(String SentenciaSQL)  throws SQLException  {
			instruccion.executeUpdate(SentenciaSQL);
	}

	/**
	 * metodo update que ejecuta sentencias SQL del tipo Select
	 * resultado devuelto en formato ResultSet SQL
	 * @param SentenciaSQL
	 * @return
	 * @throws SQLException
	 */
	public ResultSet Select(String SentenciaSQL) throws SQLException {
		resultado = instruccion.executeQuery(SentenciaSQL);
		return resultado;
	}
}