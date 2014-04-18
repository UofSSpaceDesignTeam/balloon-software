# Serial Protocol that implements multiple levels of error checking.
# Uses Forward Error Correction to average 3 identical packets and CRC(Adler32) as a final check.
# Only default python libs now!


import sys
sys.dont_write_bytecode = True

from zlib import adler32
import struct
import serial

s_send = serial.Serial(bytesize=8, parity='N', stopbits=1)
s_recv = serial.Serial(bytesize=8, parity='N', stopbits=1, timeout = 1)

# Protocol Vars

plen = 128
fmt = '<4si' + str(plen) +'sq'
header = '\x7c'
footer = '\x7d'
dle = '\x7b'
sync = '\x7a'
afterdle = lambda x: chr(ord(x) ^ ord('\x20'))
binaryTypes=["img\x00", "bin\x00"]

# Returns list of 3 similar packets with different indexes for comparing bytes
def encode(type, data):
	# Initial packet variables
	type = str(type)
	data = str(data)
	dlength = len(data)
	index = 0
	crc = 0
	pack_list=[0,0,0]
	
	if(dlength > 128):
		return FEC_status(1)
	
	# Build three copies of the packet
	

	raw = struct.pack(fmt, type, dlength, data, crc)
	crc = adler32(str(raw)) & 0xffffffff #AND to fix issues on multiple OSes
	body = struct.pack(fmt, type, dlength, data, crc)
	
	packet = header
	
	for c in body:
		if c in (header, footer, dle, sync):
			packet = packet + (dle + afterdle(c))
		else:
			packet = packet + c
	packet = packet + footer
	pack_list = [packet, packet, packet]
	
	sync_pack = header + sync + footer
	s_send.write(sync_pack)
	
	for n in range(len(pack_list)):
		s_send.write(pack_list[n])
	
	return tuple(pack_list)

# Collects a synchronizer and 3 identical packets then averages bytes
def decode():
	global state
	state = 1
	global ser_buffer
	ser_buffer = ''
	global read
	
	# Get sync byte
	bufsync = ''
	while(bufsync != sync):
		bufsync = ser_buffer
		readbuffer(s_recv.read(1))
		#FEC_status(11)
	FEC_status(12)
	
	# Fill buffer 1
	read = True
	while(read == True):
		buf1 = ser_buffer
		readbuffer(s_recv.read(1))
	
	# Fill buffer 2
	read = True
	while(read == True):
		buf2 = ser_buffer
		readbuffer(s_recv.read(1))
	
	# Fill buffer 3
	read = True
	while(read == True):
		buf3 = ser_buffer
		readbuffer(s_recv.read(1))
	
	FEC_status(13)
	
	if(len(bufsync) == len(sync)):
		if(len(buf1) == plen+16):
			goodbuf = buf1
		elif(len(buf2) == plen+16):
			goodbuf = buf3
		elif(len(buf3) == plen+16):
			goodbuf = buf3
		else:
			return FEC_status(3)
	else:
		return FEC_status(6)
	
	# Lists allow modification of each byte
	buf1 = list(buf1.ljust(plen+16))
	buf2 = list(buf2.ljust(plen+16))
	buf3 = list(buf3.ljust(plen+16))
	
	data = list(''.ljust(plen+16))
	
	for c in range(len(goodbuf)):
		if(buf2[c] == buf1[c] and buf3[c] == buf1[c]):
			data[c] = buf1[c]
		elif(buf3[c] == buf2[c] and buf1[c] != buf2[c]):
			data[c] == buf2[c]
			FEC_status(10)
		elif(buf1[c] == buf3[c] and buf2[c] != buf3[c]):
			data[c] = buf3[c]
			FEC_status(10)
		else:
			return FEC_status(4)
	
	# Back to string, because struct is picky...
	# Then clean the null bytes
	data = ''.join(data)
	packet = struct.unpack(fmt, data)
	
	# For packets of binary data, do not clean the null bytes!!
	if(packet[0] in binaryTypes):
		output = packet
	else:
		output = clean_nulls(packet)
	
	# Run a final CRC check to make sure the FEC did not mutate data
	if(check(output) == True):
		return output
	else:
		return FEC_status(5)

# What to do with a byte from the serial
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
		FEC_status(14)
	if state == 4: # finalize data
		#print "footer found"
		read = False
		state = 1
		ser_buffer = ''
		
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
def FEC_status(type):
	if(type == 0):
		print("Unknown Error")
	if(type == 1):
		print("Error: Data longer than maximum packet length")
	if(type == 2):
		print("Error: Unexpected header - Read terminated")
	if(type == 3):
		print("Error: No good buffers")
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
		print("Info: All buffers complete")
	if(type == 14):
		print("Info: Escape character detected")
	if(type == 15):	
		print("Info: Serial read timeout")
	return