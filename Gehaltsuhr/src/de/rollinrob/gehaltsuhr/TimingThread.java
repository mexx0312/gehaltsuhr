package de.rollinrob.gehaltsuhr;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

public class TimingThread extends Thread {

	private long starttime;
	private Message msg;
	private Handler msgHandler;
	
	public TimingThread(Handler messageHandler) {
		super();
		msgHandler = messageHandler;
	}

	@Override
	public synchronized void start() {
		// TODO Auto-generated method stub
		starttime = SystemClock.elapsedRealtime();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		long diff = SystemClock.elapsedRealtime()-starttime;
		msg.obtain(msgHandler, (int)diff);
		msg.sendToTarget();
		
		try {
			sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}


}
