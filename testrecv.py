import sys
sys.dont_write_bytecode = True

import serialFEC
serialFEC.s_recv.baudrate = 9600
serialFEC.s_recv.port = "COM8"
serialFEC.s_recv.open()

while True:
	print serialFEC.decode()