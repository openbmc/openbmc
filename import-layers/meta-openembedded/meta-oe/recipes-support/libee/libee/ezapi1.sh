#!/bin/sh
#
echo ---------------------------------------------------------------------------
echo A very basic test for the event class
echo ---------------------------------------------------------------------------
rm -f infile
./genfile 100 > infile
./ezapi1 -iinfile
if [ $? -ne 0 ] ; then
   echo "FAIL: $0"
   exit 1
fi
echo "PASS: $0"
rm -f infile
