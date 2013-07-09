package com.example.ussttracker;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.hardware.SensorManager;

/**
 * 
 * @author Dylan
 * 
 */
public class MainActivity extends Activity {
	/**
	 * Textview of the app, this is used for testing and currently prints the
	 * message from a text.
	 */
	TextView textview;
	
	Boolean appActive;

	// the image for the arrow that will spin
	ImageView arrow;

	// /**
	// * BroadcastReceiver that receives sms alerts
	// * this is different from the smsReceiver Object
	// */
	// private BroadcastReceiver thisReceiver;

	// /**
	// * this describes what type of message the receiver should receive
	// */
	// private IntentFilter smsFilter;

	// //The first part of the incoming message must be this for the receiver to
	// read it
	// private final String filterText = "usstgpscoords ";

	public double LONGITUDE_TO_KMS = 111.2;

	public double LATITUDE_DEG_TO_KM = 111.132;

	@SuppressWarnings("unused")
	private smsReceiver s;

	@SuppressWarnings("unused")
	private GPSTracker g;

	// the latitude of the target in km
	public double targetLatitude;

	// the longitude of the target in km
	public double targetLongitude;

	// the longitude of the phone in km
	public double phoneLatitude;

	// the longitude of the phone in km
	public double phoneLongitude;

	private double North = 0;

	SensorManager sMan;
	Sensor magnetField;
	SensorEventListener magnetListener;

	/**
	 * This function is called by the phone when the app is first started It is
	 * where you should setup the app, like initializing textviews and buttons
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setUpTextView();
		setZero();
		appActive = true;
		s = new smsReceiver(this);
		g = new GPSTracker(this);
		arrow = (ImageView) findViewById(R.id.Arrow);
		setArrowAngle(North);
		// First, get an instance of the SensorManager
		sMan = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		// Second, get the sensor you're interested in
		magnetField = sMan.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		// Third, implement a SensorEventListener class
		magnetListener = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				
				North = Math.round(event.values[0]);
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// do nothing

			}
		};

		// Finally, register your listener
		sMan.registerListener(magnetListener, magnetField,
				SensorManager.SENSOR_DELAY_NORMAL);
		
		while(appActive)
		{
			adjustView();
			try {
				wait(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// make the filter look for sms messages
		// smsFilter = new
		// IntentFilter("android.provider.Telephony.SMS_RECEIVED");

	}

	/**
	 * set up the imageview for the arrow and start the arrow facing North
	 * 
	 * @param degree
	 *            must be between 0 and 360
	 */
	private void setArrowAngle(double degree) {
		if (degree > 0 && degree < 360) {
			double actualDegree = degree + 270 + North;
			arrow.setRotation((float) actualDegree);
			// TODO update compass

		}
		// Matrix matrix=new Matrix();
		// arrow.setScaleType(ScaleType.MATRIX); //required
		// matrix.postRotate((float) 270, arrow.getX(), arrow.getY());
		// arrow.setImageMatrix(matrix);
	}

	/**
	 * onResume is called automatically by the phone when the app is active This
	 * is where you create things that need to also be deleted when the app
	 * isn't shown or start animations. You need an onPause function if you
	 * create an onResume function otherwise you will get an error
	 */
	@Override
	protected void onResume() {
		super.onResume();
		sMan.registerListener(magnetListener, magnetField,
				SensorManager.SENSOR_DELAY_NORMAL);
		
		appActive = true;
		// create a broadcastreceiver to look for sms messages
		// thisReceiver = new BroadcastReceiver() {
		//
		// @Override
		// public void onReceive(Context context, Intent intent)
		// {
		// // create a Bundle to store the information from the message
		// // A bundle is just a data storage object
		// Bundle bundle = intent.getExtras();
		// // create an SmsMessages array initialized to null
		// android.telephony.SmsMessage[] messages = null;
		// // if the bundle contains information
		// if(bundle !=null)
		// {
		// // create an object pdus from searching the bundle for the key "pdus"
		// Object[] pdus = (Object[]) bundle.get("pdus");
		// messages = new android.telephony.SmsMessage[pdus.length];
		//
		// for(int i = 0;i<messages.length;i++)
		// {
		// // get the data out of the message and put it into your SmsMessage
		// object
		// messages[i] = android.telephony.SmsMessage.createFromPdu((byte[])
		// pdus[i]);
		// }
		// }
		// //Test if the message received has the filter in it
		// String message = messages[0].getMessageBody();
		// textview.setText(message.substring(13));
		// String firstWord = message.substring(0, 14);
		// if(filterText.equals(firstWord))
		// {
		// // reads just the gpsCoords from the message and ignores the filter
		// gpsCoords = message.substring(13);
		// textview.setText(gpsCoords);
		// abortBroadcast();
		// }
		//
		// }
		// };
		// //Sets the priority of the broadcast Receiver to 8. The smsReceiver
		// object is set to 6
		// smsFilter.setPriority(8);
		// //Registers the receiver to receive messages. it must be turned off
		// in onPause or there will be an error.
		// //If it isn't registered it won't receive any messages
		// registerReceiver(thisReceiver, smsFilter);
		//
	}

	/**
	 * Sets up the Arrow and calculates distance between you and the gpsCoords
	 */
	private void adjustView() {
		double longDifference;
		double latDifference;
		Boolean phoneLatHigher = null;
		Boolean phoneLongHigher = null;

		if (this.phoneLatitude < this.targetLatitude) {
			phoneLatHigher = false;
			latDifference = this.targetLatitude - this.phoneLatitude;
		} else if (this.phoneLatitude > this.targetLatitude) {
			phoneLatHigher = true;
			latDifference = this.phoneLatitude - this.targetLatitude;
		} else {
			latDifference = 0;
		}

		if (this.phoneLongitude < this.targetLongitude) {
			phoneLongHigher = true;
			longDifference = this.targetLongitude - this.phoneLongitude;
		} else if (this.targetLongitude < this.phoneLongitude) {
			phoneLongHigher = false;
			longDifference = this.phoneLongitude - this.targetLongitude;
		} else {
			longDifference = 0;
		}
		if (phoneLatHigher == null) {
			if (phoneLongHigher) {
				setArrowAngle(0);
				this.textview.setText(longDifference + " Km");
			} else if (!phoneLongHigher) {
				setArrowAngle(180);
				this.textview.setText(longDifference + " Km");
			} else {
				this.textview.setText("You are there... Or something went badly wrong and you should quit." +
								"  This is probably an error because the likelihood of getting both gps " +
								"and phone coords to be the exact same is really small");
			}
		} else if (phoneLongHigher == null) {
			if (phoneLatHigher) {
				setArrowAngle(270);
				this.textview.setText(latDifference + " Km");
			} else {
				setArrowAngle(90);
				this.textview.setText(latDifference + " Km");
			}
		}
		double distanceToTarget = Math.sqrt(latDifference*latDifference + longDifference*longDifference);
		this.textview.setText(distanceToTarget + " Km");
		if(phoneLatHigher && phoneLongHigher)
		{
			setArrowAngle(270 + Math.atan(longDifference/latDifference));
		}
		else if(phoneLatHigher && !phoneLongHigher)
		{
			setArrowAngle(270 - Math.atan(longDifference/latDifference));	
		}
		else if(!phoneLatHigher && phoneLongHigher)
		{
			setArrowAngle(90 - Math.atan(longDifference/latDifference));
		}
		else
		{
			setArrowAngle(90 + Math.atan(longDifference/latDifference));
		}
		
		
		// TODO Auto-generated method stub

	}

	/**
	 * when the app is first open set the message to "0"
	 */
	private void setZero() {

		textview.setText("0");
	}

	@Override
	protected void onPause() {
		super.onPause();
		sMan.unregisterListener(magnetListener, magnetField);
		appActive = false;
		// Unregister the messagereceiver when the app is destroyed
		// unregisterReceiver(thisReceiver);
	}

	/**
	 * This was created to set up a textView to clean up the onCreate function
	 */
	private void setUpTextView() {
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
