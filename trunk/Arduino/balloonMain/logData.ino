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
    ss.print(millis());
    ss.print(',');
    ss.print(time);
    ss.print(',');
    ss.print(date);
    ss.print(',');
    ss.print(lat);
    ss.print(',');
    ss.print(-1*lon);  //-1*lon b/c we know we're in the western hemisphere
    ss.print(',');
    ss.print(gpsAlt);
    ss.print(',');
    ss.print(bmp.readAltitude());
    ss.print(',');
    ss.print(fixAge);
    ss.print(',');
    ss.print(speed);
    ss.print(',');
    ss.print(course);
    ss.print(',');
    ss.print(ax);
    ss.print(',');
    ss.print(ay);
    ss.print(',');
    ss.print(az);
    ss.print(',');
    ss.print(gx);
    ss.print(',');
    ss.print(gy);
    ss.print(',');
    ss.print(gz);
    ss.print(',');
    ss.print(mx);
    ss.print(',');
    ss.print(my);
    ss.print(',');
    ss.print(mz);
    ss.print(',');
    ss.print(humd);
    ss.print(',');
    ss.print(ExternalTemp);
    ss.print(',');
    ss.println(bmp.readTemperature());  //Internal Temperature

}
