package com.example.ussttracker;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


public class smsReceiver extends BroadcastReceiver 
{
	String filterNumber = "1000";

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
			Toast.makeText(context, "got message", Toast.LENGTH_SHORT).show();
			abortBroadcast();
		}

	}

}
