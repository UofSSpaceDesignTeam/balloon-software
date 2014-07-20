import sys
sys.dont_write_bytecode = True

import serialFEC
serialFEC.s_recv.baudrate = 9600
serialFEC.s_recv.port = "COM8"
serialFEC.s_recv.open()

raw_file = open("dataout.raw","w+b")
new_data = ''
print raw_file

while True:
	data_recv = serialFEC.decode()
	if data_recv != new_data:
		if data_recv is not None:
			new_data = data_recv
			raw_file.write(new_data[2])
			print(str(new_data[1]) + "bytes written")
	else:
		print("No new data")
	