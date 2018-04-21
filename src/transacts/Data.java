package transacts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ThreadLocalRandom;

public class Data {

	static final int NONLOCKING = 0; // se trabaja sin reservas
	static final int LOCKING = 1; // se trabaja utilizando reservas

	static final int SHARE_LOCKING = LOCKING; // RLOCK
	static final int EXCLUSIVE_LOCKING = 2 * LOCKING; // WLOCK

	static final int NUMBER_OF_ITERATIONS = 100; // num de iteraciones por cada
													// transaccion
	static final int NUMBER_OF_THREADS = 3; // numero de hilos maximo

	static final String X = "X";
	static final String Y = "Y";
	static final String Z = "Z";
	static final String T = "T";
	static final String A = "A";
	static final String B = "B";
	static final String C = "C";
	static final String D = "D";
	static final String E = "E";
	static final String F = "F";
	static final String M = "M";

	int SHARE_MODE;
	int EXCLUSIVE_MODE;

	// private config temporal BD Connection
	private String serverAddress = "10.0.2.15";
	private String port = "3306";
	private String bd = "concurrency_control";
	private String user = "concurrency_control";
	private String password = "hola";
	private Connection conn;
	private Statement st;
	private String sentence;
	private ResultSet resultado;

	public Data(int myShareMode, int myExclusiveMode) {
		SHARE_MODE = myShareMode;
		EXCLUSIVE_MODE = myExclusiveMode;

		// Load MySQL driver
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Open connection
		try {
			conn = DriverManager.getConnection("jdbc:mysql://" + serverAddress + ":" + port + "/" + bd, user, password);
			conn.setAutoCommit(false);
			st = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private int getValue(int nonlocking2, String x2) {// CAMBIAR MODO (SIN
														// TERMINAR)
		// recuperar valor contenido en variable 'x2'
		int result = -1;
		try {
			sentence = "Select value from variable where name = '" + x2 + "' ";
			if (nonlocking2 == SHARE_LOCKING) {//si reserva exclusiva..
				System.out.println("reservada en exclusiva... "+x2);
				sentence += "for update;";
			} else if(nonlocking2 == EXCLUSIVE_LOCKING) {//si reserva compartida..
				System.out.println("reservada en compartido... "+x2);
				sentence += "for share ;";
			}else {//sin reservas...
				System.out.println("recuperada sin reservas... "+x2);
				sentence += ";";
			}
			resultado = st.executeQuery(sentence);
			if (resultado.next()) {
				result = resultado.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	private void setValue(String variable, int value) {// CAMBIAR
																	// MODO (SIN
																	// TERMINAR)
		// update de la 'variable' con el nuevo 'value'
		try {
			sentence = "UPDATE variable SET value= " + value + " where name= '"+ variable +"';";
			st.executeUpdate(sentence);
			//String SentenciaSQL = "UPDATE variable SET value= " + value + " where name= '" + variable + "';";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void increaseBarrier() {
		// hacer una query que incremente M en la BD
		Integer mValue = getValue(EXCLUSIVE_MODE, M);
		System.out.println(mValue);
		mValue ++;
		setValue(M, mValue);
		System.out.println("WRITE( " + M + "," + Integer.toString(mValue - 1) + "," + Integer.toString(mValue) + ")");
	}

	private void decreaseBarrier() {
		// hacer una query que decremente M de la BD
		Integer mValue;
		mValue = getValue(EXCLUSIVE_MODE, M);
		mValue --;
		setValue(M, mValue);
		System.out.println("WRITE( " + M + "," + Integer.toString(mValue + 1) + "," + Integer.toString(mValue) + ")");
	}

	private int getBarrierValue() {
		return getValue(SHARE_MODE, M);
	}
		
	public void initializeSharedVariables() {
		// codigo que inicialice x,y,z,t,a,b,c,d,e,f,m a 0
		try {
			String SentenciaSQL = "UPDATE `variable` SET `value`= 0;";
			st.executeUpdate(SentenciaSQL);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void synchronyze() {
		int barrierValue;

		// incrementa la m en 1
		increaseBarrier();
		barrierValue = getBarrierValue();
		while (barrierValue < Data.NUMBER_OF_THREADS) {
			try {
				Thread.sleep(ThreadLocalRandom.current().nextInt(1, 11));
				barrierValue = getBarrierValue();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public void finish() {
		// decrementa la m en 1
		decreaseBarrier();
	}

	public Boolean procedureA(String myName, int counter) throws SQLException {
		try {
			// generar codigo procedimiento A
			String name = myName;
			int i = counter;
			Integer xValue, tValue, aValue, yValue;
			xValue = getValue(EXCLUSIVE_MODE, X);
			System.out.println("Antes:"+xValue);
			xValue = xValue + 1;

			System.out.println("Despues:"+xValue);
			setValue( X, xValue);
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + X + "," + Integer.toString(xValue - 1)
					+ "," + Integer.toString(xValue) + ")");
			tValue = getValue(EXCLUSIVE_MODE, T);
			aValue = getValue(EXCLUSIVE_MODE, A);
			yValue = getValue(SHARE_MODE, Y);
			tValue = tValue + yValue;
			aValue = aValue + yValue;
			setValue(T, tValue);
			setValue(A, aValue);
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + T + ","
					+ Integer.toString(tValue - yValue) + "," + Integer.toString(tValue) + ")");
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + A + ","
					+ Integer.toString(aValue - yValue) + "," + Integer.toString(aValue) + ")");
			System.out.println("END_TRANSACTION" + name + Integer.toString(i + 1));
			conn.commit();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			conn.rollback();
			return false;
		}
	}

	public Boolean procedureB(String myName, int counter) throws SQLException {
		try {
			// generar codigo procedimiento B
			String name = myName;
			int i = counter;
			Integer yValue, tValue, bValue, zValue;
			yValue = getValue(EXCLUSIVE_MODE, Y);
			yValue = yValue + 1;
			setValue(Y, yValue);
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + Y + "," + Integer.toString(yValue - 1)
					+ "," + Integer.toString(yValue) + ")");
			tValue = getValue(EXCLUSIVE_MODE, T);
			bValue = getValue(EXCLUSIVE_MODE, B);
			zValue = getValue(SHARE_MODE, Z);
			tValue = tValue + zValue;
			bValue = bValue + zValue;
			setValue(T, tValue);
			setValue(B, bValue);
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + T + ","
					+ Integer.toString(tValue - zValue) + "," + Integer.toString(tValue) + ")");
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + B + ","
					+ Integer.toString(bValue - zValue) + "," + Integer.toString(bValue) + ")");
			System.out.println("END_TRANSACTION" + name + Integer.toString(i + 1));
			conn.commit();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			conn.rollback();
			return false;
		}

	}

	public Boolean procedureC(String myName, int counter) throws SQLException {
		try {
			// generar codigo procedimiento C
			String name = myName;
			int i = counter;
			Integer zValue, tValue, cValue, xValue;
			zValue = getValue(EXCLUSIVE_MODE, Z);
			zValue = zValue + 1;
			setValue( Z, zValue);
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + Z + "," + Integer.toString(zValue - 1)
					+ "," + Integer.toString(zValue) + ")");
			tValue = getValue(EXCLUSIVE_MODE, T);
			cValue = getValue(EXCLUSIVE_MODE, C);
			xValue = getValue(SHARE_MODE, X);
			tValue = tValue + xValue;
			cValue = cValue + xValue;
			setValue(T, tValue);
			setValue(C, cValue);
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + T + ","
					+ Integer.toString(tValue - xValue) + "," + Integer.toString(tValue) + ")");
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + B + ","
					+ Integer.toString(cValue - xValue) + "," + Integer.toString(cValue) + ")");
			System.out.println("END_TRANSACTION" + name + Integer.toString(i + 1));
			conn.commit();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			conn.rollback();
			return false;
		}
	}

	public Boolean procedureD(String myName, int counter) throws SQLException {
		// generar codigo procedimiento D
		try {
			String name = myName;
			int i = counter;
			Integer zValue, dValue, tValue, xValue;
			tValue = getValue(EXCLUSIVE_MODE, T);
			dValue = getValue(EXCLUSIVE_MODE, D);
			zValue = getValue(SHARE_MODE, Z);
			tValue = tValue + zValue;
			dValue = dValue + zValue;
			setValue(T, tValue);
			setValue(D, dValue);
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + T + ","
					+ Integer.toString(tValue - zValue) + "," + Integer.toString(tValue) + ")");
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + D + ","
					+ Integer.toString(dValue - zValue) + "," + Integer.toString(dValue) + ")");
			xValue = getValue(EXCLUSIVE_MODE, X);
			xValue = xValue - 1;
			setValue(X, xValue);
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + X + "," + Integer.toString(xValue + 1)
					+ "," + Integer.toString(xValue) + ")");
			System.out.println("END_TRANSACTION" + name + Integer.toString(i + 1));

			conn.commit();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			conn.rollback();
			return false;
		}

	}

	public Boolean procedureE(String myName, int counter) throws SQLException {
		// generar codigo procedimiento E
		try {
			String name = myName;
			int i = counter;
			Integer tValue, eValue, xValue, yValue;
			tValue = getValue(EXCLUSIVE_MODE, T);
			eValue = getValue(EXCLUSIVE_MODE, E);
			xValue = getValue(SHARE_MODE, X);
			tValue = tValue + xValue;
			eValue = eValue + xValue;
			setValue(T, tValue);
			setValue(E, eValue);
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + T + ","
					+ Integer.toString(tValue - xValue) + "," + Integer.toString(tValue) + ")");
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + E + ","
					+ Integer.toString(eValue - xValue) + "," + Integer.toString(eValue) + ")");
			yValue = getValue(EXCLUSIVE_MODE, Y);
			yValue = yValue - 1;
			setValue(Y, yValue);
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + Y + "," + Integer.toString(yValue + 1)
					+ "," + Integer.toString(yValue) + ")");
			System.out.println("END_TRANSACTION" + name + Integer.toString(i + 1));
			conn.commit();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			conn.rollback();
			return false;
		}
	}

	public Boolean procedureF(String myName, int counter) throws SQLException {
		// generar codigo procedimiento F
		try {
			String name = myName;
			int i = counter;
			Integer tValue, fValue, yValue, zValue;
			tValue = getValue(EXCLUSIVE_MODE, T);
			fValue = getValue(EXCLUSIVE_MODE, F);
			yValue = getValue(SHARE_MODE, Y);
			tValue = tValue + yValue;
			fValue = fValue + yValue;
			setValue(T, tValue);
			setValue(F, fValue);
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + T + ","
					+ Integer.toString(tValue - yValue) + "," + Integer.toString(tValue) + ")");
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + E + ","
					+ Integer.toString(fValue - yValue) + "," + Integer.toString(fValue) + ")");
			zValue = getValue(EXCLUSIVE_MODE, Z);
			zValue = zValue - 1;
			setValue(Z, zValue);
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + Z + "," + Integer.toString(zValue + 1)
					+ "," + Integer.toString(zValue) + ")");
			System.out.println("END_TRANSACTION" + name + Integer.toString(i + 1));
			conn.commit();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			conn.rollback();
			return false;
		}
	}

	public void showInitialValues() {
		System.out.println("Initial value of " + X + ": " + Integer.toString(getValue(NONLOCKING, X)));
		System.out.println("Initial value of " + Y + ": " + Integer.toString(getValue(NONLOCKING, Y)));
		System.out.println("Initial value of " + Z + ": " + Integer.toString(getValue(NONLOCKING, Z)));
		System.out.println("Initial value of " + T + ": " + Integer.toString(getValue(NONLOCKING, T)));
		System.out.println("Initial value of " + A + ": " + Integer.toString(getValue(NONLOCKING, A)));
		System.out.println("Initial value of " + B + ": " + Integer.toString(getValue(NONLOCKING, B)));
		System.out.println("Initial value of " + C + ": " + Integer.toString(getValue(NONLOCKING, C)));
		System.out.println("Initial value of " + D + ": " + Integer.toString(getValue(NONLOCKING, D)));
		System.out.println("Initial value of " + E + ": " + Integer.toString(getValue(NONLOCKING, E)));
		System.out.println("Initial value of " + F + ": " + Integer.toString(getValue(NONLOCKING, F)));
	}

	public Boolean showFinalValues() {
		int barrierValue;

		barrierValue = getBarrierValue();

		while (barrierValue < 1) {
			try {
				Thread.sleep(ThreadLocalRandom.current().nextInt(1, 11));
				barrierValue = getBarrierValue();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		while (barrierValue > 0) {
			try {
				Thread.sleep(ThreadLocalRandom.current().nextInt(1, 11));
				barrierValue = getBarrierValue();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Final value of " + X + ": " + Integer.toString(getValue(NONLOCKING, X)));
		System.out.println("Final value of " + X + ": " + Integer.toString(getValue(NONLOCKING, Y)));
		System.out.println("Final value of " + X + ": " + Integer.toString(getValue(NONLOCKING, Z)));
		System.out.println("Final value of " + X + ": " + Integer.toString(getValue(NONLOCKING, T)));
		System.out.println("Final value of " + X + ": " + Integer.toString(getValue(NONLOCKING, A)));
		System.out.println("Final value of " + X + ": " + Integer.toString(getValue(NONLOCKING, B)));
		System.out.println("Final value of " + X + ": " + Integer.toString(getValue(NONLOCKING, C)));
		System.out.println("Final value of " + X + ": " + Integer.toString(getValue(NONLOCKING, D)));
		System.out.println("Final value of " + X + ": " + Integer.toString(getValue(NONLOCKING, E)));
		System.out.println("Final value of " + X + ": " + Integer.toString(getValue(NONLOCKING, F)));

		System.out.println("Expected final value of " + X + ": " + Integer.toString(0));
		System.out.println("Expected final value of " + Y + ": " + Integer.toString(0));
		System.out.println("Expected final value of " + Z + ": " + Integer.toString(0));
		System.out
				.println(
						"Expected final value of " + T + ": "
								+ Integer.toString(getValue(NONLOCKING, A) + getValue(NONLOCKING, B)
										+ getValue(NONLOCKING, C) + getValue(NONLOCKING, D) + getValue(NONLOCKING, E)
										+ getValue(NONLOCKING, F)));

		return true;

	}

}
