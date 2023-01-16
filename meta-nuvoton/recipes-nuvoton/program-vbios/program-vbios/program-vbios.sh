#!/bin/bash
set -e
log_file="/var/log/program_vbios.log"
/sbin/modprobe spi-gpio
sleep 1

function togspi() {
  reg=$(devmem 0xf0800260)
  reg=$(($reg|0x01000000))
  devmem 0xf0800260 32 $reg
}

crc_file=$(cat $1 | crc32)
fsiz=$(stat -c %s $1)
crc_eeprom=$(head -c $fsiz $2 | crc32)

if [[ $crc_file == $crc_eeprom ]]; then
  echo "The VGA FW already programmed" >> $log_file
  /sbin/modprobe -r spi-gpio
  sleep 0.5
  togspi
  exit 0
fi

if [ -f $1 -a -f $2 ]; then
	dd if=$1 of=$2 bs=1K count=$3  > $log_file 2>&1
	echo "Program vbios success" >> $log_file
	/sbin/modprobe -r spi-gpio
        togspi
	exit 0
fi

echo "Failure!! vbios=$1 target=$2 size=$3K" > $log_file
togspi
exit 1
