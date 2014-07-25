
// grab all the libraries for sensors
#include <AltSoftSerial.h>  // for gps
#include <TinyGPS.h>  // for gps
#include <MPU6050.h>  // for gyro
#include <I2Cdev.h>  // for gyro
#include <Wire.h>  // for gyro and pressure senors
#include <Adafruit_BMP085.h>  // for pressure sensor
#include <SoftwareSerial.h>
#include <HTU21D.h>

// create all the objects we will need
SoftwareSerial ss(8,9);  // 8: gps in, 9: data logger
MPU6050 gyro(0x68);
TinyGPS gps;
Adafruit_BMP085 bmp;  // pressure sensor
HTU21D myHumidity;


// create global variables for use later
long lat, lon, alt;  // gps position
unsigned long fixAge, speed, course, lastLog, lastTransmit, date, time,gpsAlt,ExternalTemp,InternalTemp,humd;  // gps and timing data
int16_t ax, ay, az,gx,gy,gz,mx,my,mz;  // gyro data

void setup()  // runs once at power up
{
  pinMode(4, OUTPUT); //set up keycamera
  delay(100);  // wait for devices to power up
  digitalWrite(4, 0); //begin camera
  delay(100);
  digitalWrite(4, 1);
  delay(2000);
  digitalWrite(4, 0);
  Wire.begin();  // fire up the I2C interface
  Serial.begin(1200);  // main serial port for debug/radio interface
  ss.begin(9600);  // serial interface for the gps and datalogger
  delay(100);
  gyro.initialize();  // set up IMU
  if(!gyro.testConnection())
    ss.println("Gyro fail!");
  if(!bmp.begin())
    ss.println("BMP fail!");
  ss.println("timestamp(millis),timestamp(gps),date,lat,lon,gpsAlt,bmpAlt,fixage,speed,course,ax,ay,az,gx,gy,gz,mx,my,mz,humd,ExternalTemp,InternalTemp");
  lastLog = 0;
  lastTransmit = 0;
  fixAge = 0;
  speed = 0;
  course = 0;
  lat = 0;
  lon = 0; 
  gpsAlt = 0;
  time = 0;
}

void loop()
{
  while(ss.available())  // update the gps
  {
    if(gps.encode(ss.read()));
    {
      gps.get_position(&lat,&lon,&fixAge);
      gps.get_datetime(&date,&time);
      speed = gps.speed();
      course = gps.course();
      gpsAlt = gps.altitude();
    }
  }
  
  if(millis() - lastLog > 8000)  // log data every (8 + 2 (camera)) sec
  {
    lastLog = millis();
    logData();
  }
  if(millis() - lastTransmit > 500) // transmit data every .5 second
  {
    lastTransmit = millis();
    transmitData();
  }
}
