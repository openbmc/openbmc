#!/bin/sh

# Set mctpi2c link up and assign local address.
localEid=8

# The Sentinel Dome BICs are on the i2c bus0 to bus7.
busnum=0
while [ $busnum -le 7 ]
do
    mctp link set mctpi2c${busnum} up
    mctp addr add ${localEid} dev mctpi2c${busnum}
    busnum=$((busnum+1))
done

# The NICs are on the i2c bus24 to bus27.
busnum=24
while [ $busnum -le 27 ]
do
    mctp link set mctpi2c${busnum} up
    mctp addr add ${localEid} dev mctpi2c${busnum}
    busnum=$((busnum+1))
done
