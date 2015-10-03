#!/bin/bash

path='/home/pi/pictures'
logfile='/home/pi/camlog.txt'

mkdir -p $path
touch $logfile

echo camera script running
echo started at $(date) >> $logfile

i=0

while [ -f $path/img$i.jpg ]
do
	let i=i+1
done

echo starting image numbers at $i >> $logfile

while true
do
	raspicam -n -ex sports -q 95 -o $path/img$i.jpg
	echo took picture $i at $(date) >> $logfile
	let i=i+1
	sleep 5
done

