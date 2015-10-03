
// grab all the libraries for sensors
#include <AltSoftSerial.h>	// for gps
#include <TinyGPS.h>	// for gps
#include <MPU6050.h>	// for gyro
#include <I2Cdev.h>	// for gyro
#include <Wire.h>	// for gyro and pressure senors
#include <Adafruit_BMP085.h>	// for pressure sensor
#include <SoftwareSerial.h>
#include <HTU21D.h> // humidity

// create all the objects we will need
SoftwareSerial ssLogger(10,9);	// 9: datalogger out, 10: unused
SoftwareSerial ssGPS(8,11);	 // 8: gps in, 11: unused
MPU6050 gyro;
TinyGPS gps;
Adafruit_BMP085 bmp;	// pressure sensor
HTU21D myHumidity;

// create global variables for use later
long lat, lon, alt;	// gps position
unsigned long fixAge, speed, course, lastLog, lastTransmit, lastPicture, date, time;
unsigned long gpsAlt, ExternalTemp, InternalTemp, humd;	// gps and timing data
int ax, ay, az, gx, gy, gz, mx, my, mz;	// gyro data

unsigned long chars;
unsigned short sentences, failed;

void setup()	// runs once at power up
{
	pinMode(4, OUTPUT); //set up keycamera
	delay(100);	// wait for devices to power up
	digitalWrite(4, 0); //begin camera
	delay(100);
	digitalWrite(4, 1);
	delay(2000);
	digitalWrite(4, 0);
	Wire.begin();	// fire up the I2C interface
	Serial.begin(4800);	// main serial port for debug/radio interface
	ssGPS.begin(9600);	// serial interface for the gps
	ssLogger.begin(4800);	//serial interface for the DataLogger
	gyro.initialize();	// set up IMU
	if(!gyro.testConnection())
		ssLogger.println("Gyro fail!");
	if(!bmp.begin())
		ssLogger.println("BMP fail!");
	ssLogger.println("timestamp(millis),timestamp(gps),date,lat,lon,gpsAlt,bmpAlt,fixage,speed,course,ax,ay,az,gx,gy,gz,mx,my,mz,humd,ExternalTemp,InternalTemp,SolarCell Raw,UV Sensor Raw");
	lastLog = 0;
	lastTransmit = 0;
	lastPicture = 0;
	fixAge = 0;
	speed = 0;
	course = 0;
	lat = 0;
	lon = 0; 
	gpsAlt = 0;
	time = 0;
        ssGPS.listen();
}

void loop()
{
	while(ssGPS.available())	// update the gps
	{
		if(gps.encode(ssGPS.read()));
		{
			gps.get_position(&lat,&lon,&fixAge);
			gps.get_datetime(&date,&time);
			speed = gps.speed();
			course = gps.course();
			gpsAlt = gps.altitude();
		}
	}
	
	if(millis() - lastLog > 10000)	// log data every 10 sec
	{
		lastLog = millis();
		logData();
                ssGPS.listen();
	}
	
	if(millis() - lastTransmit > 5000) // transmit data every 1 sec
	{
		lastTransmit = millis();
		transmitData();
                ssGPS.listen();
	}
}
