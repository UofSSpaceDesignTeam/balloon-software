# random number generator for testing purposes
# written by Austin Shirley
import sys
sys.dont_write_bytecode = True
import random
import math
import time
import serialprotocoltest
import serial

baudrate = 9600
device = "/dev/ttyAMA0"

def beginSerial():
	ser = serial.Serial(device, bytesize=8, parity='N', stopbits=1, )
	ser.baudrate = baudrate
	print(ser.name)
	return ser

# Sinusoidal number generator to test serial protocol

#ser=beginSerial()

cycle = 0

while True:
	val = int(random.randint(60, 80) * (1 + math.sin(cycle)))
	print("Temp: " + str(val))
	wrap = serialprotocoltest.sPack("temp", val)
	print("Wrapped: " + str(wrap))
	#ser.write(wrap)
	unwrap = serialprotocoltest.clean(serialprotocoltest.sUnpack(wrap))
	print("Unwrapped: " + str(unwrap))
	serialprotocoltest.check(unwrap)
	time.sleep(.25)
	cycle += 0.01
	if cycle >= 2 * math.pi:
		cycle = 0