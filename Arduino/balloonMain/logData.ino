// data format is: timestamp(millis),timestamp(gps),date,lat,lon,gpsAlt,bmpAlt,fixage,speed,course,ax,ay,az,gx,gy,gz,mx,my,mz,humd,ExternalTemp,InternalTemp
// two timestamps, one in milliseconds since power up, second from gps
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

void logData()
{
    gyro.getMotion9(&ax, &ay, &az, &gx, &gy, &gz, &mx, &my, &mz);
    humd =  myHumidity.readHumidity();
    ExternalTemp = myHumidity.readTemperature();
    ssLogger.print(millis());
    ssLogger.print(',');
    ssLogger.print(time);
    ssLogger.print(',');
    ssLogger.print(date);
    ssLogger.print(',');
    ssLogger.print(lat);
    ssLogger.print(',');
    ssLogger.print(-1*lon);  //-1*lon b/c we know we're in the western hemisphere
    ssLogger.print(',');
    ssLogger.print(gpsAlt);
    ssLogger.print(',');
    ssLogger.print(bmp.readAltitude());
    ssLogger.print(',');
    ssLogger.print(fixAge);
    ssLogger.print(',');
    ssLogger.print(speed);
    ssLogger.print(',');
    ssLogger.print(course);
    ssLogger.print(',');
    ssLogger.print(ax);
    ssLogger.print(',');
    ssLogger.print(ay);
    ssLogger.print(',');
    ssLogger.print(az);
    ssLogger.print(',');
    ssLogger.print(gx);
    ssLogger.print(',');
    ssLogger.print(gy);
    ssLogger.print(',');
    ssLogger.print(gz);
    ssLogger.print(',');
    ssLogger.print(mx);
    ssLogger.print(',');
    ssLogger.print(my);
    ssLogger.print(',');
    ssLogger.print(mz);
    ssLogger.print(',');
    ssLogger.print(humd);
    ssLogger.print(',');
    ssLogger.print(ExternalTemp);
    ssLogger.print(',');
    ssLogger.println(bmp.readTemperature());  //Internal Temperature

}
    //Send data over radio
 void transmitData()
{ 
    Serial.print("G");
    Serial.print(lat/100);
    Serial.print(",");
    Serial.print(lon/-100);
    Serial.print(",");
    Serial.println(gpsAlt);
    
    Serial.print("T");
    Serial.print(ExternalTemp);
    Serial.print(",");
    Serial.println(bmp.readTemperature());
}

void takePicture()
{
   //take a picture with the keycamera
   digitalWrite(4, 0);
   digitalWrite(4, 1);
   delay(2000);
   digitalWrite(4, 0);
}
  
