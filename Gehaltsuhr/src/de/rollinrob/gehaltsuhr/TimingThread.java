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

	@Override
	public void run() {
		// TODO Auto-generated method stub
		int i=0;
		while(true){
			msg = Message.obtain();
			msg.setTarget(msgHandler);
			msg.what = 1;
			msg.sendToTarget();
			i++;
			try {
				sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(inter){
				break;
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
