
// grab all the libraries for sensors
#include <AltSoftSerial.h>	// for gps
#include <TinyGPS.h>	// for gps
#include <I2Cdev.h>	
#include <Wire.h>	
#include <SoftwareSerial.h>
#include <Adafruit_MCP9808.h> //temp sensor
#include <Adafruit_MPL3115A2.h>  //pressure sensor 
#include <Adafruit_SI1145.h>   //uv sensor
#include <Adafruit_BMP085.h>	// sparkfun pressure sensor
#include <MPU6050.h>	// for gyro
#include <LSM303.h>  // for compass
#include <SparkFunHTU21D.h> // humidity 


// create all the objects we will need
// these may need to be changed 
SoftwareSerial ssLogger(10,9);	// 9: datalogger out, 10: unused
SoftwareSerial ssGPS(8,11);	 // 8: gps in, 11: unused
//SoftwareSerial ssGiger();
//SoftwareSerial ssTransmit();

TinyGPS gps;
MPU6050 gyro;
Adafruit_MCP9808  temp;
Adafruit_MPL3115A2 baro; // pressure sensor
Adafruit_SI1145 light;  
Adafruit_BMP085 bmp;	// 2nd pressure sensor
LSM303 compass;
HTU21D humidity; 

// create global variables for use later
long lat, lon, alt;	// gps position
unsigned long fixAge, speed, course, lastLog, lastTransmit, lastPicture, date, time;
unsigned long gpsAlt;	// gps and timing data
int ax, ay, az, gx, gy, gz, mx, my, mz;	// gyro data
int ExternalTemp, InternalTemp, humd;
int internalPressure, externalPressuer;
int gigercount; // giger counter 
float countsPerMinute; 

// add more variables as needed

unsigned long chars;
unsigned short sentences, failed;

void setup()	// runs once at power up
{
	//pinMode(4, OUTPUT); //set up keycamera
	delay(100);	// wait for devices to power up
	//digitalWrite(4, 0); //begin camera
	delay(100);
	//digitalWrite(4, 1);
	delay(2000);
	//digitalWrite(4, 0);
	Wire.begin();	// fire up the I2C interface
	Serial.begin(4800);	// main serial port for debug/radio interface
	ssGPS.begin(9600);	// serial interface for the gps
	ssLogger.begin(4800);	//serial interface for the DataLogger
        //ssGiger.begin(9600);  //serial interface for giger counter 
        
        // check if sensors start
	gyro.initialize();	// set up IMU
	if(!gyro.testConnection())
		ssLogger.println("Gyro fail!");
	if(!bmp.begin())
		ssLogger.println("BMP fail!");
        if(!baro.begin())
                ssLogger.println("Barometer fail!");
        if(!light.begin())
                ssLogger.println("light fail!");
        if(!temp.begin())
                ssLogger.println("temp fail!");
        //start sensors
        humidity.begin(); 
  
	ssLogger.println("timestamp(millis),timestamp(gps),date,lat,lon,gpsAlt,baroAlt,internalPressure,fixage,speed,course,ax,ay,az,gx,gy,gz,mx,my,mz,compass,humd,ExternalTemp,InternalTemp,UV Sensor Raw,Giger counter");
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
        countsPerMinute = 0; 
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

        //giger counter 
        /*
        if (ssGiger.available() > 0) {
             if (ssGiger.read() > 0)
                 gigercount++;
                 countsPerMinute = gigercount/(millis()/60000); 
        }
        */

	if(millis() - lastLog > 10000)	// log data every 10 sec
	{
		lastLog = millis();
		logData();
                ssGPS.listen();
	}
       /*
	if(millis() - lastTransmit > 5000) // transmit data every 1 sec
	{
		lastTransmit = millis();
		transmitData();
                ssGPS.listen();
	}
*/
}
