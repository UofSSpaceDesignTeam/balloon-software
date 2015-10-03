#include <SoftwareSerial.h>
#include <TinyGPS.h>

TinyGPS gps;
SoftwareSerial ss(8,7);

void setup() {
  ss.begin(9600);
  Serial.begin(9600);
  //Serial.println("GPS Test Start");
}

void loop() {
  unsigned long gpsChars;
  bool newData = false;
  
  for (unsigned long start = millis(); millis() - start < 100;)
  {
    while (ss.available())
    {
      char c = ss.read();
      if (gps.encode(c))
        newData = true;
    }
  }
  if (newData)
  {
    long int gpsLat, gpsLon;
    long int gpsAlt;
    unsigned long gpsAge;
    gps.get_position(&gpsLat, &gpsLon, &gpsAge);
    gpsLon = gpsLon * -1;
    gpsAlt = gps.altitude();
    Serial.print("G");
    Serial.print(gpsLat/100);
    Serial.print(",");
    Serial.print(gpsLon/100);
    Serial.print(",");
    Serial.println(gpsAlt);
  }
}
