package controlador;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mysql.jdbc.ResultSetMetaData;

import interfaz.Cliente;
import modelo.GestorBD;

public class Fachada {
	private static Fachada instancia;

	private Fachada() {

	}

	public static Fachada getInstancia() {
		if(instancia==null)	{
			instancia=new Fachada();
		}
		return instancia;
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
			throwException("conexion establecida");
		} catch (Exception e) {
			throwException(e.getMessage());
		}
	}

	/**
	 * metodo para cerrar la actual conexion hacia la BD
	 */
	public void CloseConnection() {
		try {
			GestorBD.getGestorBD().CloseConnection();
		} catch (Exception e) {
			throwException(e.getMessage());
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
			throwException(e.getMessage());
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
			throwException(e.getMessage());		
		}

		return json;
	}


	/**
	 * metodo para introducir un alto n�mero de registros en (alrededor de 10000 tuplas) 
	 * en cada tabla (tableA y tableB / tableC y tableD).
	 */
	public void SlowQuery(){
		//para cada n entre 0, 100 ......
		if(!GestorBD.getGestorBD().getBD().equals("auditingCD")) {
			SlowQuery("A",10000);
			SlowQuery("B",10000);
		}else {
			SlowQuery("C",10000);
			SlowQuery("D",10000);
		}
	}
	
	/**
	 * metodo SlowQuery parametrizado
	 * @param letra
	 * @param upperBound
	 */
	private void SlowQuery(String letra,int upperBound) {
		//para cada n entre 0, upperBound ......
		  IntStream.range(0, upperBound).forEachOrdered(n -> {
			    try {
			    	int primerInt=ThreadLocalRandom.current().nextInt(1, upperBound);
			    	int segundoInt=ThreadLocalRandom.current().nextInt(1, upperBound);
			    	//insertar una tupla (n,n) en la tabla 'tabla[letra]'
					GestorBD.getGestorBD().Update("insert into table"+letra+" values("+primerInt+","+segundoInt+");");
				} catch (SQLException e) {
					// lanzar excepcion
					throwException(e.getMessage());		
				}
			});	
	}
	/**
	 * metodo de redireccion de errores hacia la interfaz
	 * @param error
	 */
	private void throwException(String error) {
		Cliente.getCliente().throwException(error);
	}
}
