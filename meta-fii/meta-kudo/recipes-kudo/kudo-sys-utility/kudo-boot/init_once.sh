#!/bin/bash

# Provide source directive to shellcheck.
# shellcheck source=meta-fii/meta-kudo/recipes-kudo/kudo-fw-utility/kudo-fw/kudo-lib.sh
source /usr/libexec/kudo-fw/kudo-lib.sh

function set_mux_default(){
    # set all mux route to CPU before power on host
    # BMC_CPU_RTC_I2C_SEL
    set_gpio_ctrl CPU_RTC_SEL 1
    # BMC_CPU_DDR_I2C_SEL
    set_gpio_ctrl CPU_DDR_SEL 1
    # BMC_CPU_EEPROM_I2C_SEL
    set_gpio_ctrl CPU_EEPROM_SEL 1
    # BMC_CPU_PMBUS_SEL
    set_gpio_ctrl CPU_VRD_SEL 1
    # LED control
    # LED_BMC_LIVE
    set_gpio_ctrl LED_BMC_ALIVE 1

    # SPI control
    # Send command to CPLD to switch the bios spi interface to host
    i2cset -y -f -a "${I2C_BMC_CPLD[0]}" 0x"${I2C_BMC_CPLD[1]}" 0x10 0x00

}

# 0 - 63 EVT
# 64 + DVT/PVT
boardver=$(printf '%d' "$(awk '{print $6}' /sys/bus/i2c/drivers/fiicpld/"${I2C_MB_CPLD[0]}"-00"${I2C_MB_CPLD[1]}"/CMD00)")

# On EVT machines, the secondary SCP EEPROM is used.
# Set BMC_I2C_BACKUP_SEL to secondary.
if [[ $boardver -lt $BOARDVER_EVT_LAST ]]; then
    echo "EVT system. Choosing secondary SCP EEPROM."
    set_gpio_ctrl BACKUP_SCP_SEL 0
    set_mux_default
    # Power control
    # S0_BMC_OK
    set_gpio_ctrl S0_BMC_OK 1
else
    echo "DVT or PVT system"
    # sleep so that FRU and all ipmitool Devices are ready before HOST OS
    # HPM_STBY_RST_N to DC-SCM spec
    set_gpio_ctrl HPM_STBY_RST_N 1     # on DVT this became HPM_STBY_RST_N (EVT1 came from CPLD)
    sleep 5 # for the MUX to get ready
    set_mux_default
    # Power control
    # S0_BMC_OK
    set_gpio_ctrl S0_BMC_OK 1
fi

# Disable CPU 1 CLK when cpu not detected
# echo init_once cpu $CPU1_STATUS > /dev/ttyS0
# echo init_once board $boardver > /dev/ttyS0
CPU1_STATUS_N=$(get_gpio_ctrl S1_STATUS_N)
if [[ $CPU1_STATUS_N == 1 ]]; then
    #Execute this only on DVT systems
    if [[ $boardver -lt $BOARDVER_EVT_LAST ]]; then
        echo EVT system "$boardver"
    else
        echo DVT system "$boardver"
        i2cset -y -a -f "${I2C_S1_CLKGEN[0]}" 0x"${I2C_S1_CLKGEN[1]}" 0x05 0x03
    fi
    #These i2c deviecs are already installed on EVT systems
    i2cset -y -a -f "${I2C_S1_PCIE_CLKGEN1[0]}" 0x"${I2C_S1_PCIE_CLKGEN1[1]}" 0 1 0xdf i
    i2cset -y -a -f "${I2C_S1_PCIE_CLKGEN1[0]}" 0x"${I2C_S1_PCIE_CLKGEN1[1]}" 11 1 0x01 i
    i2cset -y -a -f "${I2C_S1_PCIE_CLKGEN2[0]}" 0x"${I2C_S1_PCIE_CLKGEN2[1]}" 1 2 0x3f 0x0c i
fi

# Create /run/openbmc for system power files
mkdir "/run/openbmc"

# Restart psusensor service to enusre that the VBAT sensor doesn't say "no reading" until
# it's second query after a hotswap
(sleep 45; systemctl restart xyz.openbmc_project.psusensor.service)&
