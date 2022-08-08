#!/bin/bash

# Provide source directive to shellcheck.
# shellcheck source=meta-fii/meta-mori/recipes-mori/mori-fw-utility/mori-fw/mori-lib.sh
# Disable check for globbing and word splitting within double quotes
# shellcheck disable=SC2086
source /usr/libexec/mori-fw/mori-lib.sh

function fwbios() {
  KERNEL_FIU_ID="c0000000.spi"
  KERNEL_SYSFS_FIU="/sys/bus/platform/drivers/NPCM-FIU"

  # switch the SPI mux from Host to BMC
  set_gpio_ctrl FM_BIOS_FLASH_SPI_MUX_R_SEL 1

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
  set_gpio_ctrl FM_BIOS_FLASH_SPI_MUX_R_SEL 0

  return 0
}

function fwbmccpld() {
  # MB_JTAG_MUX 0:CPU 1:MB
  # BMC_JTAG_MUX 0:GF/MB 1:BMC
  set_gpio_ctrl MB_JTAG_MUX_SEL 0
  set_gpio_ctrl BMC_JTAG_MUX_SEL  1
  if [ "$(loadsvf -d /dev/jtag0 -s $1 -m 0)" -ne  0 ]; then
    echo "BMC CPLD update failed" >&2
    return 1
  fi
  wait

  return 0
}

function fwmbcpld() {
  # MB_JTAG_MUX 0:CPU 1:MB
  # BMC_JTAG_MUX 0:GF/MB 1:BMC
  set_gpio_ctrl MB_JTAG_MUX_SEL 1
  set_gpio_ctrl BMC_JTAG_MUX_SEL  0
  if [ "$(loadsvf -d /dev/jtag0 -s $1 -m 0)" -ne  0 ]; then
    echo "Mobo CPLD update failed" >&2
    return 1
  fi
  wait
  set_gpio_ctrl MB_JTAG_MUX_SEL 0

  return 0
}

function fwbootstrap() {
  # to flash the CPU EEPROM
  #unbind bootstrap EEPROM
  echo ${I2C_CPU_EEPROM[0]}-00${I2C_CPU_EEPROM[1]} > /sys/bus/i2c/drivers/at24/unbind

  #switch access to BMC
  set_gpio_ctrl CPU_EEPROM_SEL 0

  if [ "$(ampere_eeprom_prog -b ${I2C_CPU_EEPROM[0]} -s 0x${I2C_CPU_EEPROM[1]} -p -f $1)" -ne  0 ]; then
    echo "CPU bootstrap EEPROM update failed" >&2
    return 1
  fi
  wait

  #bind bootstrap EEPROM
  echo ${I2C_CPU_EEPROM[0]}-00${I2C_CPU_EEPROM[1]} > /sys/bus/i2c/drivers/at24/bind
 
  #switch back access to CPU
  set_gpio_ctrl CPU_EEPROM_SEL 1
  return 0

}

function fwmb_pwr_seq(){
  #$1 PS seq config file
  if [[ ! -e "$1" ]]; then
    echo "The file $1 does not exist"
    return 1
  fi
  echo "${I2C_MB_PWRSEQ[0]}"-00"${I2C_MB_PWRSEQ[1]}" > /sys/bus/i2c/drivers/adm1266/unbind
  #Parameters passed to adm1266_fw_fx to be used to flash PS
  #1st I2C bus number of PS's
  #2nd PS seq config file
  if [ "$(adm1266_fw_fx ${I2C_MB_PWRSEQ[0]} $1)" -ne  0 ]; then

    echo "The power seq flash failed" >&2
    return 1
  fi
  echo "${I2C_MB_PWRSEQ[0]}"-00"${I2C_MB_PWRSEQ[1]}" > /sys/bus/i2c/drivers/adm1266/bind

  return 0
}

if [[ ! $(which flashcp) ]]; then
    echo "flashcp utility not installed"
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
  bootstrap)
    fwbootstrap "$2"
    ;;
  mbseq)
    fwmb_pwr_seq "$2"
    ;;
  *)
    ;;
esac
ret=$?

rm -f "$2"

exit $ret
