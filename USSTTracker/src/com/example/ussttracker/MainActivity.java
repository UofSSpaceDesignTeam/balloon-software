package com.example.ussttracker;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {
	TextView message;
	BroadcastReceiver thisReceiver;
	IntentFilter smsFilter;
	String filterNumber = "1000";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setUpTextView();
		
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		smsFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		
		thisReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) 
			{
				Bundle bundle = intent.getExtras();
				android.telephony.SmsMessage[] messages = null;
				if(bundle !=null)
				{
					Object[] pdus = (Object[]) bundle.get("pdus");
					messages = new android.telephony.SmsMessage[pdus.length];
					
					for(int i = 0;i<messages.length;i++)
					{
						messages[i] = android.telephony.SmsMessage.createFromPdu((byte[]) pdus[i]);	
					}
				}
				String phNum = messages[0].getOriginatingAddress();
				String first4Num = phNum.substring(0, 4);
				if(filterNumber.equals(first4Num))
				{
					message.setText(messages[0].getMessageBody());
				}
				
			}
		};
		smsFilter.setPriority(8);
		registerReceiver(thisReceiver, smsFilter);
		
	}
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(thisReceiver);
	}

	private void setUpTextView() 
	{
		message = (TextView) findViewById(R.id.Message);
		String str = "hello";
		String str1 = str.substring(0, 4);
		message.setText(str1 + " " + str.substring(0,3) );
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

