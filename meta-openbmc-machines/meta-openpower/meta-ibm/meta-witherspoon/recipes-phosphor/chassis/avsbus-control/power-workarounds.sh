#!/bin/sh

i2cset -y 4 0x12 0x2E 0x23 b # VDD/VCS 0
i2cset -y 4 0x13 0x2E 0x23 b # VDN 0
i2cset -y 5 0x12 0x2E 0x23 b # VDD/VCS 1
i2cset -y 5 0x13 0x2E 0x23 b # VDN 1

# A side VDDR - set to 1.23V
i2cset -y 4 0x71 0x00 0x01
i2cset -y 4 0x71 0x21 0x3B 0x01 i
i2cset -y 4 0x71 0x00 0x00

# B side VDDR - set to 1.23V
i2cset -y 5 0x71 0x00 0x01
i2cset -y 5 0x71 0x21 0x3B 0x01 i
i2cset -y 5 0x71 0x00 0x00

# VDN A - PGOOD_ON threshold
i2cset -y 4 0x71 0x00 0x00 b # PAGE
i2cset -y 4 0x71 0x5E 0xCD 0x00 i # set to 0.8V

# VDN B - PGOOD_ON threshold
i2cset -y 5 0x71 0x00 0x00 b # PAGE
i2cset -y 5 0x71 0x5E 0xCD 0x00 i # set to 0.8V

# unbind ucd driver to permit i2cset
ucd_retries=5
ucd=

ucdpath="/sys/bus/i2c/drivers/ucd9000"
if [ -e $ucdpath ]
then
  ucd=`ls -1 $ucdpath | grep 64`
  if [ -n "$ucd" ]
  then
    echo $ucd > $ucdpath/unbind
  fi
fi

# Raise AVDD +100mV
i2cset -y 11 0x64 0x00 0x09 i # set PAGE
i2cset -y 11 0x64 0xF5 0x81 i # set margin_config
i2cset -y 11 0x64 0x21 0x85 0x33 i # set VOUT_COMMAND

# Increase over-current settings
#VDD A phase current
i2cset -y 4 0x12 0xFF 0x04 b    # set window register high byte to 4
i2cset -y 4 0x12 0x3C 0xFF b    # Disable
#VDD B phase current
i2cset -y 5 0x12 0xFF 0x04 b    # set window register high byte to 4
i2cset -y 5 0x12 0x3C 0xFF b    # Disable
#VDD A master OC fault to 445A
i2cset -y 4 0x70 0x00 0x00 b    # PAGE
i2cset -y 4 0x70 0x46 0x08DE w
# VDD A master OC warn to 384A
i2cset -y 4 0x70 0x4A 0x08C0 w
#VDD B master OC fault to 445A
i2cset -y 5 0x70 0x00 0x00 b    # PAGE
i2cset -y 5 0x70 0x46 0x08DE w
# VDD B master OC warn to 384A
i2cset -y 5 0x70 0x4A 0x08C0 w

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
