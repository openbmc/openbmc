#!/bin/bash

# Provide source directive to shellcheck.
# shellcheck source=meta-fii/meta-mori/recipes-mori/mori-fw-utility/mori-fw/mori-lib.sh
source /usr/libexec/mori-fw/mori-lib.sh

function set_mux_default(){
    # set all mux route to CPU before power on host
    # BMC_CPU_DDR_I2C_SEL
    set_gpio_ctrl CPU_DDR_SEL 1
    # BMC_CPU_EEPROM_I2C_SEL
    set_gpio_ctrl CPU_EEPROM_SEL 1
    # BMC_CPU_PMBUS_SEL
    set_gpio_ctrl CPU_VRD_SEL 1
    # LED control
    # LED_BMC_LIVE
    set_gpio_ctrl LED_BMC_ALIVE 1
}

set_mux_default
set_gpio_ctrl HPM_STBY_RST_N 1
sleep 5
set_gpio_ctrl S0_BMC_OK 1

# Create /run/openbmc for system power files
if [[ ! -d "/run/openbmc" ]]; then
  mkdir "/run/openbmc"
fi

echo "BMC initialization complete"
