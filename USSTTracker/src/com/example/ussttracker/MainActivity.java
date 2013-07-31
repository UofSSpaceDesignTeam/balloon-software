package com.example.ussttracker;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author Dylan
 * 
 */
public class MainActivity extends Activity implements SensorEventListener {
	/**
	 * Textview of the app, this is used for testing and currently prints the
	 * message from a text.
	 */
	TextView textview;

	Boolean appActive;

	Boolean isChanged = false;

	// the image for the arrow that will spin
	ImageView arrow;

	public double LONGITUDE_TO_KMS = 111.2;

	public double LATITUDE_DEG_TO_KM = 111.132;


	@SuppressWarnings("unused")
	private GPSTracker g;

	// the latitude of the target in km
	public double targetLatitude = 52*LATITUDE_DEG_TO_KM;

	// the longitude of the target in km
	public double targetLongitude = -106*LONGITUDE_TO_KMS*Math.cos(52*(Math.PI/180))*(180/Math.PI);

	// the longitude of the phone in km
	public double phoneLatitude = 0.0;

	// the longitude of the phone in km
	public double phoneLongitude = 0.0;

	private double angleOffset = 0;
	private double distanceToTarget;

	double longDifference;
	double latDifference;
	Boolean phoneLatHigher = null;
	Boolean phoneLongHigher = null;

	SensorManager sensorManager;
	private Sensor sensorAccelerometer;
	private Sensor sensorMagneticField;

	private float[] valuesAccelerometer;
	private float[] valuesMagneticField;

	private float[] matrixR;
	private float[] matrixI;
	private float[] matrixValues;
	Compass myCompass;

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
		adjustView();
		myCompass = (Compass)findViewById(R.id.mycompass);

		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		valuesAccelerometer = new float[3];
		valuesMagneticField = new float[3];

		matrixR = new float[9];
		matrixI = new float[9];
		matrixValues = new float[3];

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
					adjustView();
					isChanged = true;
				}
			}



		};
		appView.start();
		//set up the interface
		adjustView();
		//set up the button to renew the user interface
	}


	//change this for telnet
	private void updateTarget() 
	{

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
		sensorManager.registerListener(this,
				sensorAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this,
				sensorMagneticField,
				SensorManager.SENSOR_DELAY_NORMAL);

		appActive = true;
	}

	/**
	 * Sets up the Arrow and calculates distance between you and the gpsCoords
	 */
	private void adjustView() 
	{
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
					angleOffset = 0;
					distanceToTarget = longDifference ;
				} else if (!phoneLongHigher) {
					angleOffset =180;
					distanceToTarget =longDifference;
				} else {
				}
			} else if (phoneLongHigher == null) {
				if (phoneLatHigher) {
					angleOffset =270;
					distanceToTarget =latDifference;
				} else {
					angleOffset =90;
					distanceToTarget = latDifference;
				}
			}
			distanceToTarget = Math.sqrt(latDifference*latDifference + longDifference*longDifference);
			if(phoneLatHigher && phoneLongHigher)
			{
				angleOffset =270 + Math.atan(longDifference/latDifference);
			}
			else if(phoneLatHigher && !phoneLongHigher)
			{
				angleOffset =270 - Math.atan(longDifference/latDifference);	
			}
			else if(!phoneLatHigher && phoneLongHigher)
			{
				angleOffset =90 - Math.atan(longDifference/latDifference);
			}
			else
			{
				angleOffset =90 + Math.atan(longDifference/latDifference);
			}
		}
	}

	/**
	 * when the app is first open set the message to "0"
	 */
	private void setZero() {

		textview.setText("0 Kms");
	}

	@Override
	protected void onPause() {
		super.onPause();
		sensorManager.unregisterListener(this,
				sensorAccelerometer);
		sensorManager.unregisterListener(this,
				sensorMagneticField);
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

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch(event.sensor.getType()){
		case Sensor.TYPE_ACCELEROMETER:
			for(int i =0; i < 3; i++){
				valuesAccelerometer[i] = event.values[i];
			}
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			for(int i =0; i < 3; i++){
				valuesMagneticField[i] = event.values[i];
			}
			break;
		}

		boolean success = SensorManager.getRotationMatrix(
				matrixR,
				matrixI,
				valuesAccelerometer,
				valuesMagneticField);

		if(success){
			SensorManager.getOrientation(matrixR, matrixValues);

			textview.setText(distanceToTarget + " KMs");
			myCompass.update((float) (matrixValues[0] + angleOffset));
		}

	}

}
