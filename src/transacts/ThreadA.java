package transacts;

public class ThreadA extends Thread {

	String myName = "A";
	Data myData;

	public ThreadA(int mode) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {

		int counter = 0;
		Boolean committed;

		//Se sincroniza el inicio de la transaccion
		myData.synchronyze();

		System.out.println("Go " + myName + "!!!");

		//La transaccion se ejecuta 100 veces
		while (counter < Data.NUMBER_OF_ITERATIONS) {
			committed = myData.procedureA(myName, counter);
			if (committed == true)
				counter = counter + 1;
		}

		//Se sincroniza el final de la transaccion
		myData.finish();
	}

}