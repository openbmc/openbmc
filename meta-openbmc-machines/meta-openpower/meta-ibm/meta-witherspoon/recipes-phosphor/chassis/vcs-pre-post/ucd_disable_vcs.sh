#!/bin/sh
# #############################################################
# script to disable UCD VCS rails.
# This REQUIRES unaltered/original UCD cfg file.
# 10/28/16 PGR -
# 11/10/16 PGR - unbind/bind UCD driver

ucd_retries=5
ucd=

# unbind ucd driver to permit i2cset
ucdpath="/sys/bus/i2c/drivers/ucd9000"
if [ -e $ucdpath ]
then
  ucd=`ls -1 $ucdpath | grep 64`
  if [ -n "$ucd" ]
  then
      echo $ucd > $ucdpath/unbind
  fi
fi

## program UCD to bypass VCS (DD1 issue)
## move memory enables to align with VDN (VDN to VDDR leakage issue)
## remove GPU PGOOD from system reset.
#GPO_CONFIG_1 (GPIO15)
i2cset -y 11 0x64 0xF7 0x00 i
i2cset -y 11 0x64 0xF8 0x15 0x6E 0x80 0x08 0x00 0x00 0x00 0x40 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 i
#GPO_CONFIG_2 (GPIO7)
i2cset -y 11 0x64 0xF7 0x01 i
i2cset -y 11 0x64 0xF8 0x15 0x16 0x80 0x08 0x00 0x00 0x20 0x40 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 i
#SYSTEM_RESET_CONFIG
i2cset -y 11 0x64 0xD2 0x09 0x3F 0xFF 0x27 0x0A 0x00 0x06 0x00 0x00 0x02 i
#ON_OFF_CONFIG rail 15
i2cset -y 11 0x64 0x00 0x0E i
i2cset -y 11 0x64 0x02 0x1A i
#ON_OFF_CONFIG rail 16
i2cset -y 11 0x64 0x00 0x0F i
i2cset -y 11 0x64 0x02 0x1A i
# change VDN delays based on UCD MFR_REVISION setting
REV=`i2cget -y 11 0x64 0x9B i 2|cut -f2 -d' '`
if [ "$REV" == "0x01" -o "$REV" == "0x02" ] ; then
  # use 20ms delay for VDN
  #TON_DELAY rail 8
  i2cset -y 11 0x64 0x00 0x07 i
  i2cset -y 11 0x64 0x60 0x80 0xDA i
  #TON_DELAY rail 9
  i2cset -y 11 0x64 0x00 0x08 i
  i2cset -y 11 0x64 0x60 0x80 0xDA i
else
  # use 70ms delay for VDN
  #TON_DELAY rail 8
  i2cset -y 11 0x64 0x00 0x07 i
  i2cset -y 11 0x64 0x60 0x30 0xEA i
  #TON_DELAY rail 9
  i2cset -y 11 0x64 0x00 0x08 i
  i2cset -y 11 0x64 0x60 0x30 0xEA i
fi

# re-bind ucd driver only if we unbound it (i.e. ucd has been set with a value)
if [ -e $ucdpath -a -n "$ucd" ]; then
  j=0
  until [ $j -ge $ucd_retries ] || [ -e $ucdpath/$ucd ]; do
      j=$((j+1))
      echo $ucd > $ucdpath/bind || ret=$?
      if [ $j -gt 1 ]; then
          echo "rebinding UCD driver. Retry number $j"
          sleep 1
      fi
  done
  if [ ! -e $ucdpath/$ucd ]; then exit $ret; fi
fi

