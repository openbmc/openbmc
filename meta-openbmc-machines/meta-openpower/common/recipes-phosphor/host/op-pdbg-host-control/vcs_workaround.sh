#!/bin/sh

# #############################################################
# usage: putcfam address andmask ormask
putcfam()
{
din=`pdbg-p9 -b fsi getcfam $1` &&
{
  din=`echo $din | cut -d ' ' -f 2`
  dout=`printf "0x%016x" $(($(($din & $2)) | $3))`
  pdbg-p9 -b fsi putcfam $1 $dout
}
return
}

# #############################################################
# P9 dd1 required workaround needed before powering VCS rails
p9_dd1_vcs_workaround()
{
putcfam 0x2810 0xFFFEFFFF 0 &&            # Unfence PLL controls
putcfam 0x281A 0xFFFFFFFF 0x80000000 &&   # Assert Perv chiplet endpoint reset, just in case
putcfam 0x281A 0xFFFFFFFF 1               # Enable Nest PLL
return
}

# put the CFAM/FSI slave into async mode
pdbg-p9 -b fsi putcfam 0x900 0x1
pdbg-p9 -b fsi putcfam 0x2801 0x80C00000

# run workaround
p9_dd1_avsbus_workaround

