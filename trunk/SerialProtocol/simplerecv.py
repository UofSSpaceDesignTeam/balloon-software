import sys
sys.dont_write_bytecode = True

import serialProtocol
serialProtocol.s_recv.baudrate = 1200
serialProtocol.s_recv.port = "COM8"
serialProtocol.s_recv.open()

while True:
	data_recv = serialProtocol.decode()
	print data_recv
	