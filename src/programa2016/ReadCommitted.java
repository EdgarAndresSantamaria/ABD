
public class ReadCommitted {

	public static void main(String[] args) {

		Data data = new Data();
		data.initializeSharedVariables();

		MyThreadA threadA = new MyThreadA();
		MyThreadB threadB = new MyThreadB();
		MyThreadC threadC = new MyThreadC();

		threadA.start();
		threadB.start();
		threadC.start();
	}

}
