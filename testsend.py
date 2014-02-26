import sys
sys.dont_write_bytecode = True

import serialFEC
serialFEC.s_send.baudrate = 9600
serialFEC.s_send.port = "COM7"
serialFEC.s_send.open()

import random
import math
import time

cycle = 0

while True:
	val = int(random.randint(60, 80) * (1 + math.sin(cycle)))
	
	serialFEC.encode("temp", val)
	
	#finishing the loop
	#time.sleep(1)
	cycle += 0.01
	if cycle >= 2 * math.pi:
		cycle = 0