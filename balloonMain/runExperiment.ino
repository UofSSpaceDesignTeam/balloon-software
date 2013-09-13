
void runExperiment()  // runs an acoustic measurement and records to logger, takes ~25 seconds
{
  Serial.println("run exp");
  //ss.println("run exp");
  digitalWrite(13,HIGH);
  long offset = 0;
  for(int i=0; i<20000; i++)  // get DC output level of mic amp
  {
    offset += analogRead(0);
  }
  offset /= 20000L;
  Serial.print("offset = ");
  Serial.println(offset);
  ss.print("offset = ");
  ss.println(offset);
  
  long noise = 0;
  for(int i=0; i<20000; i++)  // get average noise level with speaker off
  {
    noise += abs(analogRead(0) - offset);
  }
  noise /= 20000L;
  Serial.print("noise = ");
  Serial.println(noise);
  //ss.print("noise = ");
  //ss.println(noise);
  
  long measurement = 0;
  tone(12,1000);  // turn speaker on and get actual data
  delay(50);  // wait for speaker to start
  for(int i=0; i<20000; i++)
  {
    measurement += abs(analogRead(0) - offset);
  }
  noTone(12);  // turn speaker off
  measurement /= 20000L;
  Serial.print("measured = ");
  Serial.println(measurement);
  //ss.print("measured = ");
  //ss.println(measurement);
  Serial.print("net = ");
  Serial.println(measurement - noise);
  Serial.println("end exp");
  digitalWrite(13,LOW);
  //ss.print("net = ");
  //ss.println(measurement - noise);
  //ss.println("end exp");
}
