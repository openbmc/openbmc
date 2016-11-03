#!/bin/sh

vcs_on()
{
  # enable VCS rail with OPERATION
  # A side
  i2cset -y 4 0x70 0x00 0x01 b
  i2cset -y 4 0x70 0x02 0x1A b #respond to OPERATION
  i2cset -y 4 0x70 0x00 0x00 b
  # B side
  i2cset -y 5 0x70 0x00 0x01 b
  i2cset -y 5 0x70 0x02 0x1A b #respond to OPERATION
  i2cset -y 5 0x70 0x00 0x00 b
}

vcs_off()
{
  # use these commands to properly disable VCS before powering on
  # A side
  i2cset -y 4 0x70 0x00 0x01 b
  i2cset -y 4 0x70 0x02 0x16 b #respond to OPERATION
  i2cset -y 4 0x70 0x00 0x00 b
  # B side
  i2cset -y 5 0x70 0x00 0x01 b
  i2cset -y 5 0x70 0x02 0x16 b #respond to OPERATION
  i2cset -y 5 0x70 0x00 0x00 b                      
}

gpioutil -p D0 -d out -v 1      # enable fsi link

echo Disable VCS
vcs_off
echo -n  powering on....
gpioutil -p D1 -d out -v 1      # on
sleep 1
pgood=`gpioutil -p D2 -d in`
if [ $pgood -eq 1 ]
then
  echo ....OK. Now run putcfam commands.
  putcfam pu 2810 15 1 0       # Unfence PLL controls
  putcfam pu 281A  1 1 f       # Assert Perv chiplet endpoint reset, just in case
  putcfam pu 281A 31 1 f       # Enable Nest PLL
  sleep 1
  echo VCS on
  vcs_on
  sleep 1
  echo Start IPL
  putcfam pu 2801 0 1 1 -ib
else
  echo ....FAILED
fi
echo
