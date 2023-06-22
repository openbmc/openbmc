#!/bin/bash

# Disable check for usage of the definitions within mori-lib.sh
#shellcheck disable=SC2034

# get_gpio_num
# Dynamically obtains GPIO number from chip base and I2C expanders
# line-name
function get_gpio_num() {
    #shellcheck disable=SC2207
    CHIP_PIN=($(gpiofind "$1" | awk '{print substr ($1, 9 ), $2 }'))
    #shellcheck disable=SC2128
    if [ -z "$CHIP_PIN" ]; then
        echo "Could not find GPIO with name: $1"
        return 1
    fi

    if [ "${CHIP_PIN[0]}" -gt 7 ]; then
        BUS_ADDR=$(gpiodetect | grep gpiochip"${CHIP_PIN[0]}" | \
                   grep -o '\[.*]' | tr -d ' \[\]')
        GPIO_BASE_DIR=$(cd /sys/bus/i2c/devices/"$BUS_ADDR"/gpio/ || \
                        exit; ls -1 -v)
        # Check that there is a single gpiobank per i2c device
        GPIO_BANKS=$(cd /sys/bus/i2c/devices/"$BUS_ADDR"/ || \
                     exit ;  ls -1 -d -v gpiochip*)
        # Determine which GPIO_BASE to use based on the place of the GPIO_BANK
        # in comparision to GPIO_BANKS
        # gpiochip# is set in reverse order of numbering for location of
        # GPIO_BASE_DIR
        count=$(echo "$GPIO_BANKS" | wc -w)
        for X in ${GPIO_BANKS}
        do
            if [[ $(gpiofind "$1" | cut -d " " -f 1) == "$X" ]]; then
                # Used to select the correct GPIO_BASE value
                #shellcheck disable=SC2086
                GPIO_BASE_DIR=("$(echo ${GPIO_BASE_DIR} | cut -d " " -f $count)")
                break
            fi
            count=$((count-1))
        done
        tmp="/sys/bus/i2c/devices/$BUS_ADDR/gpio/${GPIO_BASE_DIR[0]}/base"
        GPIO_BASE=$(cat "$tmp")
        echo "$((GPIO_BASE+CHIP_PIN[1]))"
    else
        echo "$((CHIP_PIN[0]*32+CHIP_PIN[1]))"
    fi
}

# set_gpio_ctrl
# line-name, high(1)/low(0)
function set_gpio_ctrl() {
    #shellcheck disable=SC2046
    gpioset $(gpiofind "$1")="$2"
}

# get_gpio_ctrl
# line-name
function get_gpio_ctrl() {
    GPIO_NUM=$(get_gpio_num "$1")
    echo "$GPIO_NUM" > /sys/class/gpio/export
    # GPIOs added by drivers use different path for value most but not all
    # drivers follow this trend
    # Try reading like traditional GPIO, if fails, try reading in driver format
    if ! cat /sys/class/gpio/gpio"$GPIO_NUM"/value 2> /dev/null ; then
        cat /sys/class/gpio/"$1"/value
    fi
    echo "$GPIO_NUM" > /sys/class/gpio/unexport
}


function Does_File_Exist() {
  local FILE=$1

  if [[ ! -f "${FILE}" ]]; then
    echo "${FILE} file does not exist" >&2
    return 1
  fi
}

function cleanup_bios_access() {
  # Run only if setup_bios_access was previously ran
  if Does_File_Exist /run/bios_access &> /dev/null ; then
    # switch the SPI mux from BMC to Host
    if [ -d "${KERNEL_SYSFS_FIU}/${KERNEL_FIU_ID}" ]; then
      echo "${KERNEL_FIU_ID}" > "${KERNEL_SYSFS_FIU}"/unbind
    fi
    set_gpio_ctrl FM_BIOS_FLASH_SPI_MUX_R_SEL 0

    # Indicate to host that BMC is finished accessing SPI
    set_gpio_ctrl S0_BMC_SPI_NOR_ACCESS 0

    rm /run/bios_access
  fi
}

function setup_bios_access() {
  # Run only if setup_bios_access was not previously ran without cleanup
  if ! Does_File_Exist /run/bios_access &> /dev/null ; then
    echo "BMC is accessing BIOS" > /run/bios_access

    # rescan the spi bus
    if [ -d "${KERNEL_SYSFS_FIU}/${KERNEL_FIU_ID}" ]; then
      echo "${KERNEL_FIU_ID}" > "${KERNEL_SYSFS_FIU}"/unbind
      usleep 100
    fi

    # Wait until the host is finished accessing the SPI
    while [[ $(get_gpio_ctrl S0_SOC_SPI_NOR_ACCESS) == 1 ]]
    do
      sleep 1
    done
    # Indicate to host that BMC is accessing SPI
    set_gpio_ctrl S0_BMC_SPI_NOR_ACCESS 1

    # switch the SPI mux from Host to BMC
    set_gpio_ctrl FM_BIOS_FLASH_SPI_MUX_R_SEL 1

    echo "${KERNEL_FIU_ID}" > "${KERNEL_SYSFS_FIU}"/bind
  fi
}

# Start definitions

# I2C Definitions
# The array is (<bus> <address>), where address is in hexadecimal.
I2C_BMC_CPLD=(13 76)
I2C_MB_CPLD=(0 76)
I2C_FANCTRL=(35 2c)
I2C_BMC_PWRSEQ=(48 59)
I2C_MB_PWRSEQ=(40 40)
I2C_CPU_EEPROM=(19 50)

# File Path Definition
# File path used to prevent hotswapping
RST_LOCK_FILE="/etc/FW_FLASH_ONGOING"

# Device name and driver path used for BIOS SPI
KERNEL_FIU_ID="c0000000.spi"
KERNEL_SYSFS_FIU="/sys/bus/platform/drivers/NPCM-FIU"
