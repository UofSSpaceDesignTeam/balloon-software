// grab all the libraries for sensors
//#include <AltSoftSerial.h>	// for gps
#include <TinyGPS.h>	// for gps
#include <I2Cdev.h>	
#include <Wire.h>	
#include <SoftwareSerial.h>
#include <Adafruit_MCP9808.h> //temp sensor
#include <Adafruit_MPL3115A2.h>  //pressure sensor 
#include <Adafruit_BMP085.h>	// sparkfun pressure sensor
#include <MPU6050.h>	// for gyro
#include <LSM303.h>  // for compass
#include <SparkFunHTU21D.h> // humidity 


// create all the objects we will need
//giger = Serial1; 
//gps = Serial2;	
//logger = Serial3;	


TinyGPS gps;
MPU6050 gyro;
Adafruit_MCP9808  temp  = Adafruit_MCP9808(); // Internal temp 
Adafruit_MPL3115A2 baro = Adafruit_MPL3115A2();// external sensor 
Adafruit_BMP085 bmp;	// internal pressure sensor
LSM303 compass;
HTU21D humidity; 

// create global variables for use later
long lat, lon, alt;	// gps position
unsigned long fixAge, speed, course, lastLog, lastTransmit, lastPicture, date, time;
unsigned long gpsAlt;	// gps and timing data
int16_t ax, ay, az, gx, gy, gz, mx, my, mz;	// gyro data
int gigercount; // giger counter 
unsigned long long countsPerMinute; 

boolean gyroRunning, bmpRunning, baroRunning, tempRunning; 

unsigned long chars;
unsigned short sentences, failed;

void setup()	// runs once at power up
{
	pinMode(2, OUTPUT); //camra 1
  pinMode(3,OUTPUT); // camra 2
  pinMode(4,OUTPUT); // camra 3
  pinMode(5,OUTPUT); // status LED
	
	delay(1500);	// wait for devices to power up
        //begin camera
	digitalWrite(2,0);
  digitalWrite(3,0);
  digitalWrite(4,0);
	delay(100);
	
	digitalWrite(2, 1);
  digitalWrite(3, 1);
  digitalWrite(4, 1);
	delay(4000);
	
	digitalWrite(2, 0);
  digitalWrite(3, 0);
  digitalWrite(4, 0);

	Wire.begin();	// fire up the I2C interface
	//Serial.begin(4800);	// main serial port for debug/radio interface
  Serial1.begin(9600);  //serial interface for giger counter
	Serial2.begin(9600);	// serial interface for the gps
	Serial3.begin(9600);	//serial interface for the DataLogger/Radio
        
        
        // start sensors
	gyro.initialize();	// set up IMU
	if(!gyro.testConnection()) {
                gyroRunning = false;  
		Serial3.println("Gyro fail!");
        } 
        else 
        {
                gyroRunning = true;
        }
        
	if(!bmp.begin()) 
        {
                bmpRunning = false;  
		Serial3.println("Internal Barometer fail!");
        } 
        else 
        {
                bmpRunning = true;
        }
        
        if(!baro.begin()) 
        {
                baroRunning = false;  
                Serial3.println("external Barometer fail!");

        } 
        else 
        {
                baroRunning = true;
        }
        
        if(!temp.begin()) 
        {
                tempRunning = false;  
                Serial3.println("temp fail!");
        } 
        else 
        {
                tempRunning = true;
        }
        
        humidity.begin(); 
        
        //set up compass
        compass.init();
        compass.enableDefault();
        compass.m_min = (LSM303::vector<int16_t>){-32767, -32767, -32767};
        compass.m_max = (LSM303::vector<int16_t>){+32767, +32767, +32767};
        
        
        digitalWrite(5, 1); // turn on LED
  
	Serial3.println("timestamp(millis),timestamp(gps),date,lat,lon,gpsAlt,baroAlt,internalPressure,fixage,speed,course,ax,ay,az,gx,gy,gz,mx,my,mz,compass,humd,ExternalTemp,InternalTemp,Giger_counter");
        lastLog = 0;
	fixAge = 0;
	speed = 0;
	course = 0;
	lat = 0;
	lon = 0; 
	gpsAlt = 0;
	time = 0;
        countsPerMinute = 0; 
       
       ax = 0;
       ay = 0;
       az = 0;
       gx = 0;
       gy = 0;
       gz = 0;
       mx = 0;
       my = 0;
       mz = 0;
}

void loop()
{
	while(Serial2.available())	// update the gps
	{
		if(gps.encode(Serial2.read()));
		{
			gps.get_position(&lat,&lon,&fixAge);
			gps.get_datetime(&date,&time);
			speed = gps.speed();
			course = gps.course();
			gpsAlt = gps.altitude();
		}
	}

        //giger counter 
        if (Serial1.available() > 0) {
             if (Serial1.read() > 0)
                 gigercount++;
                 countsPerMinute = gigercount/(millis()/60000); 
        }

	if(millis() - lastLog > 10000)	// log data every 10 sec
	{
		lastLog = millis();
		logData();
                //Serial2.listen();
	}
}
