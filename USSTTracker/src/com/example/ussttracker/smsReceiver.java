package com.example.ussttracker;
import java.util.Scanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * The reason for this class is because it runs in the background.
 * So if you don't have the app running but you are being sent messages
 * from our Gmail you don't receive all of those messages on your phone.
 * 
 * The Priority of this BroadcastReceiver is set in the manifest and it is currently 6 so it goes after the MainActivity
 * @author Dylan
 *
 */
public class smsReceiver extends BroadcastReceiver 
{
	//The first part of the incoming message must be this for the receiver to read it
	private String filterText = "usstGPS ";
	
	public Double latitude = 0.0;
	
	public Double longitude = 0.0;
	
	public double LONGITUDE_TO_KMS = 111.2;

	public double LATITUDE_DEG_TO_KM = 111.132;
	
	public smsReceiver(){
		
	}
	
	/**
	 * takes the message and parses the string for the gps latitude and longitude
	 * @param coords has 2 floats separated by white space.  it can handle a blank string
	 */
	private void parseGPS(String coords)
	{
		if(coords.equals(""))
		{
		}
		Scanner sc = new Scanner(coords);
		if(sc.hasNextFloat())
		{
			double nlatitude = sc.nextFloat();
			if(sc.hasNextFloat())
			{
				double nlongitude = sc.nextFloat();
				//Make the conversion between degrees and kms
				latitude = LATITUDE_DEG_TO_KM*nlatitude;
				longitude = LONGITUDE_TO_KMS*Math.cos(nlatitude*(Math.PI/180))*(180/Math.PI)*nlongitude;
			}
		}
		
		
	}

	/**
	 * On receive is called when the Broadcast Receiver gets an sms message 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
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
		if(message.length()<8)
		{
			String firstWord = message.substring(0, 8);
			if(filterText.equals(firstWord))
			{
				//set the received message to the gps coords without the filter
				parseGPS(message.substring(8));
				// create a message on the phone saying you got a message
				Toast.makeText(context, "got message", Toast.LENGTH_SHORT).show();
				// delete the message so it doesn't alert any receivers with a lower priority
				abortBroadcast();
			}
		}
		else
		{
			
		}


	}

}
