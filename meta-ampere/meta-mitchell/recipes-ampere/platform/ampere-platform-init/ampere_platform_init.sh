#!/bin/bash

# shellcheck disable=SC2046
# shellcheck source=meta-ampere/meta-mitchell/recipes-ampere/platform/ampere-platform-init/mtmitchell_platform_gpios_init.sh
# shellcheck source=meta-ampere/meta-common/recipes-ampere/platform/ampere-utils/utils-lib.sh
source /usr/sbin/platform_gpios_init.sh
source /usr/sbin/utils-lib.sh

function socket-based-fan-conf-update() {
    echo "Checking phosphor-fan configurations based on CPU 1 presence..."
    fanControlConfDir="/usr/share/phosphor-fan-presence/control/com.ampere.Hardware.Chassis.Model.MtMitchell"
    targetConf=groups.json

    if [[ "$(sx_present 1)" == "0" ]]; then
        refConf=groups_2p.json
        echo "CPU 1 is present"
        echo "Using fan configs for 2P at $fanControlConfDir/$refConf"
    else
        refConf=groups_1p.json
        echo "CPU 1 is NOT present"
        echo "Using fan configs for 1P at $fanControlConfDir/$refConf"
    fi

    if [ -f  "$fanControlConfDir/$refConf" ]; then
        cp "$fanControlConfDir/$refConf" "$fanControlConfDir/$targetConf"
    else
        echo "The reference fan config $fanControlConfDir/$refConf does not exist!!"
    fi
}

function mtc_board_revision_detection() {
    # Support to detect MTC board revisions via board ID and to set GPI pins for the host
    # to identify the board revision.

    # Check the mainboard by board_id (i2c0, address 0x20)
    board_id=$(i2cget -y -a 0 0x20 0x0 b)
    if [ "$?" == '1' ]; then
        echo "Failed to read board_id from i2c"
    fi

    # BIT[7:6:5:4]
    # 0000 : EVT1
    # 0001 : EVT2
    # 0010 : EVT3
    # 0011 :
    # 0100 : DVT1
    # 0101 : DVT2
    # 0110 : DVT3
    # 0111 :
    # 1000 : PVT1
    # 1001 : PVT2
    # 1010 : PVT3
    # 1011 :

    md_id_7_6_5_4=$(( (board_id & 0xF0)>>4 ))

    # P0[7] -> GPI[1] and P0[6] -> GPI[0]
    # P[7:6] = 2'b01 for Mitchell 2.0 (PVT2)
    # P[7:6] = 2'b00 for Mitchell 1.0 (EVTx, DVTx, PVT1 )
    if [[ $md_id_7_6_5_4 -gt 8 ]]; then
        # Board is MTC2.0
        echo "Update GPI1 to low and GPI0 to high"
        gpioset $(gpiofind gpi1)=0
        gpioset $(gpiofind gpi0)=1
    else
        # Board is MTC1.0
        echo "Update GPI1 to low and GPI0 to low"
        gpioset $(gpiofind gpi1)=0
        gpioset $(gpiofind gpi0)=0
    fi
}

#pre platform init function. implemented in platform_gpios_init.sh
pre-platform-init

# =======================================================
# Setting default value for device sel and mux
bootstatus=$(cat /sys/class/watchdog/watchdog0/bootstatus)
if [ "$bootstatus" == '32' ]; then
    echo "CONFIGURE: gpio pins to output high after AC power"
    for gpioName in "${output_high_gpios_in_ac[@]}"; do
        gpioset $(gpiofind "$gpioName")=1
    done
    echo "CONFIGURE: gpio pins to output low after AC power"
    for gpioName in "${output_low_gpios_in_ac[@]}"; do
        gpioset $(gpiofind "$gpioName")=0
    done
fi

# =======================================================
# Setting default value for others gpio pins
echo "CONFIGURE: gpio pins to output high"
for gpioName in "${output_high_gpios_in_bmc_reboot[@]}"; do
    gpioset $(gpiofind "$gpioName")=1
done
echo "CONFIGURE: gpio pins to output low"
for gpioName in "${output_low_gpios_in_bmc_reboot[@]}"; do
    gpioset $(gpiofind "$gpioName")=0
done

socket-based-fan-conf-update
mtc_board_revision_detection

#post platform init function. implemented in platform_gpios_init.sh
post-platform-init

exit 0
