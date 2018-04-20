package pruebacliente;

public class MyThreadB extends Thread {

	String myName="A";
	Data myData;
	int myMode;

	public MyThreadB() {

		myData = new Data();
		myMode =Data.NONLOCKING;
	}
	public MyThreadB(int mode){
		myData = new Data();
		myMode = mode;

	}

	public void run() {

		int counter = 0;
		Boolean commited;

		myData.showInitialValues(myName);

		while (counter < Data.NUMBER_OF_ITERATIONS) {
			commited = myData.transactionB(myName, counter, myMode);
			if (commited == true) {
				counter++;
			}
		}
		myData.showFinalValues(myName); 
		myData.logOut();

	}

}