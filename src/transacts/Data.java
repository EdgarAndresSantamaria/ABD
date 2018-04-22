package transacts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ThreadLocalRandom;

public class Data {

	static final int NONLOCKING = 0; // Sin reserva
	static final int LOCKING = 1; // Con reserva

	static final int SHARE_LOCKING = LOCKING; // RLOCK
	static final int EXCLUSIVE_LOCKING = 2 * LOCKING; // WLOCK

	static final int NUMBER_OF_ITERATIONS = 100; // n�vueltas por procedimiento
	static final int NUMBER_OF_THREADS = 6; // n� max de hilos

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

	private Connection conn;
	private Statement st;
	private String sentence;
	private ResultSet resultado;

	// datos para la conexion con la BD
	private String serverAddress = "192.168.56.10";
	private String port = "3306";
	private String bd = "concurrency_control";
	private String user = "concurrency_control";
	private String password = "hola";

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

		// abrir conexion
		try {
			conn = DriverManager.getConnection("jdbc:mysql://" + serverAddress + ":" + port + "/" + bd, user, password);
			conn.setAutoCommit(false);
			st = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// obtener valor de 'variable' de la BD
	private int getValue(int mode, String variable) throws SQLException {
		int result = -1;
			sentence = "Select value from variables where name = '" + variable + "' ";
			if (mode == EXCLUSIVE_LOCKING) {// exclusiva..
				sentence += "for update;";
			} else if (mode == SHARE_LOCKING) {// compartida..
				sentence += "lock in share mode;";
			} else {// sin reservas...
				sentence += ";";
			}
			resultado = st.executeQuery(sentence);
			if (resultado.next()) {
				result = resultado.getInt("value");
			}
		return result;
	}

	// guardar valor de la variable en la BD
	private void setValue(int mode, String variable, int value) throws SQLException {
	
			if (mode == SHARE_LOCKING) {
				throw new SQLException();
			}
			sentence = "UPDATE variables SET value= " + value + " where name= '" + variable + "'; ";
			st.executeUpdate(sentence);
	}

	// incrementar M en la BD
	private void increaseBarrier() {
		try {
			Integer mValue = getValue(2, M);//siempre se reserva en exclusiva
			mValue = mValue + 1;
			setValue(2, M, mValue);//siempre se reserva en exclusiva
			System.out
					.println("WRITE( " + M + "," + Integer.toString(mValue - 1) + "," + Integer.toString(mValue) + ")");
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	// decrementar M de la BD
	private void decreaseBarrier() {
		try {
			Integer mValue;

			mValue = getValue(2, M);//siempre se reserva en exclusiva
			mValue = mValue - 1;
			setValue(2, M, mValue);
			System.out
					.println("WRITE( " + M + "," + Integer.toString(mValue + 1) + "," + Integer.toString(mValue) + ")");
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	// obtener el valor de M de la BD
	private int getBarrierValue() {
		int resultado=-1;
		try {
			 resultado=getValue(2, M);//siempre se reserva en exclusiva
			 conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return resultado;
	}

	// poner a 0 todos los valores de la BD
	public void initializeSharedVariables() {
		try {
			String SentenciaSQL = "UPDATE `variables` SET `value`= 0;";
			st.executeUpdate(SentenciaSQL);
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public void synchronyze() {
		int barrierValue;
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
		decreaseBarrier();
	}

	public Boolean procedureA(String myName, int counter)  {
		try {
			String name = myName;
			int i = counter;
			Integer xValue, tValue, aValue, yValue;
			xValue = getValue(EXCLUSIVE_MODE, X);
			xValue = xValue + 1;
			setValue(EXCLUSIVE_MODE, X, xValue);
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + X + "," + Integer.toString(xValue - 1)
					+ "," + Integer.toString(xValue) + ")");
			tValue = getValue(EXCLUSIVE_MODE, T);
			aValue = getValue(EXCLUSIVE_MODE, A);
			yValue = getValue(SHARE_MODE, Y);
			tValue = tValue + yValue;
			//tValue = tValue + 1;
			aValue = aValue + yValue;
			setValue(EXCLUSIVE_MODE, T, tValue);
			setValue(EXCLUSIVE_MODE, A, aValue);
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + T + ","
					+ Integer.toString(tValue - yValue) + "," + Integer.toString(tValue) + ")");
			/*System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + T + ","
					+ Integer.toString(tValue - 1) + "," + Integer.toString(tValue) + ")");*/
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + A + ","
					+ Integer.toString(aValue - yValue) + "," + Integer.toString(aValue) + ")");
			System.out.println("END_TRANSACTION" + name + Integer.toString(i + 1));
			conn.commit();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return false;
		}
	}

	public Boolean procedureB(String myName, int counter){
		try {
			String name = myName;
			int i = counter;
			Integer yValue, tValue, bValue, zValue;
			yValue = getValue(EXCLUSIVE_MODE, Y);
			yValue = yValue + 1;
			setValue(EXCLUSIVE_MODE, Y, yValue);
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + Y + "," + Integer.toString(yValue - 1)
					+ "," + Integer.toString(yValue) + ")");
			tValue = getValue(EXCLUSIVE_MODE, T);
			bValue = getValue(EXCLUSIVE_MODE, B);
			zValue = getValue(SHARE_MODE, Z);
			tValue = tValue + zValue;
			//tValue = tValue + 1;
			bValue = bValue + zValue;
			setValue(EXCLUSIVE_MODE, T, tValue);
			setValue(EXCLUSIVE_MODE, B, bValue);
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + T + ","
					+ Integer.toString(tValue - zValue) + "," + Integer.toString(tValue) + ")");
			/*System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + T + ","
					+ Integer.toString(tValue - 1) + "," + Integer.toString(tValue) + ")");*/
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + B + ","
					+ Integer.toString(bValue - zValue) + "," + Integer.toString(bValue) + ")");
			System.out.println("END_TRANSACTION" + name + Integer.toString(i + 1));
			conn.commit();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return false;
		}

	}

	public Boolean procedureC(String myName, int counter) {
		try {
			String name = myName;
			int i = counter;
			Integer zValue, tValue, cValue, xValue;
			zValue = getValue(EXCLUSIVE_MODE, Z);
			zValue = zValue + 1;
			setValue(EXCLUSIVE_MODE, Z, zValue);
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + Z + "," + Integer.toString(zValue - 1)
					+ "," + Integer.toString(zValue) + ")");
			tValue = getValue(EXCLUSIVE_MODE, T);
			cValue = getValue(EXCLUSIVE_MODE, C);
			xValue = getValue(SHARE_MODE, X);
			tValue = tValue + xValue;
			//tValue = tValue + 1;
			cValue = cValue + xValue;
			setValue(EXCLUSIVE_MODE, T, tValue);
			setValue(EXCLUSIVE_MODE, C, cValue);
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + T + ","
					+ Integer.toString(tValue - xValue) + "," + Integer.toString(tValue) + ")");
			/*System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + T + ","
					+ Integer.toString(tValue - 1) + "," + Integer.toString(tValue) + ")");*/
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + B + ","
					+ Integer.toString(cValue - xValue) + "," + Integer.toString(cValue) + ")");
			System.out.println("END_TRANSACTION" + name + Integer.toString(i + 1));
			conn.commit();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return false;
		}
	}

	public Boolean procedureD(String myName, int counter) {
		try {
			String name = myName;
			int i = counter;
			Integer zValue, dValue, tValue, xValue;
			tValue = getValue(EXCLUSIVE_MODE, T);
			dValue = getValue(EXCLUSIVE_MODE, D);
			zValue = getValue(SHARE_MODE, Z);
			tValue = tValue + zValue;
			//tValue = tValue + 1;
			dValue = dValue + zValue;
			setValue(EXCLUSIVE_MODE, T, tValue);
			setValue(EXCLUSIVE_MODE, D, dValue);
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + T + ","
					+ Integer.toString(tValue - zValue) + "," + Integer.toString(tValue) + ")");
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + D + ","
					+ Integer.toString(dValue - zValue) + "," + Integer.toString(dValue) + ")");
			xValue = getValue(EXCLUSIVE_MODE, X);
			xValue = xValue - 1;
			setValue(EXCLUSIVE_MODE, X, xValue);
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + X + "," + Integer.toString(xValue + 1)
					+ "," + Integer.toString(xValue) + ")");
			System.out.println("END_TRANSACTION" + name + Integer.toString(i + 1));
			conn.commit();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return false;
		}

	}

	public Boolean procedureE(String myName, int counter) {
		try {
			String name = myName;
			int i = counter;
			Integer tValue, eValue, xValue, yValue;
			tValue = getValue(EXCLUSIVE_MODE, T);
			eValue = getValue(EXCLUSIVE_MODE, E);
			xValue = getValue(SHARE_MODE, X);
			tValue = tValue + xValue;
			//tValue = tValue + 1;
			eValue = eValue + xValue;
			setValue(EXCLUSIVE_MODE, T, tValue);
			setValue(EXCLUSIVE_MODE, E, eValue);
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + T + ","
					+ Integer.toString(tValue - xValue) + "," + Integer.toString(tValue) + ")");
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + E + ","
					+ Integer.toString(eValue - xValue) + "," + Integer.toString(eValue) + ")");
			yValue = getValue(EXCLUSIVE_MODE, Y);
			yValue = yValue - 1;
			setValue(EXCLUSIVE_MODE, Y, yValue);
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + Y + "," + Integer.toString(yValue + 1)
					+ "," + Integer.toString(yValue) + ")");
			System.out.println("END_TRANSACTION" + name + Integer.toString(i + 1));
			conn.commit();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return false;
		}
	}

	public Boolean procedureF(String myName, int counter) {
		try {
			String name = myName;
			int i = counter;
			Integer tValue, fValue, yValue, zValue;
			tValue = getValue(EXCLUSIVE_MODE, T);
			fValue = getValue(EXCLUSIVE_MODE, F);
			yValue = getValue(SHARE_MODE, Y);
			tValue = tValue + yValue;
			//tValue = tValue + 1;
			fValue = fValue + yValue;
			setValue(EXCLUSIVE_MODE, T, tValue);
			setValue(EXCLUSIVE_MODE, F, fValue);
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + T + ","
					+ Integer.toString(tValue - yValue) + "," + Integer.toString(tValue) + ")");
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + E + ","
					+ Integer.toString(fValue - yValue) + "," + Integer.toString(fValue) + ")");
			zValue = getValue(EXCLUSIVE_MODE, Z);
			zValue = zValue - 1;
			setValue(EXCLUSIVE_MODE, Z, zValue);
			System.out.println("WRITE( " + name + Integer.toString(i + 1) + "," + Z + "," + Integer.toString(zValue + 1)
					+ "," + Integer.toString(zValue) + ")");
			System.out.println("END_TRANSACTION" + name + Integer.toString(i + 1));
			conn.commit();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return false;
		}
	}

	public void showInitialValues() {
		try {
			System.out.println("Initial value of " + X + ": " + Integer.toString(getValue(NONLOCKING, X)));
		System.out.println("Initial value of " + Y + ": " + Integer.toString(getValue(NONLOCKING, Y)));
		System.out.println("Initial value of " + Z + ": " + Integer.toString(getValue(NONLOCKING, Z)));
		System.out.println("Initial value of " + A + ": " + Integer.toString(getValue(NONLOCKING, A)));
		System.out.println("Initial value of " + B + ": " + Integer.toString(getValue(NONLOCKING, B)));
		System.out.println("Initial value of " + C + ": " + Integer.toString(getValue(NONLOCKING, C)));
		System.out.println("Initial value of " + D + ": " + Integer.toString(getValue(NONLOCKING, D)));
		System.out.println("Initial value of " + E + ": " + Integer.toString(getValue(NONLOCKING, E)));
		System.out.println("Initial value of " + F + ": " + Integer.toString(getValue(NONLOCKING, F)));
		
			System.out.println("Initial value of " + T + ": " + Integer.toString(getValue(NONLOCKING, T)));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		
		try {
			System.out.println("Final value of " + X + ": " + Integer.toString(getValue(NONLOCKING, X)));
		System.out.println("Final value of " + Y + ": " + Integer.toString(getValue(NONLOCKING, Y)));System.out.println("Final value of " + Z + ": " + Integer.toString(getValue(NONLOCKING, Z)));
		System.out.println("Final value of " + T + ": " + Integer.toString(getValue(NONLOCKING, T)));
		System.out.println("Final value of " + A + ": " + Integer.toString(getValue(NONLOCKING, A)));
		System.out.println("Final value of " + B + ": " + Integer.toString(getValue(NONLOCKING, B)));
		System.out.println("Final value of " + C + ": " + Integer.toString(getValue(NONLOCKING, C)));
		System.out.println("Final value of " + D + ": " + Integer.toString(getValue(NONLOCKING, D)));
		System.out.println("Final value of " + E + ": " + Integer.toString(getValue(NONLOCKING, E)));
		System.out.println("Final value of " + F + ": " + Integer.toString(getValue(NONLOCKING, F)));
		System.out.println("Expected final value of " + X + ": " + Integer.toString(0));
		System.out.println("Expected final value of " + Y + ": " + Integer.toString(0));
		System.out.println("Expected final value of " + Z + ": " + Integer.toString(0));
		System.out
				.println(
						"Expected final value of " + T + ": "
								+ Integer.toString(getValue(NONLOCKING, A) + getValue(NONLOCKING, B)
										+ getValue(NONLOCKING, C) + getValue(NONLOCKING, D) + getValue(NONLOCKING, E)
										+ getValue(NONLOCKING, F)));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;

	}

}
