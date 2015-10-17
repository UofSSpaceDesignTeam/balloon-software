
// grab all the libraries for sensors
//#include <AltSoftSerial.h>	// for gps
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
SoftwareSerial ssLogger(7,8);	
SoftwareSerial ssGPS(9,10);	
SoftwareSerial ssGiger(0,1);  

TinyGPS gps;
MPU6050 gyro;
//Adafruit_MCP9808  temp;
Adafruit_MPL3115A2 baro; // pressure sensor
Adafruit_SI1145 light;  
Adafruit_BMP085 bmp;	// 2nd pressure sensor
LSM303 compass;
HTU21D humidity; 

// create global variables for use later
long lat, lon, alt;	// gps position
unsigned long fixAge, speed, course, lastLog, lastTransmit, lastPicture, date, time;
unsigned long gpsAlt;	// gps and timing data
int16_t ax, ay, az, gx, gy, gz, mx, my, mz;	// gyro data
int gigercount; // giger counter 
float countsPerMinute; 

// add more variables as needed

unsigned long chars;
unsigned short sentences, failed;

void setup()	// runs once at power up
{
	pinMode(2, OUTPUT); //camra 1
        pinMode(3,OUTPUT); // camra 2
        pinMode(4,OUTPUT); // camra 3
        pinMode(5,OUTPUT); // status LED
	delay(100);	// wait for devices to power up
        //begin camera
	digitalWrite(2,0);
        digitalWrite(3,0);
        digitalWrite(4,0);
	delay(100);
	digitalWrite(2, 1);
        digitalWrite(3, 1);
        digitalWrite(4, 1);
	delay(2000);
	digitalWrite(2, 0);
        digitalWrite(3, 0);
        digitalWrite(4, 0);

	Wire.begin();	// fire up the I2C interface
	Serial.begin(4800);	// main serial port for debug/radio interface
	ssGPS.begin(9600);	// serial interface for the gps
	ssLogger.begin(4800);	//serial interface for the DataLogger
        ssGiger.begin(9600);  //serial interface for giger counter 
        
        // start sensors
	gyro.initialize();	// set up IMU
	if(!gyro.testConnection())
		ssLogger.println("Gyro fail!");
	if(!bmp.begin())
		ssLogger.println("BMP fail!");
        if(!baro.begin())
                ssLogger.println("Barometer fail!");
        if(!light.begin())
                ssLogger.println("light fail!");
       // if(!temp.begin())
        //        ssLogger.println("temp fail!");
        //start sensors
        humidity.begin(); 
        
        digitalWrite(6, 1); // turn on LED
  
	ssLogger.println("timestamp(millis),timestamp(gps),date,lat,lon,gpsAlt,baroAlt,internalPressure,fixage,speed,course,ax,ay,az,gx,gy,gz,mx,my,mz,compass,humd,ExternalTemp,InternalTemp,UV Sensor Raw,Giger counter");
	lastLog = 0;
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
        if (ssGiger.available() > 0) {
             if (ssGiger.read() > 0)
                 gigercount++;
                 countsPerMinute = gigercount/(millis()/60000); 
        }

	if(millis() - lastLog > 10000)	// log data every 10 sec
	{
		lastLog = millis();
		logData();
                ssGPS.listen();
	}
}
