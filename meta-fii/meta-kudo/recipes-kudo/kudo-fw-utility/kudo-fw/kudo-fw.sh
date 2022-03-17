#!/bin/bash

# Provide source directive to shellcheck.
# shellcheck source=meta-fii/meta-kudo/recipes-kudo/kudo-fw-utility/kudo-fw/kudo-lib.sh
# Disable check for globbing and word splitting within double quotes
# shellcheck disable=SC2086
source /usr/libexec/kudo-fw/kudo-lib.sh

devpath="/sys/bus/i2c/devices/13-0077/driver"

function fwbios() {
  KERNEL_FIU_ID="c0000000.spi"
  KERNEL_SYSFS_FIU="/sys/bus/platform/drivers/NPCM-FIU"

  # switch the SPI mux from Host to BMC
  i2cset -y -f -a 13 0x76 0x10 0x01

  # rescan the spi bus
  if [ -d "${KERNEL_SYSFS_FIU}/${KERNEL_FIU_ID}" ]; then
    echo "${KERNEL_FIU_ID}" > "${KERNEL_SYSFS_FIU}"/unbind
    sleep 1
  fi
  echo "${KERNEL_FIU_ID}" > "${KERNEL_SYSFS_FIU}"/bind

  # write to the mtd device
  BIOS_MTD=$(grep "bios" /proc/mtd | sed -n 's/^\(.*\):.*/\1/p')

  if [ ! -f "$1" ]; then
    echo " Cannot find the" "$1" "image file"
    return 1

  fi
  echo "Flashing BIOS @/dev/${BIOS_MTD}"
  if [ "$(flashcp -v $1 /dev/${BIOS_MTD})" -ne  0 ]; then
    echo "Flashing the bios failed " >&2
    return 1
  fi
  wait

  # switch the SPI mux from BMC to Host
  if [ -d "${KERNEL_SYSFS_FIU}/${KERNEL_FIU_ID}" ]; then
    echo "${KERNEL_FIU_ID}" > "${KERNEL_SYSFS_FIU}"/unbind
  fi
  i2cset -y -f -a 13 0x76 0x10 0x00

  # Disable LPI mode NV_SI_CPU_LPI_FREQ_DISABLE for SCP 1.06 and older.
  if [ "$(nvparm -s 0x1 -o 0x114090)" -ne  0 ]; then
    echo "Setting LPI mode for SCP 1.06 and older failed " >&2
    return 1
  fi

  # Disable LPI mode NV_SI_CPU_LPI_FREQ_DISABLE for SCP 1.07 and newer
  if [ "$(nvparm -s 0x1 -o 0x02A8)" -ne  0 ]; then
    echo "Setting LPI mode for SCP 1.07 and newer failed " >&2
    return 1
  fi

  # Disable toggling of SMPro heartbeat
  if [ "$(nvparm -s 0x0 -o 0x5F0638)" -ne  0 ]; then
    echo "Setting SMpro heartbeat failed " >&2
    return 1
  fi

  if [[ $(find "$1" -type f -size +17156k 2>/dev/null) ]]; then
    echo "Extracting the SCP from the image"
    dd if="$1" bs=1024 skip=17156 count=256 of=/run/initramfs/myscp.img
    # Update both primary and backup EEPROM
    fwscp /run/initramfs/myscp.img
    fwscpback /run/initramfs/myscp.img
  fi


  return 0
}

function fwbmccpld() {
  # BMC_JTAG_MUX_1 #218 0:BMC 1:MB
  set_gpio_ctrl 218 out 0
  if [ "$(loadsvf -d /dev/jtag0 -s $1 -m 0)" -ne  0 ]; then
    echo "BMC CPLD update failed" >&2
    return 1
  fi
  wait
  set_gpio_ctrl 218 out 1

  return 0
}

function fwmbcpld() {
  # BMC_JTAG_MUX_1 #218 0:BMC 1:MB
  # BMC_JTAG_SEL #164 0:BMC 1:CPU
  set_gpio_ctrl 218 out 1
  set_gpio_ctrl 164 out 1
  if [ "$(loadsvf -d /dev/jtag0 -s $1 -m 0)" -ne  0 ]; then
    echo "Mobo CPLD update failed" >&2
    return 1
  fi
  wait

  return 0
}

function fwscp() {
  # BMC_I2C_BACKUP_SEL #168 0:failover, 1:main
  # BMC_CPU_EEPROM_I2C_SEL #85 0:BMC, 1:CPU
  scp_eeprom_sel=$(get_gpio_ctrl 168)
  set_gpio_ctrl 168 out 1
  set_gpio_ctrl 85 out 0
  #shellcheck disable=SC2010
  I2C_BUS_DEV=$(ls -l $devpath/"13-0077/" | grep channel-0 | awk '{ print $11}' | cut -c 8-)
  if [ "$(ampere_eeprom_prog -b $I2C_BUS_DEV -s 0x50 -p -f $1)" -ne  0 ]; then
    echo "SCP eeprom update failed" >&2
    return 1
  fi
  wait
  set_gpio_ctrl 85 out 1
  set_gpio_ctrl 168 out "$scp_eeprom_sel"

  return 0
}

function fwscpback() {
  # BMC_I2C_BACKUP_SEL #168 0:failover, 1:main
  # BMC_CPU_EEPROM_I2C_SEL #85 0:BMC, 1:CPU
  scp_eeprom_sel=$(get_gpio_ctrl 168)
  set_gpio_ctrl 168 out 0
  set_gpio_ctrl 85 out 0
  #shellcheck disable=SC2010
  I2C_BUS_DEV=$(ls -l $devpath/"13-0077/" | grep channel-0 | awk '{ print $11}' | cut -c 8-)
  if [ "$(ampere_eeprom_prog -b $I2C_BUS_DEV -s 0x50 -p -f $1)" -ne  0 ]; then
    echo "SCP BACKUP eeprom update failed" >&2
    return 1
  fi
  wait
  set_gpio_ctrl 85 out 1
  set_gpio_ctrl 168 out "$scp_eeprom_sel"

  return 0
}

function fwmb_pwr_seq(){
  #$1 0x40 seq config file
  #$2 0x41 seq config file
  if [[ ! -e "$1" ]]; then
    echo "The file $1 does not exist"
    return 1
  fi
  if [[ ! -e "$2" ]]; then
    echo "The file $2 file does not exist"
    return 1
  fi
  echo 32-0040 > /sys/bus/i2c/drivers/adm1266/unbind
  echo 32-0041 > /sys/bus/i2c/drivers/adm1266/unbind
  if [ "$(adm1266_fw_fx $1 $2)" -ne  0 ]; then
    echo "The power seq flash failed" >&2
    return 1
  fi
  echo 32-0040 > /sys/bus/i2c/drivers/adm1266/bind
  echo 32-0041 > /sys/bus/i2c/drivers/adm1266/bind

  return 0
}

if [[ ! $(which flashcp) ]]; then
    echo "flashcp utility not installed"
    exit 1
fi
if [[ ! $(which ampere_eeprom_prog) ]]; then
    echo "ampere_eeprom_prog utility not installed"
    exit 1
fi
if [[ ! $(which loadsvf) ]]; then
    echo "loadsvf utility not installed"
    exit 1
fi
if [[ ! -e /dev/jtag0 ]]; then
    echo "Jtag device driver not functional"
    exit 1
fi

case $1 in
  bios)
    fwbios "$2"
    ;;
  bmccpld)
    fwbmccpld "$2"
    ;;
  mbcpld)
    fwmbcpld "$2"
    ;;
  scp)
    fwscp "$2"
    ;;
  scpback)
    fwscpback "$2"
    ;;
  mbseq)
    fwmb_pwr_seq "$2" "$3"
    ;;
  *)
    ;;
esac
ret=$?

rm -f "$2" "$3"

exit $ret
