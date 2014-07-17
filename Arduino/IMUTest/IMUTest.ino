
#include "Wire.h"
#include "I2Cdev.h"
#include "MPU6050.h"

MPU6050 imu(0x68);

int16_t ax, ay, az, pitch, roll, initialPitch, initialRoll;

void setup()
{
  pinMode(7,OUTPUT);
  digitalWrite(7,HIGH);
  delay(500);
  Wire.begin();
  Serial.begin(9600);

  Serial.println("Initializing...");
  imu.initialize();

  Serial.println("Testing connection...");
  if(imu.testConnection())
  {
    Serial.println("IMU connection successful");
  }
  else
  {
    Serial.println("IMU connection failed");
  }
  delay(50);
  imu.getAcceleration(&ax, &ay, &az);

}

void loop()
{
  imu.getAcceleration(&ax, &ay, &az);
  pitch = (int)(degrees(atan2(ax,az)) - initialPitch);
  roll = (int)(degrees(atan2(ay,az)) - initialRoll);
  Serial.print("P/R:\t");
  Serial.print(pitch);
  Serial.print("\t");
  Serial.println(roll);
  delay(200);
}
