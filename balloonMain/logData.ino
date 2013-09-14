
// data format is: timestamp,GPS time,lat,lon,fix age,GPS alt,speed,course,pressure,temp,BMP alt,yaw,pitch,roll
// timestamp in milliseconds since power up
// gps time in hhmmsscc UTC
// lat, lon in millionths of a degree
// GPS altitude in centimeters
// fix age in milliseconds
// speed in 100ths of a knot
// course in 100ths of a degree
// pressure in pascals
// temp in degrees C
// BMP altitude in meters
// to convert yaw, pitch, roll to deg/sec multiply by (500/32768)

void logData()  // records gps and sensor data to logger
{
  gyro.getRotation(&yawRate,&pitchRate,&rollRate);
  Serial.print(millis());
  Serial.print(',');
  Serial.print(time);
  Serial.print(',');
  Serial.print(lat);
  Serial.print(',');
  Serial.print(lon);
  Serial.print(',');
  Serial.print(alt);
  Serial.print(',');
  Serial.print(fixAge);
  Serial.print(',');
  Serial.print(speed);
  Serial.print(',');
  Serial.print(course);
  Serial.print(',');
  Serial.print(bmp.readPressure());
  Serial.print(',');
  Serial.print(bmp.readTemperature());
  Serial.print(',');
  Serial.print(bmp.readAltitude());
  Serial.print(',');
  Serial.print(yawRate);
  Serial.print(',');
  Serial.print(pitchRate);
  Serial.print(',');
  Serial.println(rollRate);
}
