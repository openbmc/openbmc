#!/bin/sh -e
# Apply fixes over FSI to POWER9 hosts. Required before VCS rail on.

PDBG=${PDBG:-pdbg}
# Argument [device]: if provided, pass to pdbg as "-d [device]"
DEVICE_OPT=${1:+-d $1}

putcfam()
{
    $PDBG -b fsi $DEVICE_OPT $1 putcfam $2 $3 $4
}

# P9 dd1 required workaround needed before powering VCS rails
p9_dd1_vcs_workaround()
{
    putcfam -a 0x2810 0x00000000 0x00010000 &&  # Unfence PLL controls
    putcfam -a 0x281A 0x40000000 0x40000000 &&  # Assert Perv chiplet endpoint reset, just in case
    putcfam -a 0x281A 0x00000001 0x00000001     # Enable Nest PLL
}

# Put the CFAM/FSI slave into async mode
putcfam -p0 0x900 1

#Set hMFSI error recovery
putcfam -p0 0x34b8 0x0000c000

#Set hMFSI timings
putcfam -p0 0x3400 0xd0040110
putcfam -p0 0x3401 0xffff0000

#Enable hMFST ports
putcfam -p0 0x3404 0xf0000000

p9_dd1_vcs_workaround
