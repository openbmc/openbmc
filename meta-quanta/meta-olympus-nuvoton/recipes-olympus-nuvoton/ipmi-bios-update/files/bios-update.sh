#!/bin/sh

# Global variables

# GPIO to control the host SPI mux
SPI_SW_SELECT=227

# Kernel control string for bind/unbind
KERNEL_FIU_ID="c0000000.spi"

# Kernel sysfs path for bind/unbind
KERNEL_SYSFS_FIU="/sys/bus/platform/drivers/NPCM-FIU"

IMAGE_FILE="/tmp/image-bios"

FLASH_LOG="/tmp/bios_flash.log"

POWER_CTRL_TIMEOUT=360
ME_TIMEOUT=30

# ME IPMB commands
MeChannelNum=0x01

MeColdResetNetFn=0x06
MeColdResetLun=0x00
MeColdResetCmd=0x02

MeForceMERecoveryNetFn=0x2e
MeForceMERecoveryLun=0x00
MeForceMERecoveryCmd=0xdf
MeRestartRecoveryFW=0x01
MeForceMERecoveryRes=47

MeGetDevIdNetFn=0x06
MeGetDevIdLun=0x00
MeGetTestResults=0x4
MeGetDevIdRes=7

IntelID0=0x57
IntelID1=0x01
IntelID2=0x00

MERecoveryByIPMI=129
MENOERROR=85

findmtd() {
    m=$(grep -xl "$1" /sys/class/mtd/*/name)
    m=${m%/name}
    m=${m##*/}
    echo $m
}

GetMEStatus() {
    i=0
    res=0
    until [ "$res" == "${1}" -o "$i" == "${ME_TIMEOUT}" ]
    do
        i=$(($i+1))
        res=$(busctl call xyz.openbmc_project.Ipmi.Channel.Ipmb \
            /xyz/openbmc_project/Ipmi/Channel/Ipmb \
            org.openbmc.Ipmb sendRequest yyyyay \
            ${MeChannelNum} ${MeGetDevIdNetFn} ${MeGetDevIdLun} ${MeGetTestResults} 0)
        res=$(echo $(echo ${res} | awk '{print $8}'))
        echo "ME Status ${res} " | tee -a ${FLASH_LOG}
        sleep 1
    done

    if [ "$res" != "${1}" ];then
        echo "ME status error $res" | tee -a ${FLASH_LOG}
        exit 1
    fi

}

SendIPMB() {
    res=$(busctl call xyz.openbmc_project.Ipmi.Channel.Ipmb \
        /xyz/openbmc_project/Ipmi/Channel/Ipmb \
        org.openbmc.Ipmb sendRequest yyyyay \
        ${1} ${2} ${3} ${4} \
        ${5} ${6} ${7} ${8} ${9})

    echo "IPMB Respond ${res}" | tee -a ${FLASH_LOG}
}

ForceMERecovery() {
    i=0
    done=0

    echo "Force ME Recovery" | tee -a ${FLASH_LOG}
    until [ "$done" == "1" -o "$i" == "${ME_TIMEOUT}" ]
    do
        i=$(($i+1))
        SendIPMB ${MeChannelNum} ${MeForceMERecoveryNetFn} \
            ${MeForceMERecoveryLun} ${MeForceMERecoveryCmd} \
            4 ${IntelID0} ${IntelID1} ${IntelID2} ${MeRestartRecoveryFW}

        cmdr=$(echo ${res} | awk '{print $3}')
        completed=$(echo ${res} | awk '{print $6}')

        if [ "${cmdr}" == "${MeForceMERecoveryRes}" -a "${completed}" == "0" ];then
           done=1
        fi

        sleep 1
    done

    if [ "$done" != "1" ];then
        echo "Force ME Recovery fail!! $res" | tee -a ${FLASH_LOG}
        exit 1
    fi

    GetMEStatus ${MERecoveryByIPMI}
}

MEColdReset() {
    i=0
    done=0
    until [ "$done" == "1" -o "$i" == "${ME_TIMEOUT}" ]
    do
        i=$(($i+1))
        SendIPMB ${MeChannelNum} ${MeColdResetNetFn} \
            ${MeColdResetLun} ${MeColdResetCmd} 0

        cmdr=$(echo ${res} | awk '{print $3}')
        completed=$(echo ${res} | awk '{print $6}')

        if [ "${cmdr}" == "${MeGetDevIdRes}" -a "${completed}" == "0" ];then
           done=1
        fi

        sleep 1
    done

    if [ "$done" != "1" ];then
        echo "ME Reset fail!! $res" | tee -a ${FLASH_LOG}
        exit 1
    fi

    GetMEStatus ${MENOERROR}

    sleep 9
}

HostPower() {
    i=0
    res=0

    busctl set-property xyz.openbmc_project.State.Host \
        /xyz/openbmc_project/state/host0 \
        xyz.openbmc_project.State.Host \
        RequestedHostTransition s \
        xyz.openbmc_project.State.Host.Transition.${1}

    if [ "${1}" == "Off" ];then
        until [ "$res" == "1" -o "$i" == "${POWER_CTRL_TIMEOUT}" ]
        do
            i=$(($i+1))
            state=$(busctl get-property xyz.openbmc_project.State.Chassis \
                /xyz/openbmc_project/state/chassis0 \
                xyz.openbmc_project.State.Chassis \
                CurrentPowerState)
            res=$(echo "${state}" | grep -c "${1}")
            echo "${i}s  ${state}"
            sleep 1
        done

        if [ "$res" == "0" ];then
            echo "Power ${1} failed" | tee -a ${FLASH_LOG}
            exit 1
        fi
    fi

    echo "Power ${1} success" | tee -a ${FLASH_LOG}
}

BIOSFlashMount() {
    if [ ! -d "/sys/class/gpio/gpio${SPI_SW_SELECT}" ];then
        echo ${SPI_SW_SELECT} > /sys/class/gpio/export
    fi

    if [ "${1}" == "1" ];then
        echo high > /sys/class/gpio/gpio${SPI_SW_SELECT}/direction
    else
        echo low > /sys/class/gpio/gpio${SPI_SW_SELECT}/direction
    fi

    if [ -d "${KERNEL_SYSFS_FIU}/${KERNEL_FIU_ID}" ];then
        echo "${KERNEL_FIU_ID}" > "${KERNEL_SYSFS_FIU}"/unbind
    fi
    echo "${KERNEL_FIU_ID}" > "${KERNEL_SYSFS_FIU}"/bind
}

trap "BIOSFlashMount 0; MEColdReset; HostPower On" EXIT SIGHUP SIGINT SIGTERM

main() {
    if [ ! -f ${IMAGE_FILE} ]; then
        echo "Invalid bios image file!" | tee -a ${FLASH_LOG}
        exit 1
    fi

    HostPower Off

    ForceMERecovery

    echo "Mount BIOS flash" | tee -a ${FLASH_LOG}
    BIOSFlashMount 1

    echo "Find mtd partition for bios" | tee -a ${FLASH_LOG}
    m=$(findmtd bios)
    if test -z "$m"
    then
        echo "Unable to find mtd partition for bios." | tee -a ${FLASH_LOG}
        exit 1
    fi

    echo "Starting programing BIOS flash" | tee -a ${FLASH_LOG}
    flashcp -v ${IMAGE_FILE} /dev/$m
    if [ $? -eq 0 ]; then
        echo "bios update successfully..." | tee -a ${FLASH_LOG}
    else
        echo "bios update failed..." | tee -a ${FLASH_LOG}
        exit 1
    fi
}

main "$@"
