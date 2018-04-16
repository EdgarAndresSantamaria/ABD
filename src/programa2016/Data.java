import java.sql.*;

public class Data {

	Connection conn;
	Statement st;
	String Sentence;

	static final int LOCKING = 1;
	static final int NONLOCKING = 0;
	static final int NUMBER_OF_ITERATIONS = 100;

	public Data() {
		// Cargar MYSQL DRIVER

		try {
			Class.forName("org.gjt.mm.mysql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		// open connection

		try {
			conn = DriverManager.getConnection("jdbc:mysql://dbaserver1516.cloudapp.net:3306", "prueba", "prueba1");
			conn.setAutoCommit(false);
			st = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// metodos
	public void actualizar(String consulta) {
		try { // cuando haces insert o update
			st.execute(consulta);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ResultSet consulta(String consulta) {
		ResultSet result = null; // cuando haces select
		try {
			result = st.executeQuery(consulta);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public Boolean showInitialValues(String name) {
		try {
			System.out.println("Valor de X: " + getXValue(LOCKING));
			System.out.println("Valor de Y:" + getYValue(LOCKING));
			System.out.println("Valor de Z" + getZValue(LOCKING));
			//LO MISMO PARA Y y Z tambien en el final values
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return true;
	}

	public Boolean showFinalValues(String name) {

		int valor = 0;
		ResultSet result = consulta("Select value from concurrency_control.variables where name='" + name + "';");
		try {
			while (result.next()) {
				valor = result.getInt("value");
				System.out.println("La transaccion " + name + " tiene el valor " + valor);
				System.out.println("Valor de x" + getXValue(LOCKING));
				System.out.println("Valor de Y" + getYValue(LOCKING));
				System.out.println("valor de z" + getZValue(LOCKING));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;

	}

	public Boolean transactionA(String name, int i, int mode) {
		try {
			int xValue = getXValue(mode);
			setXValue(xValue + 1);
			System.out.println("WRITE(" + name + Integer.toString(i + 1) + ",X," + Integer.toString(xValue) + ","
					+ Integer.toString(xValue + 1) + ")");

			int yValue = getYValue(mode);
			setYValue(yValue + 1);
			System.out.println("WRITE(" + name + Integer.toString(i + 1) + ",Y," + Integer.toString(yValue) + ","
					+ Integer.toString(yValue + 1) + ")");
			this.commit();
			System.out.println("COMMIT(" + name + i + ")");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			this.rollback();
			System.out.println("ROLLBACK(" + name + i + ")");
			return false;
		}

	}

	public Boolean transactionB(String name, int i, int mode) {
		try {
			int yValue = getYValue(mode);
			setYValue(yValue + 1);
			System.out.println("WRITE(" + name + Integer.toString(i + 1) + ",Y," + Integer.toString(yValue) + ","
					+ Integer.toString(yValue + 1) + ")");

			int zValue = getZValue(mode);
			setZValue(zValue + 1);
			System.out.println("WRITE(" + name + Integer.toString(i + 1) + ",Z," + Integer.toString(zValue) + ","
					+ Integer.toString(yValue + 1) + ")");
			this.commit();
			System.out.println("COMMIT(" + name + i + ")");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			this.rollback();
			System.out.println("ROLLBACK(" + name + i + ")");
			return false;
		}

	}
	public Boolean transactionC(String name, int i, int mode) {
		try {
			int zValue = getZValue(mode);
			setZValue(zValue + 1);
			System.out.println("WRITE(" + name + Integer.toString(i + 1) + ",Z," + Integer.toString(zValue) + ","
					+ Integer.toString(zValue + 1) + ")");

			int xValue = getXValue(mode);
			setXValue(xValue + 1);
			System.out.println("WRITE(" + name + Integer.toString(i + 1) + ",X," + Integer.toString(xValue) + ","
					+ Integer.toString(xValue + 1) + ")");
			this.commit();
			System.out.println("COMMIT(" + name + i + ")");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			this.rollback();
			System.out.println("ROLLBACK(" + name + i + ")");
			return false;
		}

	}

	private void setZValue(int i) throws SQLException {

		this.actualizar("update concurrency_control.variables SET value=" + i + " where name='Z'");

	}


	private int getZValue(int mode) throws SQLException {
		String sql = "select value from concurrency_control.variables where name ='Z'";
		if (mode == LOCKING) {
			sql += " for update;";
		}
		ResultSet result = this.consulta(sql);
		result.next();
		int z = result.getInt("value");
		return z;
	}

	private void setXValue(int i) throws SQLException {

		this.actualizar("update concurrency_control.variables SET value=" + i + " where name='X'");

	}

	private void setYValue(int i) throws SQLException {

		this.actualizar("update concurrency_control.variables SET value=" + i + " where name='Y'");

	}

	private int getXValue(int mode) throws SQLException {
		String sql = "select value from concurrency_control.variables where name ='X'";
		if (mode == LOCKING) {
			sql += " for update;";
		}
		ResultSet result = this.consulta(sql);
		result.next();
		int x = result.getInt("value");
		return x;
	}

	private int getYValue(int mode) throws SQLException {
		String sql = "select value from concurrency_control.variables where name ='Y'";
		if (mode == LOCKING) {
			sql += " for update;";
		}
		ResultSet result = this.consulta(sql);
		result.next();
		int y = result.getInt("value");
		return y;
	}

	private void commit() {
		// TODO Auto-generated method stub
		try {
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void rollback() {
		try {
			conn.rollback();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void logOut() {
		try {
			conn.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public Boolean initializeSharedVariables(){
		
		try {
			this.setXValue(0);
			this.setYValue(0);
			this.setZValue(0);
			System.out.println("X = " + this.getXValue(LOCKING));
			System.out.println("Y = " + this.getYValue(LOCKING));
			System.out.println("Z = " + this.getZValue(LOCKING));
			this.commit();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
			
			
		}
		
	}

}
