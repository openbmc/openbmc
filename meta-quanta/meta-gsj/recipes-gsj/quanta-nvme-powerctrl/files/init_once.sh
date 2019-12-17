#!/bin/bash

source /usr/libexec/nvme_powerctrl_library.sh

function set_gpio() {
  #$1 gpio pin
  echo $1 > /sys/class/gpio/export
}

echo "Read Clock Gen Value is: $CLOCK_GEN_VALUE"

## Initial U2_PRESENT_N
for i in ${!U2_PRESENT[@]};
do
    set_gpio ${U2_PRESENT[$i]};
    set_gpio_direction ${U2_PRESENT[$i]} 'in';
    echo "Read $i SSD present: $(read_gpio_input ${U2_PRESENT[$i]})"
done

## Initial POWER_U2_EN
for i in ${!POWER_U2[@]};
do
    set_gpio ${POWER_U2[$i]};
done

## Initial PWRGD_U2
for i in ${!PWRGD_U2[@]};
do
    set_gpio ${PWRGD_U2[$i]};
    set_gpio_direction ${PWRGD_U2[$i]} 'in';
    echo "Read $i SSD Power Good: $(read_gpio_input ${PWRGD_U2[$i]})"
done

## Initial RST_BMC_U2
for i in ${!RST_BMC_U2[@]};
do
    set_gpio ${RST_BMC_U2[$i]};
done

### Initial related Power by Present
for i in {0..7};
do
    update_value=$(printf '%x\n' "$((0x01 <<$i))")
    if [ $(read_gpio_input ${U2_PRESENT[$i]}) == $PLUGGED ];then
        CLOCK_GEN_VALUE=$(printf '0x%x\n' \
        "$(($CLOCK_GEN_VALUE | 0x$update_value))")
    else
        set_gpio_direction "${RST_BMC_U2[$i]}" "low"
        set_gpio_direction "${POWER_U2[$i]}" "low"

        CLOCK_GEN_VALUE=$(printf '0x%x\n' \
        "$(($CLOCK_GEN_VALUE & ~0x$update_value))")
    fi
done
i2cset -y $I2C_BUS $CHIP_ADDR 0 $CLOCK_GEN_VALUE s
echo "Read Clock Gen Value again is: $CLOCK_GEN_VALUE"

exit 0;
