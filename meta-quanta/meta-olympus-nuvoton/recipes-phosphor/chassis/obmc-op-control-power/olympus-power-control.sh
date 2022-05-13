echo "set power state $1"

powerOnRetry=3
powerOffRetry=1
powerOnTimeOut=30
powerOffTimeOut=90
powerPluseTime=0.2
ForcePowerPluseTime=15
state=$1
pwrpin=$3
pgpin=$2

powerPluse() {
    echo "gpio${pgpin} is pgood pin"
    echo 0 > /sys/class/gpio/gpio${pwrpin}/value
    echo "gpio${pwrpin} output low"
    sleep ${powerPluseTime}

    echo 1 > /sys/class/gpio/gpio${pwrpin}/value
    echo "gpio${pwrpin} output hi"
}

ForcePowerOff() {
    echo "Try to ForcePowerOff"

    echo 0 > /sys/class/gpio/gpio${pwrpin}/value
    echo "gpio${pwrpin} output low"
    sleep ${ForcePowerPluseTime}

    echo 1 > /sys/class/gpio/gpio${pwrpin}/value
    echo "gpio${pwrpin} output hi"
}

PowerOffTimerStart() {
    i=0
    pgood=1
    until [ "${pgood}" == "0" -o "$i" == "${powerOffTimeOut}" ]
    do
       i=$(($i+1))
       pgood=$(cat /sys/class/gpio/gpio${pgpin}/value)
       sleep 1
    done

    echo ${pgood}
}

PowerOnTimerStart() {
    i=0
    pgood=0
    until [ "${pgood}" == "1" -o "$i" == "${powerOnTimeOut}" ]
    do
       i=$(($i+1))
       pgood=$(cat /sys/class/gpio/gpio${pgpin}/value)
       sleep 1
    done

    echo ${pgood}
}

try=0
PGOOD=$(cat /sys/class/gpio/gpio${pgpin}/value)

if [ ${state} = "1" ]; then
    echo "Power On Start"
    until [ "${PGOOD}" == "1" -o "${try}" == "${powerOnRetry}" ]
    do
      try=$((${try}+1))
      powerPluse
      PGOOD=$(PowerOnTimerStart)
    done
    echo "Power On done; pgood=${PGOOD}; Tried=${try}"
else
    echo "Power Off Start"
    until [ "${PGOOD}" == "0" -o "${try}" == "${powerOffRetry}" ]
    do
      try=$((${try}+1))
      powerPluse
      PGOOD=$(PowerOffTimerStart)
    done
    echo "Power Off done; pgood=${PGOOD}; Tried=${try}"

    if [ "${PGOOD}" == "1" ]; then
        ForcePowerOff
    fi
fi

if [ $1 = "1" ]; then
    if [ -d "/sys/bus/platform/drivers/peci_npcm/f0100000.peci-bus" ]; then
        echo f0100000.peci-bus > /sys/bus/platform/drivers/peci_npcm/unbind; echo f0100000.peci-bus > /sys/bus/platform/drivers/peci_npcm/bind
    else
        echo f0100000.peci-bus > /sys/bus/platform/drivers/peci_npcm/bind
    fi
else
    if [ -d "/sys/bus/platform/drivers/peci_npcm/f0100000.peci-bus" ]; then
        echo f0100000.peci-bus > /sys/bus/platform/drivers/peci_npcm/unbind
    fi
fi
