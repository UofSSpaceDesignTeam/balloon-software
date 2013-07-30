package com.example.ussttracker;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
	TextView datatext;

	Boolean appActive;
	
	Boolean isChanged = false;

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
	private GPSTracker g;

	// the latitude of the target in km
	public double targetLatitude = 52;

	// the longitude of the target in km
	public double targetLongitude = -106;

	// the longitude of the phone in km
	public double phoneLatitude = 0.0;

	// the longitude of the phone in km
	public double phoneLongitude = 0.0;

	private double North = 0;

	double longDifference;
	double latDifference;
	Boolean phoneLatHigher = null;
	Boolean phoneLongHigher = null;

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
		g = new GPSTracker(this);
		arrow = (ImageView) findViewById(R.id.Arrow);
		setArrowAngle(North);
		adjustView();
		// First, get an instance of the SensorManager
		sMan = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		// Second, get the sensor you're interested in
		magnetField = sMan.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		// Third, implement a SensorEventListener class
		magnetListener = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				float[] mGravity = null;
				float[] mGeomagnetic = null;
				if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
					mGravity = event.values;
				if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
					mGeomagnetic = event.values;
				if (mGravity != null && mGeomagnetic != null) {
					Log.d("verbose" ,mGravity + "" + mGeomagnetic);
					float R[] = new float[9];
					float I[] = new float[9];
					boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
					if (success) {
						float orientation[] = new float[3];
						SensorManager.getOrientation(R, orientation);
						double Azimut = orientation[0]; // orientation contains: azimut, pitch and roll
						North = Math.round(Azimut);
						
					}
				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// do nothing

			}
		};

		// Finally, register your listener
		sMan.registerListener(magnetListener, magnetField,
				SensorManager.SENSOR_DELAY_NORMAL);

		Thread appView = new Thread()
		{
			@Override
			public void run()
			{
				while(appActive)
				{
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					updateTarget();
					isChanged = true;
				}
			}



		};
		appView.start();
		//set up the interface
		adjustView();
		//set up the button to renew the user interface
		setUpUpdateButton();
	}

	private void setUpUpdateButton() 
	{
		Button updateButton = (Button) findViewById(R.id.updateButton);
		updateButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				adjustView();
			}
		});
	}

	//change this for telnet
	private void updateTarget() 
	{
		
	}

	/**
	 * set up the imageview for the arrow and start the arrow facing North
	 * @param degree
	 *            must be between 0 and 360
	 */
	private void setArrowAngle(double degree) {
		if (degree > 0 && degree < 360) {
			double actualDegree = degree - 90 + North;
			arrow.setRotation((float) actualDegree);
//			Matrix matrix=new Matrix();
//			arrow.setScaleType(ScaleType.MATRIX); //required
//			arrow.setImageMatrix(matrix);
//			matrix.postRotate( (float) actualDegree,arrow.getWidth()/2, arrow.getHeight()/2);
		}

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
	}

	/**
	 * Sets up the Arrow and calculates distance between you and the gpsCoords
	 */
	private void adjustView() 
	{
		datatext.setText("Phone Latitude: " + this.phoneLatitude + " Phone Longitude: " + this.phoneLongitude 
				+ " Target Latitude: " + this.targetLatitude + " Target Longitude: " + this.targetLongitude);
		if(this.phoneLatitude == 0.0 || this.phoneLongitude == 0.0 || this.targetLatitude == 0.0 || this.targetLongitude == 0.0)
		{
			return;
		}
		else
		{
			if (this.phoneLatitude < this.targetLatitude) {
				phoneLatHigher = false;
				latDifference = this.targetLatitude - this.phoneLatitude;
			} else if (this.phoneLatitude > this.targetLatitude) {
				phoneLatHigher = true;
				latDifference = this.phoneLatitude - this.targetLatitude;
			} else {
				latDifference = 0;
				phoneLatHigher = null;
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
		}
		this.textview.setText("North: " + North);


	}

	/**
	 * when the app is first open set the message to "0"
	 */
	private void setZero() {

		textview.setText("This is working");
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
		datatext = (TextView) findViewById(R.id.data);
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
