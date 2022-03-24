#!/bin/bash
#Prototype CPU detection script

# Provide source directive to shellcheck.
# shellcheck source=meta-fii/meta-kudo/recipes-kudo/kudo-fw-utility/kudo-fw/kudo-lib.sh
source /usr/libexec/kudo-fw/kudo-lib.sh

CPU1_STATUS_N=$(get_gpio_ctrl S1_STATUS_N)
sleep 60
if [[ $CPU1_STATUS_N == 1 ]]; then
    cp -f /etc/virtual-sensor/configurations/virtual_sensor_config1p.json /usr/share/phosphor-virtual-sensor/virtual_sensor_config.json
        else
    cp -f /etc/virtual-sensor/configurations/virtual_sensor_config2p.json /usr/share/phosphor-virtual-sensor/virtual_sensor_config.json
        fi
