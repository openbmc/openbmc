#!/bin/bash

set -e
log_file="/var/log/program_edid.log"

if [ ! -f $1 ]; then
    echo "$1 does not exist." >> $log_file
    exit 0
fi

if [ ! -f $2 ]; then
    echo "$2 does not exist." >> $log_file
    exit 0
fi

echo "Programmning $1 to $2" >> $log_file
cat $1 > $2

crc_file=$(cat $1 | crc32)
fsiz=$(stat -c %s $1)
crc_eeprom=$(head -c $fsiz $2 | crc32)

if [[ $crc_file == $crc_eeprom ]]; then
  echo "Program EDID success" >> $log_file
  exit 1
else
  echo "Program EDID failed" >> $log_file
  exit 0
fi

exit 1