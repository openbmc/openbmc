#!/bin/sh

# enable VCS rail with OPERATION
# A side
i2cset -y 4 0x70 0x00 0x01 b
i2cset -y 4 0x70 0x02 0x1A b #respond to OPERATION
i2cset -y 4 0x70 0x00 0x00 b
# B side
i2cset -y 5 0x70 0x00 0x01 b
i2cset -y 5 0x70 0x02 0x1A b #respond to OPERATION
i2cset -y 5 0x70 0x00 0x00 b

# This causes CFAM operations to second processor to fail
# TODO openbmc/openbmc#2204

# unbind ucd driver to permit i2cset
#ucd_retries=5
#ucd=

#ucdpath="/sys/bus/i2c/drivers/ucd9000"
#if [ -e $ucdpath ]
#then
#  ucd=`ls -1 $ucdpath | grep 64`
#  if [ -n "$ucd" ]
#  then
#    echo $ucd > $ucdpath/unbind
#  fi
#fi

# re-enable VCS in system PGOOD
#sleep 1
#SYSTEM_RESET_CONFIG
#i2cset -y 11 0x64 0xD2 0x09 0xFF 0xFF 0x27 0x0A 0x00 0x06 0x00 0x00 0x02 i

# re-bind ucd driver only if we unbound it (i.e. ucd has been set with a value)
#if [ -e $ucdpath -a -n "$ucd" ]; then
#  j=0
#  until [ $j -ge $ucd_retries ] || [ -e $ucdpath/$ucd ]; do
#    j=$((j+1))
#    echo $ucd > $ucdpath/bind || ret=$?
#    if [ $j -gt 1 ]; then
#      echo "rebinding UCD driver. Retry number $j"
#      sleep 1
#    fi
#  done
#  if [ ! -e $ucdpath/$ucd ]; then exit $ret; fi
#fi
