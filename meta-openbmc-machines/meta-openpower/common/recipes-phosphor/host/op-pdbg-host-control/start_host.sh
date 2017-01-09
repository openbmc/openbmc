#!/bin/sh -e
# Starts POWER9 IPL (boot)

PDBG=${PDBG:-pdbg}
# Argument [device]: if provided, pass to pdbg as "-d [device]"
DEVICE_OPT=${1:+-d $1}

putcfam()
{
    $PDBG -p0 -b fsi $DEVICE_OPT putcfam $1 $2 $3
}

putcfam 0x283f 0x20000000             # Write scratch register 8
putcfam 0x2801 0x80000000 0x80000000  # Set SBE start bit to start IPL
