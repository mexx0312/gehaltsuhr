package de.rollinrob.gehaltsuhr;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

public class TimingThread extends Thread {

	private boolean inter=false;
	private Message msg;
	private Handler msgHandler;
	
	public TimingThread(Handler messageHandler) {
		super();
		msgHandler = messageHandler;
	}

	public void goOn(){
		inter = false;
	}
	
	@Override
	public void run() {

		while(true){
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!inter){
				msg = Message.obtain();
				msg.setTarget(msgHandler);
				msg.what = 1;
				msg.sendToTarget();
			}
		}
				
	}

	@Override
	public void interrupt() {
		// TODO Auto-generated method stub
		super.interrupt();
		inter = true;
	}
	
	


}
