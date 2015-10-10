// data format is: timestamp(millis),timestamp(gps),date,lat,lon,gpsAlt,baroAlt,internalPressure,fixage,speed,course,ax,ay,az,gx,gy,gz,mx,my,mz,compass,humdity,ExternalTemp,InternalTemp,visible light,UV,Giger counter
// two timestamps, one in milliseconds since power up, second from gps
// gps time in hhmmsscc UTC
// lat, lon in millionths of a degree
// GPS altitude in centimeters
// baro altitude in meters
// internal pressure in pascales
// fix age in milliseconds
// speed in 100ths of a knot
// course in 100ths of a degree
// accel in
// gyro in 
// mag in 
// compass in degrees 
// humdity in 
// external temp in C
// internal temp n C
// visible light
// UV in 
// giger counter in 


// pressure in pascals
// temp in degrees C
// BMP altitude in meters
// to convert yaw, pitch, roll to deg/sec multiply by (500/32768)

void logData()
{
	gyro.getMotion9(&ax, &ay, &az, &gx, &gy, &gz, &mx, &my, &mz);
	//humd = myHumidity.readHumidity();
	//ExternalTemp = myHumidity.readTemperature();
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
	ssLogger.print(baro.getAltitude());  //altitude 
	ssLogger.print(',');
        ssLogger.print(bmp.readPressure()); //internal pressure 
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
	ssLogger.print(humidity.readHumidity());
	ssLogger.print(',');
	ssLogger.print(baro.getTemperature());  //external temp 
	ssLogger.print(',');
	ssLogger.print(bmp.readTemperature());  //Internal Temperature
	ssLogger.print(',');
	ssLogger.print(light.readVisible());  //visable light
	ssLogger.print(',');
	ssLogger.print((light.readUV())/100.0);  //uv sensor
        ssLogger.print(',');
        ssLogger.print(gigercount);
        ssLogger.print(',');
        ssLogger.println(countsPerMinute);
}
/*
// Send data over radio
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
*/

