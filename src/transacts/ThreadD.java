package transacts;

public class ThreadD extends Thread {

	String myName = "D";
	Data myData;

	public ThreadD(int mode) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {

		int counter = 0;
		Boolean committed;

		myData.synchronyze();

		System.out.println("Go " + myName + "!!!");

		while (counter < Data.NUMBER_OF_ITERATIONS) {
			committed = myData.procedureA(myName, counter);
			if (committed == true)
				counter = counter + 1;
		}

		myData.finish();
	}

}