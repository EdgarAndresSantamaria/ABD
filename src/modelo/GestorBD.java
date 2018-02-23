package modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class GestorBD {

	/**
	 * atributos ConexiónBD debe contener la <ip de la máquina
	 * virtual>:<puerto de escucha mysql>/<BD utilizada> user: no utilizar root
	 * y tener en cuenta que debe tener permisos sobre la BD a utilizar
	 * 
	 */
	private static GestorBD miGestorBD;
	private ResultSet resultado;
	private Connection connection;
	private Statement instruccion;

	
	/**
	 * constructora
	 */
	private GestorBD() {

	}

	/*
	 * devolver GestorBD
	 */
	public static GestorBD getGestorBD() {
		if (miGestorBD == null) {
			miGestorBD = new GestorBD();
		}
		return miGestorBD;
	}

	/*
	 * Crear conexion con la base de datos
	 */
	public String OpenConnection(String serverAddress, String port, String user, String password) {
		String msg = "";
		try {
			connection= DriverManager.getConnection("jdbc:mysql://"+serverAddress+":"+port, user, password);
			connection.setAutoCommit(true);
			instruccion = connection.createStatement();
			msg = "Conexion establecida correctamente.";
		} catch (SQLException e) {
			e.printStackTrace();
			msg = e.getMessage();
		}
		return msg;
	}

	/*
	 * Cerrar conexion con la base de datos
	 */
	public String CloseConnection() {
		String msg = "";
		try {
			connection.close();
			msg = "Conexion cerrada correctamente.";

		} catch (SQLException e) {
			e.printStackTrace();
			msg = e.getMessage();
		}
		return msg;
	}

	/*
	 * Sentencia update, insert y delete
	 */
	public String Update(String SentenciaSQL) {
		String msg ;
		try {
			int num = instruccion.executeUpdate(SentenciaSQL);
			msg = Integer.toString(num);
		}  catch (SQLException e) {
			e.printStackTrace();
			msg = e.getMessage();
		}
		return msg;
	}

	/*
	 * Sentencias select que lo devuelve en un resulset
	 */
	public ResultSet Select(String SentenciaSQL) {
		try {
			resultado = instruccion.executeQuery(SentenciaSQL);
		}  catch (SQLException e) {
			e.printStackTrace();
		}
		return resultado;
	}
}