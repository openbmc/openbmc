#!/bin/sh -e
# Apply fixes over FSI to POWER9 hosts. Required before VCS rail on.

PDBG=${PDBG:-pdbg}
# Argument [device]: if provided, pass to pdbg as "-d [device]"
DEVICE_OPT=${1:+-d $1}

putcfam()
{
    $PDBG -b fsi $DEVICE_OPT putcfam $1 $2 $3
}

# P9 dd1 required workaround needed before powering VCS rails
p9_dd1_vcs_workaround()
{
    putcfam 0x2810 0x00000000 0x00010000 &&  # Unfence PLL controls
    putcfam 0x281A 0x40000000 0x40000000 &&  # Assert Perv chiplet endpoint reset, just in case
    putcfam 0x281A 0x00000001 0x00000001     # Enable Nest PLL
}

# Put the CFAM/FSI slave into async mode
putcfam 0x900 1

p9_dd1_vcs_workaround
