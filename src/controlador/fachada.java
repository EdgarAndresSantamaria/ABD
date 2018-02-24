package controlador;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mysql.jdbc.ResultSetMetaData;

import interfaz.Cliente;
import modelo.GestorBD;

public class fachada {
	private static fachada instancia;

	private fachada() {

	}

	public static fachada getInstancia() {
		if(instancia==null)	{
			instancia=new fachada();
		}
		return instancia;
	}

	/**
	 * metodo para cerrar la actual conexion hacia la BD
	 */
	public void CloseConnection() {
		try {
			GestorBD.getGestorBD().CloseConnection();
		} catch (Exception e) {
			throwException("error al cerrar conexion");
		}
	}

	/**
	 * metodo update que ejecuta sentencias SQL del tipo update,insert,delete
	 * @param SentenciaSQL
	 */
	public void Update(String SentenciaSQL) {

		try {
			GestorBD.getGestorBD().Update(SentenciaSQL);
		} catch (Exception e) {
			throwException("error al ejecutar execute");
		}

	}
	/**
	 * metodo para abrir una nueva conexion hacia la BD
	 * @param serverAddress
	 * @param port
	 * @param user
	 * @param password
	 */
	public void OpenConnection(String serverAddress, String port, String user, String password) {
		try {
			GestorBD.getGestorBD().OpenConnection(serverAddress, port, user, password);
		} catch (Exception e) {
			throwException("error al establecer la conexion");
		}
	}

	/**
	 * metodo update que ejecuta sentencias SQL del tipo Select
	 * resultado devuelto en formato JSON
	 * @param SentenciaSQL
	 * @return
	 */
	public JSONArray Select(String SentenciaSQL) {
		JSONArray json = new JSONArray();
		try {
			ResultSet rs = GestorBD.getGestorBD().Select(SentenciaSQL);
			ResultSetMetaData rsmd =  (ResultSetMetaData) rs.getMetaData();

			while(rs.next()) {
				int numColumns = rsmd.getColumnCount();
				JSONObject obj = new JSONObject();

				for (int i=1; i<numColumns+1; i++) {
					String column_name = rsmd.getColumnName(i);

					if(rsmd.getColumnType(i)==java.sql.Types.ARRAY){
						obj.put(column_name, rs.getArray(column_name));
					}
					else if(rsmd.getColumnType(i)==java.sql.Types.BIGINT){
						obj.put(column_name, rs.getInt(column_name));
					}
					else if(rsmd.getColumnType(i)==java.sql.Types.BOOLEAN){
						obj.put(column_name, rs.getBoolean(column_name));
					}
					else if(rsmd.getColumnType(i)==java.sql.Types.BLOB){
						obj.put(column_name, rs.getBlob(column_name));
					}
					else if(rsmd.getColumnType(i)==java.sql.Types.DOUBLE){
						obj.put(column_name, rs.getDouble(column_name)); 
					}
					else if(rsmd.getColumnType(i)==java.sql.Types.FLOAT){
						obj.put(column_name, rs.getFloat(column_name));
					}
					else if(rsmd.getColumnType(i)==java.sql.Types.INTEGER){
						obj.put(column_name, rs.getInt(column_name));
					}
					else if(rsmd.getColumnType(i)==java.sql.Types.NVARCHAR){
						obj.put(column_name, rs.getNString(column_name));
					}
					else if(rsmd.getColumnType(i)==java.sql.Types.VARCHAR){
						obj.put(column_name, rs.getString(column_name));
					}
					else if(rsmd.getColumnType(i)==java.sql.Types.TINYINT){
						obj.put(column_name, rs.getInt(column_name));
					}
					else if(rsmd.getColumnType(i)==java.sql.Types.SMALLINT){
						obj.put(column_name, rs.getInt(column_name));
					}
					else if(rsmd.getColumnType(i)==java.sql.Types.DATE){
						obj.put(column_name, rs.getDate(column_name));
					}
					else if(rsmd.getColumnType(i)==java.sql.Types.TIMESTAMP){
						obj.put(column_name, rs.getTimestamp(column_name));   
					}
					else{
						obj.put(column_name, rs.getObject(column_name));
					}
				}

				json.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throwException("error al ejecutar query");		
		}

		return json;
	}


	/**
	 * método de redireccion de errores hacia la interfaz
	 * @param error
	 */
	private void throwException(String error) {
		Cliente.getCliente().throwException(error);
	}
}
