#include <SoftwareSerial.h>
#include <TinyGPS.h>

TinyGPS gps;
SoftwareSerial ss(8,7);

void setup() {
  ss.begin(9600);
  Serial.begin(9600);
  Serial.println("GPS Test Start");
}

void loop() {
  unsigned long gpsChars;
  bool newData = false;
  
  for (unsigned long start = millis(); millis() - start < 1000;)
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
    Serial.println();
    Serial.print("POS: ");
    Serial.print(gpsLat);
    Serial.print(", ");
    Serial.println(gpsLon);
    Serial.print("ALT: ");
    Serial.println(gpsAlt);
    
    Serial.println();
    Serial.println("Example Packet");
    
    char serBufHead;
    char serBufLon;
    char serBufLat;
    char serBufAlt;
    
    serBufHead = 'G';
    serBufLon = gpsLon;
    serBufLat = gpsLat;
    serBufAlt = gpsAlt;
    Serial.print(serBufHead);
    Serial.print(serBufLon);
    Serial.print(serBufLat);
    Serial.println(serBufAlt);
    
  }
}
