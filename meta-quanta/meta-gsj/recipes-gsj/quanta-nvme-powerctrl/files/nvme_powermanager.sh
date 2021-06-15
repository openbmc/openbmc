#!/bin/bash

source /usr/libexec/nvme_powerctrl_library.sh
U2_PRESENT_STATUS=( 1 1 1 1 1 1 1 1 )

function recovery_power()
{
    set_gpio_direction "${POWER_U2[$1]}" "low"
    sleep 0.2
    set_gpio_direction "${POWER_U2[$1]}" "high"
    sleep 0.2
    check_powergood $1
}


##Initial U2 present status
for i in {0..7};
do
    U2_PRESENT_STATUS[$i]=$(read_gpio_input ${U2_PRESENT[$i]})
done

## Loop while
while :
do
  for i in {0..7};
  do
    ## 1 second scan all loop
    sleep 0.125
    read_present=$(read_gpio_input ${U2_PRESENT[$i]})
    if [ "$read_present" != "${U2_PRESENT_STATUS[$i]}" ];then
        U2_PRESENT_STATUS[$i]="$read_present"
        if [ "$read_present" == $PLUGGED ];then
            echo "NVME $i Enable Power"
            enable_nvme_power $i
        else
            echo "NVME $i Disable Power"
            disable_nvme_power $i
        fi
    else
        if [ "${U2_PRESENT_STATUS[$i]}" == $PLUGGED ] &&
           [ $(read_gpio_input ${PWRGD_U2[$i]}) == 0 ];then
            echo "NVME $i Recovery Power"
            recovery_power $i
        fi
    fi
  done
done
