package main;

public class ThreadC extends Thread {

	String myName = "C";
	Data myData;

	public ThreadC(int mode) {
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