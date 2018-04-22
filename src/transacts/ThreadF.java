package transacts;

import java.sql.SQLException;

public class ThreadF extends Thread {

	String myName = "F";
	Data myData;

	public ThreadF(int mode) {

		if(mode==Data.LOCKING) {
			myData=new Data(Data.SHARE_LOCKING,Data.EXCLUSIVE_LOCKING);
		}else {
			myData=new Data(0,0);
		}
	}

	@Override
	public void run() {

		int counter = 0;
		Boolean committed;

		myData.synchronyze();

		System.out.println("Go " + myName + "!!!");

		while (counter < Data.NUMBER_OF_ITERATIONS) {
				committed = myData.procedureF(myName, counter);
			if (committed == true)
				counter = counter + 1;
		}

		myData.finish();
	}

}