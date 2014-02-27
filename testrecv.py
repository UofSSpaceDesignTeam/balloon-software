import sys
sys.dont_write_bytecode = True

import serialFEC
serialFEC.s_recv.baudrate = 1200
serialFEC.s_recv.port = "COM6"
serialFEC.s_recv.open()

while True:
	print serialFEC.decode()