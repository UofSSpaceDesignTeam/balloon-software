package com.example.ussttracker;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

/**
 * 
 * @author Dylan
 *
 */
public class MainActivity extends Activity {
	/**
	 * Textview of the app, this is used for testing and currently prints the message from a text.
	 */
	TextView message;
	
	/**
	 * BroadcastReceiver that receives sms alerts 
	 * this is different from the smsReceiver Object 
	 */
	BroadcastReceiver thisReceiver;
	
	/**
	 * this describes what type of message the receiver should receive
	 */
	IntentFilter smsFilter;
	
	//The filter number is the first 4 digits of a Gmail sms message number 
	//Unconfirmed for other service providers from sasktel
	String filterNumber = "1000";
	
	/**
	 * This function is called by the phone when the app is first started
	 * It is where you should setup the app, like initializing textviews and buttons
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setUpTextView();
		
	}
	
	/**
	 * onResume is called automatically by the phone after onCreate.
	 * This is where you create things that need to also be deleted or start animations.
	 * You need an onPause function if you create an onResume function otherwise you will get an error
	 */
	@Override
	protected void onResume() 
	{
		super.onResume();
		//make the filter look for sms messages
		smsFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		//create a broadcastreceiver to look for sms messages
		thisReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) 
			{
				// create a Bundle to store the information from the message
				// A bundle is just a data storage object
				Bundle bundle = intent.getExtras();
				// create an SmsMessages array initialized to null
				android.telephony.SmsMessage[] messages = null;
				// if the bundle contains information
				if(bundle !=null)
				{
					// create an object pdus from searching the bundle for the key "pdus"
					Object[] pdus = (Object[]) bundle.get("pdus");
					messages = new android.telephony.SmsMessage[pdus.length];
					
					for(int i = 0;i<messages.length;i++)
					{
						// get the data out of the message and put it into your SmsMessage object
						messages[i] = android.telephony.SmsMessage.createFromPdu((byte[]) pdus[i]);	
					}
				}
				// get the phone number from the message and then take the first 4 numbers of it
				String phNum = messages[0].getOriginatingAddress();
				String first4Num = phNum.substring(0, 4);
				// test if the phone number is from a gmail account
				// this will have to be changed if Gmail numbers change according to service providers
				if(filterNumber.equals(first4Num))
				{
					// reads the messageBody of the sms and then sets the TextView to display the message
					message.setText(messages[0].getMessageBody());
				}
				
			}
		};
		//Sets the priority of the broadcast Receiver to 8.  The smsReceiver object is set to 6
		smsFilter.setPriority(8);
		//Registers the receiver to receive messages.  it must be turned off in onPause or there will be an error.
		//If it isn't registered it won't receive any messages
		registerReceiver(thisReceiver, smsFilter);
		
	}
	@Override
	protected void onPause() {
		super.onPause();
		//Unregister the messagereceiver when the app is not in use
		unregisterReceiver(thisReceiver);
	}

	/**
	 * This was created to set up a textView to clean up the onCreate function
	 */
	private void setUpTextView() 
	{
		message = (TextView) findViewById(R.id.Message);
		String str = "hello";
		String str1 = str.substring(0, 4);
		message.setText(str1 + " " + str.substring(0,3) );
	}

	/**
	 * this is called to initialize the menu options 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

