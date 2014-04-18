# Plain Serial Protocol that does not do any error checking
# Probably needs to be rewritten with iterators instead of loops.. :)
# Uses only default Python libs now!

import sys
sys.dont_write_bytecode = True

from zlib import adler32
import struct
import serial

s_send = serial.Serial(bytesize=8, parity='N', stopbits=1)
s_recv = serial.Serial(bytesize=8, parity='N', stopbits=1, timeout = 1)

# Protocol Vars
plen = 128 # Binary packet length
fmt = '<4si' + str(plen) +'sq'
header = '\x7c'
footer = '\x7d'
dle = '\x7b' # escape 
afterdle = lambda x: chr(ord(x) ^ ord('\x20')) # used to escape bytes
binaryTypes=["img\x00", "bin\x00"] # expected binary formats for better handling by Struct

def encode(type, data):
	# initial packet variables
	type = str(type)
	data = str(data)
	dlength = len(data)
	index = 0
	crc = 0
	
	if(dlength > 128):
		return Serial_status(1)
	
	raw = struct.pack(fmt, type, dlength, data, crc)
	crc = adler32(str(raw)) & 0xffffffff # AND to fix issues between Linux and NT
	body = struct.pack(fmt, type, dlength, data, crc)
	
	packet = header
	
	for c in body:
		if c in (header, footer, dle):
			packet = packet + (dle + afterdle(c))
		else:
			packet = packet + c
	packet += footer	
	
	s_send.write(packet)
	
	return packet

def decode():
	global state
	state = 1
	global ser_buffer
	global read
	
	# Fill buffer
	ser_buffer = ''
	read = True
	while(read == True):
		readbuffer(s_recv.read(1))
	data = ser_buffer

	Serial_status(13)
	try:
		packet = struct.unpack(fmt, data)
	except:
		Serial_status(3)
		print "Raw packet dump: " + data 
		return None
	
	# For packets of binary data, do not clean the null bytes!!
	if(packet[0] in binaryTypes):
		output = packet
	else:
		output = clean_nulls(packet)
	
	# Run a final CRC check to make sure the Serial did not mutate data
	if(check(output) == True):
		return output
	else:
		return Serial_status(5)

# Handles incoming serial bytes
def readbuffer(new_char):
	global state
	global ser_buffer
	global read
	
	if state == 1:
		if new_char == header:
			ser_bufer = ''
			state = 2
			#print("header found")
	elif state == 2:
		if new_char == dle:
			state = 3
			#print("escape found")
		elif new_char == footer:
			state = 4
		else:
			ser_buffer += new_char
	elif state == 3:
		ser_buffer += afterdle(new_char)
		state = 2
		Serial_status(14)
	if state == 4: # finalize data
		#print "footer found"
		read = False
		state = 1
		
# Remove the null bytes that were used as padding
def clean_nulls(orig_packet):
	clean_packet = [0,0,0,0]
	for n in range(len(orig_packet)):
		try:
			clean_packet[n] = orig_packet[n].split('\x00')[0]
		except:
			clean_packet[n] = orig_packet[n]
			
	return tuple(clean_packet)

# CRC check, reverse of packing method	
def check(packet):
	raw = packet[:-1] + (0,) 
	raw = struct.pack(fmt, raw[0], raw[1], raw[2], raw [3])
	crc=adler32(raw) & 0xffffffff
	if packet[3] == crc:
		integrity = True
	else:
		integrity = False
	return integrity

# Debug Messages
def Serial_status(type):
	if(type == 0):
		print("Unknown Error")
	if(type == 1):
		print("Error: Data longer than maximum packet length")
	if(type == 2):
		print("Error: Unexpected header - Read terminated")
	if(type == 3):
		print("Error: Unexpected Data Length")
	if(type == 4):
		print("Error: All buffers mismatch")
	if(type == 5):
		print("Error: CRC Check Failed")
	if(type == 6):
		print("Error: No Sync Packet.. listening")
	if(type == 10):
		print("Info: buffer character discarded")
	if(type == 11):
		print("Info: Waiting for sync Packet")
	if(type == 12):
		print("Info: Sync packet found")
	if(type == 13):
		print("Info: Buffer complete")
	if(type == 14):
		print("Info: Escape character detected")
	if(type == 15):	
		print("Info: Serial read timeout")
	return