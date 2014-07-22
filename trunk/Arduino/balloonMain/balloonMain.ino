// data format is: "timestamp(millis),timestamp(gps),lat,lon,gpsAlt,fixage,speed,course,ax,ay,az,gx,gy,gz,mx,my,mz,humd,ExternalTemp,InternalTemp,bmpAlt"
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
    Serial.print(millis());
    Serial.print(',');
    Serial.print(time);
    Serial.print(',');
    Serial.print(lat);
    Serial.print(',');
    Serial.print(-1*lon);
    Serial.print(',');
    Serial.print(alt);
    Serial.print(',');
    Serial.print(fixage);
    Serial.print(',');
    Serial.print(speed);
    Serial.print(',');
    Serial.print(course);
    Serial.print(',');
    Serial.print(ax);
    Serial.print(',');
    Serial.print(ay);
    Serial.print(',');
    Serial.print(az);
    Serial.print(',');
    Serial.print(gx);
    Serial.print(',');
    Serial.print(gy);
    Serial.print(',');
    Serial.print(gz);
    Serial.print(',');
    Serial.print(mx);
    Serial.print(',');
    Serial.print(my);
    Serial.print(',');
    Serial.print(mz);
    Serial.print(',');
    Serial.print(humd);
    Serial.print(',');
    Serial.print(ExternalTemp);
    Serial.print(',');
    Serial.print(bmp.readTemperature)  //Internal Temperature
    Serial.print(',');
    Serial.print(bmp.readAltitude());
}
