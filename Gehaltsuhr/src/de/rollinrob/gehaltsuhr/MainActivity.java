package de.rollinrob.gehaltsuhr;

import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	private TextView textviewCurrentPay;
	private TimingThread timing = null;
	private boolean runningState = false;
	EditText hourlyRateEditText;
	Editable hourlyRateText;
	char[] chars = new char[10];
	private int currentPay = 0;
	private int hourlyRate = 0; //in Cents
	private long starttime;
	Toast test;
	Handler messageHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(runningState){
				currentPay += hourlyRate/60;
				int curpeuro = currentPay/100;
				int curpcent = currentPay-curpeuro*100;
				char nn;
				if(curpcent<10){
					nn = '0';
				} else {
					nn = '\0';
				}
				setCurrentPay(""+curpeuro+","+nn+curpcent+"€");
			}
			//test = Toast.makeText(getApplicationContext(),"Message handled!", Toast.LENGTH_SHORT);
			//test.show();
			//super.handleMessage(msg);
		}
		
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textviewCurrentPay = (TextView)findViewById(R.id.textViewCurrentPay);
		hourlyRateEditText = (EditText)findViewById(R.id.editTextHourlyRate);
		
		//Initialise the notification interval spinner
		Spinner spinner = (Spinner) findViewById(R.id.spinnerNotification);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.intervals_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		
		final Button buttonStartStop = (Button)findViewById(R.id.buttonStartStop);
		buttonStartStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(timing==null && !runningState){
            		starttime = SystemClock.elapsedRealtime();
            		hourlyRateText = hourlyRateEditText.getText();
            		hourlyRateText.getChars(0, hourlyRateText.length(), chars, 0);
            		for(int j=0; j<hourlyRateText.length();j++){
            			if(chars[j]=='.'){
            				for(int k=j-1; k>=0;k--){
            					hourlyRate += Integer.parseInt(""+chars[k])*Math.pow(10,1+j-k); //times 100 because it's Cents
            				}
            				hourlyRate += Integer.parseInt(""+chars[j+1])*10;
            				hourlyRate += Integer.parseInt(""+chars[j+2]);
            				break;
            			}
            			if(j==hourlyRateText.length()-1 && chars[j]!='.'){
            				for(int k=j; k>=0;k--){
            					hourlyRate += Integer.parseInt(""+chars[k])*Math.pow(10,2+j-k); //times 100 because it's Cents
            				}
            				break;
            			}
            		}
            		timing = new TimingThread(messageHandler);
            		try{
            			timing.start();
            			runningState = true;
            			buttonStartStop.setText("Stop");
            		} catch (IllegalThreadStateException e){
            			e.printStackTrace();
            		}
            	} else if(runningState){
            		timing.interrupt();
            		runningState = false;
        			buttonStartStop.setText("Start");
            	} else if(!runningState){
            		//test = Toast.makeText(getApplicationContext(),timing.getState().toString(), Toast.LENGTH_SHORT);
        			//test.show();
            		starttime = SystemClock.elapsedRealtime();
        			if(timing.getState() == Thread.State.NEW){
        				hourlyRateText = hourlyRateEditText.getText();
                		hourlyRateText.getChars(0, hourlyRateText.length(), chars, 0);
                		for(int j=0; j<hourlyRateText.length();j++){
                			if(chars[j]=='.'){
                				for(int k=j-1; k>=0;k--){
                					hourlyRate += Integer.parseInt(""+chars[k])*Math.pow(10,1+j-k); //times 100 because it's Cents
                				}
                				hourlyRate += Integer.parseInt(""+chars[j+1])*10;
                				hourlyRate += Integer.parseInt(""+chars[j+2]);
                				break;
                			}
                			if(j==hourlyRateText.length()-1 && chars[j]!='.'){
                				for(int k=j; k>=0;k--){
                					hourlyRate += Integer.parseInt(""+chars[k])*Math.pow(10,2+j-k); //times 100 because it's Cents
                				}
                				break;
                			}
                		}
        				timing.start();
        			} else {
        				timing.goOn();
        			}
            		runningState = true;
        			buttonStartStop.setText("Stop");
            	}
            	
               
            }
        });

		final Button buttonReset = (Button)findViewById(R.id.buttonReset);
		buttonReset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(timing==null && !runningState){
            		timing = new TimingThread(messageHandler);
            	} else if(runningState){
            		timing.interrupt();
            		runningState = false;
        			buttonStartStop.setText("Start");
        			timing = new TimingThread(messageHandler);
            	} else if(!runningState){
            		timing = new TimingThread(messageHandler);
            	}
    			currentPay = 0;
    			setCurrentPay("0,00€");
            	
               
            }
        });
		
			
	}
	
	public void setCurrentPay(String input){
		textviewCurrentPay = (TextView)findViewById(R.id.textViewCurrentPay);
		textviewCurrentPay.setText(input);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		// TODO Save current pay, which is (elapsedRealtime()-starttime)ms*hourlyRate/(1000(ms)*60s*60min)
		// TODO Save settings (hourly rate, notifications, running state) 
		try{
			timing.interrupt();
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}

	@Override
	protected void onRestart() {
		// TODO Recover current pay, hourly rate, notifications setting, running state info
		super.onRestart();
		test = Toast.makeText(getApplicationContext(),"Gehaltsuhr: onRestart()", Toast.LENGTH_SHORT);
		test.show();
		
	}

	
}
