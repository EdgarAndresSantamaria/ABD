package transacts;

public class Execute {

	static final int MODE = Data.LOCKING;
	// static final int MODE = Data.NONLOCKING;

	public static void main(String[] args) {
		Data mainData = new Data(Data.NONLOCKING, Data.NONLOCKING);

		ThreadA threadA = new ThreadA(MODE);
		//ThreadB threadB = new ThreadB(MODE);
		//ThreadC threadC = new ThreadC(MODE);
		//ThreadD threadD = new ThreadD(MODE);
		//ThreadE threadE = new ThreadE(MODE);
		//ThreadF threadF = new ThreadF(MODE);

		mainData.initializeSharedVariables();
		
		//Ver el valor de las variables antes de la ejecucion
		mainData.showInitialValues();

		new Thread(threadA).start();
		//new Thread(threadB).start();
		//new Thread(threadC).start();
		//new Thread(threadD).start();
		//new Thread(threadE).start();
		//new Thread(threadF).start();

		//ver el valor resultante de las variables
		mainData.showFinalValues();
	}

}
