#!/bin/bash

# Provide source directive to shellcheck.
# shellcheck source=meta-fii/meta-mori/recipes-mori/mori-fw-utility/mori-fw/mori-lib.sh
# Disable check for globbing and word splitting within double quotes
# shellcheck disable=SC2086
source /usr/libexec/mori-fw/mori-lib.sh

function fwbios() {
  KERNEL_FIU_ID="c0000000.spi"
  KERNEL_SYSFS_FIU="/sys/bus/platform/drivers/NPCM-FIU"
  ret=0
  if [ ! -f "$1" ]; then
    echo " Cannot find the" "$1" "image file"
    return 1
  fi

  # switch the SPI mux from Host to BMC
  set_gpio_ctrl FM_BIOS_FLASH_SPI_MUX_R_SEL 1

  # rescan the spi bus
  if [ -d "${KERNEL_SYSFS_FIU}/${KERNEL_FIU_ID}" ]; then
    echo "${KERNEL_FIU_ID}" > "${KERNEL_SYSFS_FIU}"/unbind
    sleep 1
  fi
  echo "${KERNEL_FIU_ID}" > "${KERNEL_SYSFS_FIU}"/bind

  # write to the mtd device
  BIOS_MTD=$(grep "hnor" /proc/mtd | sed -n 's/^\(.*\):.*/\1/p')

  echo "Flashing BIOS @/dev/${BIOS_MTD}"
  if ! flashcp -v $1 /dev/${BIOS_MTD} ; then
    echo "Flashing the bios failed " >&2
    ret=1
  fi

  # switch the SPI mux from BMC to Host
  if [ -d "${KERNEL_SYSFS_FIU}/${KERNEL_FIU_ID}" ]; then
    echo "${KERNEL_FIU_ID}" > "${KERNEL_SYSFS_FIU}"/unbind
  fi
  set_gpio_ctrl FM_BIOS_FLASH_SPI_MUX_R_SEL 0

  return $ret
}

function fwbmccpld() {
  if [ ! -s "$1" ]; then
    echo "Image file" "$1" "is empty or nonexistent, BMC CPLD update failed"
    return 1
  fi

  # MB_JTAG_MUX 0:CPU 1:MB
  # BMC_JTAG_MUX 0:GF/MB 1:BMC
  set_gpio_ctrl MB_JTAG_MUX_SEL 0
  set_gpio_ctrl BMC_JTAG_MUX_SEL  1

  # 1st condition checks if the svf file is valid
  # 2nd condition checks flashing logs for flash errors
  if ! mesg=$(loadsvf -d /dev/jtag0 -s $1 -m 0 2>&1) \
        || echo "$mesg" | grep -i -e error -e fail ; then
    echo "BMC CPLD update failed"
    return 1
  fi
  echo "BMC CPLD update successful"
  return 0
}

function fwmbcpld() {
  if [ ! -s "$1" ]; then
    echo "Image file" "$1" "is empty or nonexistent, MB CPLD update failed"
    return 1
  fi

  # MB_JTAG_MUX 0:CPU 1:MB
  # BMC_JTAG_MUX 0:GF/MB 1:BMC
  set_gpio_ctrl MB_JTAG_MUX_SEL 1
  set_gpio_ctrl BMC_JTAG_MUX_SEL  0

  # 1st condition checks if the svf file is valid
  # 2nd condition checks flashing logs for flash errors
  if ! mesg=$(loadsvf -d /dev/jtag0 -s $1 -m 0 2>&1) \
        || echo "$mesg" | grep -i -e error -e fail ; then
    echo "MB CPLD update failed"
    return 1
  fi
  set_gpio_ctrl MB_JTAG_MUX_SEL 0
  echo "MB CPLD update successful"

  return 0
}

function fwbootstrap() {
  # to flash the CPU EEPROM
  #unbind bootstrap EEPROM
  echo ${I2C_CPU_EEPROM[0]}-00${I2C_CPU_EEPROM[1]} > /sys/bus/i2c/drivers/at24/unbind

  #switch access to BMC
  set_gpio_ctrl CPU_EEPROM_SEL 0

  if [ "$(bootstrap_flash -b ${I2C_CPU_EEPROM[0]} -s 0x${I2C_CPU_EEPROM[1]} -p -f $1)" -ne  0 ]; then
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
  echo "${I2C_MB_PWRSEQ[0]}"-00"${I2C_MB_PWRSEQ[1]}" > \
    /sys/bus/i2c/drivers/adm1266/unbind
  #Parameters passed to mb_power_sequencer_flash to be used to flash PS
  #1st I2C bus number of PS's
  #2nd PS seq config file
  if [ "$(mb_power_sequencer_flash ${I2C_MB_PWRSEQ[0]} $1)" -ne  0 ]; then

    echo "The power seq flash failed" >&2
    return 1
  fi
  echo "${I2C_MB_PWRSEQ[0]}"-00"${I2C_MB_PWRSEQ[1]}" > \
    /sys/bus/i2c/drivers/adm1266/bind

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
if [[ ! $(which mb_power_sequencer_flash) ]]; then
    echo "mb_power_sequencer_flash utility not installed"
    exit 1
fi
if [[ ! $(which bootstrap_flash) ]]; then
    echo "bootstrap_flash utility not installed"
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
