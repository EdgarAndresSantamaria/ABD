package modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class GestorBD {
	
	/**
	 * atributos
	 * ConexiónBD debe contener la <ip de la máquina virtual>:<puerto de escucha mysql>/<BD utilizada>
	 * user: no utilizar root y tener en cuenta que debe tener permisos sobre la BD a utilizar
	 * 
	 */
	private static GestorBD miGestorBD;
	private String ConexionBD = "jdbc:mysql://192.168.56.10:3306/example";
	private String user = "cristo";
	private String password = "";
	private Connection CanalBD;
	private Statement Instruccion;
	private ResultSet Resultado;
	
	/**
	 * constructora
	 */
	private GestorBD(){
		try{
			
			CanalBD = DriverManager.getConnection(ConexionBD, user, password);
			Instruccion = CanalBD.createStatement();
		}catch(SQLException e){
			JOptionPane.showMessageDialog(null, "Error en la conexion con BD\nERROR : "+e.getMessage());
		}
	}
		
	/**
	 * main para pruebas
	 * @param args
	 */
	public static void main (String[]args) {
		GestorBD.getGestorBD().Select("Select * from mytable");
	}
	
	/*
	 * devolver la bd
	 */
	public static GestorBD getGestorBD(){
		if(miGestorBD == null){
			miGestorBD = new GestorBD();
		}
		return miGestorBD;
	}
	
	/*
	 * sentencia update, insert y delete
	 */
	public void Update(String SentenciaSQL){
		try{
			Instruccion.executeUpdate(SentenciaSQL);
		}catch(SQLException e){
			JOptionPane.showMessageDialog(null, "Error Al modificar\nERROR : "+e.getMessage());			
		}
	}
	
	/*
	 * sentencias select que lo devuelve en un resulset
	 */
	public ResultSet Select(String SentenciaSQL){
		
		try{
			Resultado = Instruccion.executeQuery(SentenciaSQL);
		}catch(SQLException e){
			JOptionPane.showMessageDialog(null, "Error al cargar los datos\nERROR : "+e.getMessage());			
		}
		
		return Resultado;
	}		
}