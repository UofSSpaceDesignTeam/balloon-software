package com.example.ussttracker;

import java.util.Scanner;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
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
	TextView textview;
	
	// the image for the arrow that will spin
	ImageView arrow;
	
	/**
	 * BroadcastReceiver that receives sms alerts 
	 * this is different from the smsReceiver Object 
	 */
	private BroadcastReceiver thisReceiver;
	
	/**
	 * this describes what type of message the receiver should receive
	 */
	private IntentFilter smsFilter;
	
	//The first part of the incoming message must be this for the receiver to read it
	private final String filterText = "usstRoverGPSCoords ";
	
	//The gps coordinates sent to this phone via sms
	private String gpsCoords;
	
	//The sms receiver that runs in the background and stores gps coords when this app is inactive
	private smsReceiver s;
	
	/**
	 * This function is called by the phone when the app is first started
	 * It is where you should setup the app, like initializing textviews and buttons
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setUpTextView();
		setUpArrow();
		s = new smsReceiver();
		//make the filter look for sms messages
		smsFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		
	}
	
	/**
	 * set up the imageview for the arrow and start the arrow facing North
	 */
	private void setUpArrow() 
	{
		arrow = (ImageView) findViewById(R.id.Arrow);
		arrow.setRotation(270);
//		Matrix matrix=new Matrix();
//		arrow.setScaleType(ScaleType.MATRIX);   //required
//		matrix.postRotate((float) 270, arrow.getX(), arrow.getY());
//		arrow.setImageMatrix(matrix);
	}

	/**
	 * onResume is called automatically by the phone when the app is active
	 * This is where you create things that need to also be deleted when the app isn't shown or start animations.
	 * You need an onPause function if you create an onResume function otherwise you will get an error
	 */
	@Override
	protected void onResume() 
	{
		super.onResume();
		
		//get the most recent coords from the sms receiver and then parse the string
		gpsCoords = s.receivedGPSCoords;
		parseGPS(gpsCoords);
		
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
				//Test if the message received has the filter in it
				String message = messages[0].getMessageBody();
				String firstWord = message.substring(0, 19);
				if(filterText.equals(firstWord))
				{
					// reads just the gpsCoords from the message and ignores the filter
					gpsCoords = message.substring(19);
					parseGPS(gpsCoords);
				}
				
			}
		};
		//Sets the priority of the broadcast Receiver to 8.  The smsReceiver object is set to 6
		smsFilter.setPriority(8);
		//Registers the receiver to receive messages.  it must be turned off in onPause or there will be an error.
		//If it isn't registered it won't receive any messages
		registerReceiver(thisReceiver, smsFilter);
		
	}
	
	/**
	 * takes the message and parses the string for the gps latitude and longitude
	 * @param coords has 2 floats separated by white space.  it can handle a blank string
	 */
	private void parseGPS(String coords)
	{
		textview.setText(coords);
		if(coords.equals(""))
		{
			setZero();
		}
		Scanner sc = new Scanner(coords);
		if(sc.hasNextFloat())
		{
			float longitude = sc.nextFloat();
			if(sc.hasNextFloat())
			{
				float latitude = sc.nextFloat();
				adjustView(longitude, latitude);
			}
		}
		
	}

	/**
	 * Sets up the Arrow and calculates distance between you and the gpsCoords
	 * @param longtitude
	 * @param latitude
	 */
	private void adjustView(float longtitude, float latitude) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * when the app is first open set the message to "0"
	 */
	private void setZero() 
	{
		
		textview.setText("0");		
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
		textview = (TextView) findViewById(R.id.Message);
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

