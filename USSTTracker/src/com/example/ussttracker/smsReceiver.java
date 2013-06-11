package com.example.ussttracker;
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
	//The filter number is the first 4 digits of a Gmail sms message number 
	//Unconfirmed for other service providers from sasktel
	String filterNumber = "1000";

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
		// get the phone number from the message and then take the first 4 numbers of it
		String phNum = messages[0].getOriginatingAddress();
		String first4Num = phNum.substring(0, 4);
		// test if the phone number is from a gmail account
		// this will have to be changed if Gmail numbers change according to service providers
		if(filterNumber.equals(first4Num))
		{
			// create a message on the phone saying you got a message
			Toast.makeText(context, "got message", Toast.LENGTH_SHORT).show();
			// delete the message so it doesn't alert any receivers with a lower priority
			abortBroadcast();
		}

	}

}
