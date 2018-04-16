package transacts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ThreadLocalRandom;

public class Data {

	Connection conn;
	Statement st;
	String sentence;

	static final int NONLOCKING = 0; //se trabaja sin reservas
	static final int LOCKING = 1; //se trabaja utilizando reservas
	
	static final int SHARE_LOCKING = LOCKING; //RLOCK
	static final int EXCLUSIVE_LOCKING = 2 * LOCKING; //WLOCK
	
	static final int NUMBER_OF_ITERATIONS = 100; //num de iteraciones por cada transaccion
	static final int NUMBER_OF_THREADS = 6; //numero de hilos maximo

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
	
	//private config temporal BD Connection
	private String serverAddress= null;
	private String port= null;
	private String bd = null;
	private String user=null;
	private String password=null;

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
			conn = DriverManager.getConnection("jdbc:mysql://"+serverAddress+":"+port+"/"+bd, user, password);
			conn.setAutoCommit(true);
			st = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void initializeSharedVariables() {
		//codigo que inicialice x,y,z,t,a,b,c,d,e,f,m a 0

	}

	private int getBarrierValue() {
		//hacer una query que devuelva M de la BD
		return 0;
	}

	private int getValue(int nonlocking2, String x2) {
		//recuperar valor contenido en variable 'x2'
		return 0;
	}
	
	private Boolean setValue(int mode, String variable, int value) {
		//update de la 'variable' con el nuevo 'value'
		return null;
		
	}

	public void synchronyze() {
		int barrierValue;

		//incrementa la m en 1
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
		//decrementa la m en 1
		decreaseBarrier();
	}

	private void decreaseBarrier() {
		//hacer una query que incremente M de la BD

	}

	private void increaseBarrier() {
		//hacer una query que incremente M en la BD

	}

	public Boolean procedureA(String myName, int counter) {
		//generar codigo procedimiento A
		int i = counter;
		Integer xValue, tValue, aValue, yValue;
		xValue = getValue(EXCLUSIVE_MODE, X);
		xValue = xValue +1;
		setValue(EXCLUSIVE_MODE, X , xValue);
		System.out.println("WRITE( " + myName + Integer.toString(i+1) + "," + X + "," + Integer.toString(xValue-1) + "," + Integer.toString(xValue) + ")") ;
		tValue =getValue(EXCLUSIVE_MODE, T);
		aValue =getValue(EXCLUSIVE_MODE, A);
		yValue =getValue(SHARE_MODE, Y);
		tValue = tValue + yValue;
		aValue=aValue + yValue;
		setValue(EXCLUSIVE_MODE, T, tValue);
		setValue(EXCLUSIVE_MODE, A, aValue);
		System.out.println("WRITE( " + myName + Integer.toString(i+1) + "," + T + "," + Integer.toString(tValue-yValue) + "," + Integer.toString(tValue) + ")") ;
		System.out.println("WRITE( " + myName + Integer.toString(i+1) + "," + X + "," + Integer.toString(aValue-yValue) + "," + Integer.toString(aValue) + ")") ;
		System.out.println("END_TRANSACTION" + myName + Integer.toString(i+1));
		return null;
	}

	public Boolean procedureB(String myName, int counter) {
		//generar codigo procedimiento B
		return null;
	}
	
	public Boolean procedureC(String myName, int counter) {
		//generar codigo procedimiento C
		return null;
	}
	
	public Boolean procedureD(String myName, int counter) {
		//generar codigo procedimiento D
		return null;
	}
	
	
	public Boolean procedureE(String myName, int counter) {
		//generar codigo procedimiento E
		return null;
	}
	
	public Boolean procedureF(String myName, int counter) {
		//generar codigo procedimiento F
		return null;
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

		while(barrierValue < 1) {
			try {
				Thread.sleep(ThreadLocalRandom.current().nextInt(1,11));
				barrierValue = getBarrierValue();			
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		while(barrierValue > 0) {
			try {
				Thread.sleep(ThreadLocalRandom.current().nextInt(1,11));
				barrierValue = getBarrierValue();
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Final value of " + X + ": " + Integer.toString(getValue(NONLOCKING,X)));
		System.out.println("Final value of " + X + ": " + Integer.toString(getValue(NONLOCKING,Y)));
		System.out.println("Final value of " + X + ": " + Integer.toString(getValue(NONLOCKING,Z)));
		System.out.println("Final value of " + X + ": " + Integer.toString(getValue(NONLOCKING,T)));
		System.out.println("Final value of " + X + ": " + Integer.toString(getValue(NONLOCKING,A)));
		System.out.println("Final value of " + X + ": " + Integer.toString(getValue(NONLOCKING,B)));
		System.out.println("Final value of " + X + ": " + Integer.toString(getValue(NONLOCKING,C)));
		System.out.println("Final value of " + X + ": " + Integer.toString(getValue(NONLOCKING,D)));
		System.out.println("Final value of " + X + ": " + Integer.toString(getValue(NONLOCKING,E)));
		System.out.println("Final value of " + X + ": " + Integer.toString(getValue(NONLOCKING,F)));

		System.out.println("Expected final value of " + X + ": " + Integer.toString(0));
		System.out.println("Expected final value of " + Y + ": " + Integer.toString(0));
		System.out.println("Expected final value of " + Z + ": " + Integer.toString(0));
		System.out.println("Expected final value of " + T + ": " + Integer.toString(getValue(NONLOCKING,A)+
				getValue(NONLOCKING,B)+getValue(NONLOCKING,C)+getValue(NONLOCKING,D)+getValue(NONLOCKING,E)+
				getValue(NONLOCKING,F)));
		
		return true;

	}

}


