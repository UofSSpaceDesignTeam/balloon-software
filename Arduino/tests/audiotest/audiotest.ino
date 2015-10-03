
//Usage instructions:
// open serial monitor, press t, hit enter
// remain quiet and still while the test runs
// runtime is about 20 seconds


void setup()
{
  Serial.begin(115200);
  pinMode(6,OUTPUT);
  pinMode(7,OUTPUT);
  digitalWrite(6,HIGH);
  digitalWrite(7,HIGH);
}

void loop()
{
  if(Serial.available())
  {
    if(Serial.read() == 't')
    {
      Serial.println("Running test...");
      digitalWrite(13,HIGH);
      long offset = 0;
      for(int i=0; i<20000; i++)
      {
        offset += analogRead(0);
      }
      offset /= 20000L;
      Serial.print("DC offset is ");
      Serial.println(offset);
      
      long noise = 0;
      for(int i=0; i<20000; i++)
      {
        noise += abs(analogRead(0) - offset);
      }
      noise /= 20000L;
      Serial.print("Noise level is ");
      Serial.println(noise);
      
      long measurement = 0;
      tone(12,1000);
      delay(10);
      for(int i=0; i<20000; i++)
      {
        measurement += abs(analogRead(0) - offset);
      }
      noTone(12);
      measurement /= 20000L;
      Serial.print("Measured value is ");
      Serial.println(measurement);
      Serial.print("Net acoustic transfer is ");
      Serial.println(measurement - noise);
      digitalWrite(13,LOW);
    }
  }
}
