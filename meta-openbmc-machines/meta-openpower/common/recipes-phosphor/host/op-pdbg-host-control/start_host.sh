#!/bin/sh -e
# Starts POWER9 IPL (boot)

PDBG=${PDBG:-pdbg}
# Argument [device]: if provided, pass to pdbg as "-d [device]"
DEVICE_OPT=${1:+-d $1}

putcfam()
{
    $PDBG $1 -b fsi $DEVICE_OPT putcfam $2 $3 $4
}

#Clock mux select override
#Can maybe be removed in DD2
putcfam -a 0x2918 0x0000000C 0x0000000C

#Allow xstop/ATTN to flow to BMC
putcfam -p0 0x081C 0x20000000             # Setup FSI2PIB to report
putcfam -p0 0x100D 0x60000000             # Enable Xstop/ATTN interrupt
putcfam -p0 0x100B 0xFFFFFFFF             # Arm mechanism

putcfam -p0 0x283f 0x20000000             # Write scratch register 8
putcfam -p0 0x2801 0x80000000 0x80000000  # Set SBE start bit to start IPL
