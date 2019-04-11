#!/bin/bash

U2_PRESENT_STATUS=( 1 1 1 1 1 1 1 1 )
U2_PRESENT=( 148 149 150 151 152 153 154 155 )
POWER_U2=( 195 196 202 199 198 197 127 126 )
PWRGD_U2=( 161 162 163 164 165 166 167 168 )


function set_gpio_direction(){
    #$1 gpio pin, $2 'in','high','low'
    echo $2 > /sys/class/gpio/gpio$1/direction
}

function read_present_set_related_power(){
    #$1 read present number, $2 output power gpio
    var="${U2_PRESENT_STATUS[$1]}"
    # present 0 is plugged,present 1 is removal
    if [ "$var" == "0" ];then
        set_gpio_direction $2 "high"
    else 
        set_gpio_direction $2 "low"
    fi
}

function update_u2_status(){
  #$1 read present gpio
  var=$(cat /sys/class/gpio/gpio$2/value)
  U2_PRESENT_STATUS[$1]="$var"
}

function check_present_and_powergood(){
    #$2 present gpio, $3 powergood gpio
    present=$(cat /sys/class/gpio/gpio$2/value)
    pwrgd=$(cat /sys/class/gpio/gpio$3/value)
    path=`expr $1`
    if [ "$present" -eq 0 ] && [ "$pwrgd" -eq 1 ];then        
        busctl set-property xyz.openbmc_project.nvme.manager /xyz/openbmc_project/nvme/$path xyz.openbmc_project.Inventory.Item Present b true
    else        
        busctl set-property xyz.openbmc_project.nvme.manager /xyz/openbmc_project/nvme/$path xyz.openbmc_project.Inventory.Item Present b false
            if [ "$present" -eq "$pwrgd" ];then
                #set fault led
                busctl set-property xyz.openbmc_project.LED.GroupManager /xyz/openbmc_project/led/groups/led\_u2\_$1\_fault xyz.openbmc_project.Led.Group Asserted b true
            else
                busctl set-property xyz.openbmc_project.LED.GroupManager /xyz/openbmc_project/led/groups/led\_u2\_$1\_fault xyz.openbmc_project.Led.Group Asserted b false
            fi
        
    fi
    

}

##Initial U2 present status
for i in {0..7};
do 
    update_u2_status $i "${U2_PRESENT[$i]}"
done


## Loop while
while :
do
  for i in {0..7};
  do
    ## 1 scend scan all loop
    sleep 0.125
    read=$(cat /sys/class/gpio/gpio${U2_PRESENT[$i]}/value)
    if [ "${U2_PRESENT_STATUS[$1]}" != read ];then
        update_u2_status $i "${U2_PRESENT[$i]}"
        read_present_set_related_power $i "${POWER_U2[$i]}"
        check_present_and_powergood $i "${U2_PRESENT[$i]}" "${POWER_U2[$i]}"
    fi 
  done
done
