
public class MyThreadC extends Thread {

	String myName="A";
	Data myData;
	int myMode;

	public MyThreadC() {

		myData = new Data();
		myMode =Data.NONLOCKING;
	}
	public MyThreadC(int mode){
		myData = new Data();
		myMode = mode;

	}

	public void run() {

		int counter = 0;
		Boolean commited;

		myData.showInitialValues(myName);

		while (counter < Data.NUMBER_OF_ITERATIONS) {
			commited = myData.transactionC(myName, counter, myMode);
			if (commited == true) {
				counter++;
			}
		}
		myData.showFinalValues(myName); 
		myData.logOut();

	}

}