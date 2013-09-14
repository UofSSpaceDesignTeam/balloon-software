// Written for the September 2013 HAB launch by Jordan Kubica

// grab all the libraries for sensors
#include <AltSoftSerial.h>  // for gps
#include <TinyGPS.h>  // for gps
#include <MPU6050.h>  // for gyro
#include <I2Cdev.h>  // for gyro
#include <Wire.h>  // for gyro and pressure senors
#include <Adafruit_BMP085.h>  // for pressure sensor

// create all the objects we will need
AltSoftSerial ss;  // pin 8 = input from gps
MPU6050 gyro(0x68);
TinyGPS gps;
Adafruit_BMP085 bmp;  // pressure sensor

// create global variables for use later
long lat, lon, alt;  // gps position
unsigned long fixAge, speed, course, lastLog, lastExp;  // gps and timing data
int16_t pitchRate, yawRate, rollRate;  // gyro data

void setup()  // runs once at power up
{
  pinMode(6,OUTPUT);  // powers mic amp
  pinMode(7,OUTPUT);  // powers gyro and pressure sensor
  digitalWrite(6,HIGH);
  digitalWrite(7,HIGH);
  delay(100);  // wait for devices to power up
  Wire.begin();  // fire up the I2C interface
  Serial.begin(9600);  // main serial port for debug/radio interface
  ss.begin(9600);  // serial interface for the gps
  gyro.initialize();  // set up IMU
  if(!gyro.testConnection())
    Serial.println("gyro is fail!");
  if(!bmp.begin())
    Serial.println("bmp is fail!");
  Serial.println("System power up and timer reset");
  lastLog = 0;
  lastExp = 0;
  fixAge = 0;
  speed = 0;
  course = 0;
  lat = 0;
  lon = 0;
  alt = 0;
}

void loop()
{
  while(ss.available())  // update the gps
  {
    if(gps.encode(ss.read()));
    {
      gps.get_position(&lat,&lon,&fixAge);
      speed = gps.speed();
      course = gps.course();
      alt = gps.altitude();
    }
  }
  
  if(millis() - lastLog > 10000)  // log data every (10 + runtime) sec
  {
    lastLog = millis();
    logData();
  }
  
  if(millis() - lastExp > 90000)  // run experiment every (90 + runtime) sec
  {
    lastExp = millis();
    logData();  // also log data first
    runExperiment();
  }
}
