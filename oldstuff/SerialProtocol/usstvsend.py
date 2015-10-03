import sys
sys.dont_write_bytecode = True

import serialFEC 
serialFEC.s_send.baudrate = 9600
serialFEC.s_send.port = "COM7"
serialFEC.s_send.open()

import os

os.system("java usstv.Encoder 5 5 0.2 < usstv/in.jpg > data.raw")

def getlength(file):
	lastpos = file.tell()
	file.seek(0,2)
	pos = file.tell()
	file.seek(lastpos,0)
	return pos
	
def sendfile(file):
	file.seek(0)
	for n in range(getlength(rawimg)/128):
		to_write = file.read(128)
		print len(to_write)
		serialFEC.encode("img",to_write)
		
	

rawimg = open("data.raw", "rb")
print rawimg
sendfile(rawimg)