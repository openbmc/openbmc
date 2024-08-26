#!/bin/sh

# Set mctpi2c/mctpi3c link up and assign local address.
localEid=8

# shellcheck source=meta-facebook/meta-yosemite4/recipes-yosemite4/plat-tool/files/yosemite4-common-functions
. /usr/libexec/yosemite4-common-functions

is_nuvoton_board="$(check_nuvoton_board)"

busnum=0
if [ -n "$is_nuvoton_board" ]; then
    # Enable MCTP-I3C for Nuvoton BMC
    # The Sentinel Dome BICs are after the I3C hub on I3C bus0 and bus1.
    while [ $busnum -le 1 ]
    do
        mctp link set mctpi3c${busnum} up
        mctp addr add ${localEid} dev mctpi3c${busnum}
        busnum=$((busnum+1))
    done
else
    # Enable MCTP-I2C for ASPEED BMC
    # The Sentinel Dome BICs are on the i2c bus0 to bus7.
    while [ $busnum -le 7 ]
    do
        mctp link set mctpi2c${busnum} up
        mctp addr add ${localEid} dev mctpi2c${busnum}
        busnum=$((busnum+1))
    done
fi

# The NICs are on the i2c bus24 to bus27.
busnum=24
while [ $busnum -le 27 ]
do
    mctp link set mctpi2c${busnum} up
    mctp addr add ${localEid} dev mctpi2c${busnum}
    busnum=$((busnum+1))
done
