# Serial data protocol (library) for high altitude balloon
# Written by Austin

import sys
sys.dont_write_bytecode = True
from zlib import adler32
import struct
import time
import serial

# Encoding Vars
fmt = '<10sdi128sq' # data format: little-endian string, double, int, string, long-long
header='\x7c'
footer='\x7d'
dle='\x7b' # escape
afterdle=lambda x: chr(ord(x) ^ ord('\x20')) # Uses an XOR Swap to 'hide' any escaped characters

# Packs data and timestamp into a convenient packet.
def sPack(type,data):
	data=str(data)
	dlength=len(data)
	ts=time.time()
	crc = 0
	raw = struct.pack(fmt, str(type), ts, dlength, str(data), crc)
	crc=adler32(str(raw)) & 0xffffffff
	packed = struct.pack(fmt, str(type), ts, dlength, str(data), crc)
	#print packed
	
	# Wrap the packed data inside a header and footer, escaping any problematic chars
	wrap = header
	for c in packed:
		if c in (header, footer, dle):
			wrap = wrap + (dle + afterdle(c))
		else:
			wrap = wrap + c
	wrap = wrap + footer
	return wrap

def fks(pack): # fake serial for now to emulate receiving one byte at a time
	if n < len(pack):
		out = pack[n]
	else:
		out = footer
			
	return out	

def sUnpack(pack): # parses serial data for packets and unpacks in steps
	global n # for fake serial
	n = 0
	databuf = ''
	state = 1 # wait for header
	while state < 5:
		while state == 1:
			if fks(pack) == header:
				state = 2
				n += 1
				#print("header found")
		while state == 2: # collect bytes
			if fks(pack) == dle:
				state = 3
				n += 1
				#print("escape found")
			elif fks(pack) == footer:
				state = 4
				n += 1
				#print("footer found")
			else: # regular data
				databuf = databuf + fks(pack)
				n += 1
		while state == 3: # handle escapes
			databuf = databuf + afterdle(fks(pack))
			state = 2
			n += 1
			print("escape handled")
		while state == 4: # finalize data
			data = databuf
			n = 0
			state = 5
			#print("done")
	
	#print(len(data))
	#print(data)
	result = struct.unpack(fmt, data)
	return result
	
def clean(result): # cleans out all the zero bytes
	clean = [0,0,0,0,0]
	for n in range(len(result)):
		try:
			clean[n] = result[n].split('\x00')[0]
		except:
			clean[n] = result[n]
	clean = tuple(clean)
	return clean

def check(result): # computes the original hash and compares it to the received hash
	raw = result[:-1] + (0,) 
	raw = struct.pack(fmt, raw[0], raw[1], raw[2], raw [3], raw[4])
	crc=adler32(raw) & 0xffffffff
	if result[4] == crc:
		integrity = True
		print("CRC check passed")
	else:
		integrity = False
		print("CRC check failed")
	return integrity