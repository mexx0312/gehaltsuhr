package de.rollinrob.gehaltsuhr;

import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
	private int hourlyRate = 1000; //in Cents
	Toast test;
	Handler messageHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(runningState){
				currentPay += hourlyRate/60;
				setCurrentPay(""+currentPay+"€");
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
            		hourlyRateEditText = (EditText)findViewById(R.id.editTextHourlyRate);
            		hourlyRateText = hourlyRateEditText.getText();
            		//test = Toast.makeText(getApplicationContext(),hourlyRateText.toString(), Toast.LENGTH_SHORT);
        			//test.show();
            		hourlyRateText.getChars(0, hourlyRateText.length(), chars, 0);
            		test = Toast.makeText(getApplicationContext(),""+chars[0], Toast.LENGTH_SHORT);
        			test.show();
        			/*test = Toast.makeText(getApplicationContext(),chars[1], Toast.LENGTH_SHORT);
        			test.show();
            		test = Toast.makeText(getApplicationContext(),chars[2], Toast.LENGTH_SHORT);
        			test.show();*/
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
        			if(timing.getState() == Thread.State.NEW){
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
	protected void onPause() {
		// Then app loses focus
		//super.onPause();
		//test = Toast.makeText(getApplicationContext(),"onPause!", Toast.LENGTH_SHORT);
		//test.show();
		//TODO Save current time and reload it in onRestart()-method !!!!!
			try{
				timing.interrupt();
			} catch(Exception e){
    			e.printStackTrace();
			}
			super.onPause();
	}

	
}
