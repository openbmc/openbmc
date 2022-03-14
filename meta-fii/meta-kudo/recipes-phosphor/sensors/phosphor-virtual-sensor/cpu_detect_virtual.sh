#!/bin/bash

# Provide source directive to shellcheck.
# shellcheck source=meta-fii/meta-kudo/recipes-kudo/kudo-fw-utility/kudo-fw/kudo-lib.sh
source /usr/libexec/kudo-fw/kudo-lib.sh

CPU1_STATUS_N=$(get_gpio_ctrl "$GPIO_CPU1_STATUS_N")
sleep 60
sensor_name=$(busctl get-property xyz.openbmc_project.EntityManager /xyz/openbmc_project/inventory/system/chassis/NBM4G/CPU0_NBM4G_TMP_V xyz.openbmc_project.Configuration.ADC Name | awk '{print $2}')
sensor="\"CPU0_NBM4G_TMP_V\""

#4G sensor is found only in 1 CPU systems, so enough to check 4G for 1p alone
if [[ "$sensor_name" == "$sensor" ]]; then
    cp -f /etc/virtual-sensor/configurations/virtual_sensor_config1p4G.json /usr/share/phosphor-virtual-sensor/virtual_sensor_config.json
else
    if [[ $CPU1_STATUS_N == 1 ]]; then
        cp -f /etc/virtual-sensor/configurations/virtual_sensor_config1p2G.json /usr/share/phosphor-virtual-sensor/virtual_sensor_config.json
    else
        cp -f /etc/virtual-sensor/configurations/virtual_sensor_config2p2G.json /usr/share/phosphor-virtual-sensor/virtual_sensor_config.json
    fi
fi
