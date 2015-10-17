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
	Serial3.print(millis());
	Serial3.print(',');
	Serial3.print(time);
	Serial3.print(',');
	Serial3.print(date);
	Serial3.print(',');
	Serial3.print(lat);
	Serial3.print(',');
	Serial3.print(-1*lon);  //-1*lon b/c we know we're in the western hemisphere
	Serial3.print(',');
	Serial3.print(gpsAlt);
	Serial3.print(',');
	Serial3.print(baro.getAltitude());  //altitude 
	Serial3.print(',');
        Serial3.print(bmp.readPressure()); //internal pressure 
	Serial3.print(',');
	Serial3.print(fixAge);
	Serial3.print(',');
	Serial3.print(speed);
	Serial3.print(',');
	Serial3.print(course);
	Serial3.print(',');
	Serial3.print(ax);
	Serial3.print(',');
	Serial3.print(ay);
	Serial3.print(',');
	Serial3.print(az);
	Serial3.print(',');
	Serial3.print(gx);
	Serial3.print(',');
	Serial3.print(gy);
	Serial3.print(',');
	Serial3.print(gz);
	Serial3.print(',');
	Serial3.print(mx);
	Serial3.print(',');
	Serial3.print(my);
	Serial3.print(',');
	Serial3.print(mz);
	Serial3.print(',');
	Serial3.print(humidity.readHumidity());
	Serial3.print(',');
	Serial3.print(baro.getTemperature());  //external temp 
	Serial3.print(',');
	Serial3.print(bmp.readTemperature());  //Internal Temperature
	Serial3.print(',');
	Serial3.print(light.readVisible());  //visable light
	Serial3.print(',');
	Serial3.print((light.readUV())/100.0);  //uv sensor
        Serial3.print(',');
        Serial3.print(gigercount);
        Serial3.print(',');
        Serial3.println(countsPerMinute);
        /*
	Serial.print(millis());
	Serial.print(',');
	Serial.print(time);
	Serial.print(',');
	Serial.print(date);
	Serial.print(',');
	Serial.print(lat);
	Serial.print(',');
	Serial.print(-1*lon);  //-1*lon b/c we know we're in the western hemisphere
	Serial.print(',');
	Serial.print(gpsAlt);
	Serial.print(',');
	Serial.print(baro.getAltitude());  //altitude 
	Serial.print(',');
        Serial.print(bmp.readPressure()); //internal pressure 
	Serial.print(',');
	Serial.print(fixAge);
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
	Serial.print(humidity.readHumidity());
	Serial.print(',');
	Serial.print(baro.getTemperature());  //external temp 
	Serial.print(',');
	Serial.print(bmp.readTemperature());  //Internal Temperature
	Serial.print(',');
	Serial.print(light.readVisible());  //visable light
	Serial.print(',');
	Serial.print((light.readUV())/100.0);  //uv sensor
        Serial.print(',');
        Serial.print(gigercount);
        Serial.print(',');
        Serial.println(countsPerMinute);
        */
}


