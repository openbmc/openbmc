#!/bin/sh

# GPIO to control the host SPI mux
SPI_MUX_SELECT=114
SPI_SW_SELECT=115


# Kernel control string for bind/unbind
KERNEL_FIU_ID="c0000000.spi"

# Kernel sysfs path for bind/unbind
KERNEL_SYSFS_FIU="/sys/bus/platform/drivers/NPCM-FIU"

IMAGE_FILE="/tmp/image-bios"

FLASH_LOG="/tmp/bios_flash.log"

findmtd() {
    m=$(grep -xl "$1" /sys/class/mtd/*/name)
    m=${m%/name}
    m=${m##*/}
    echo $m
}

HostPower() {
    i=0
    res=0

    #busctl set-property xyz.openbmc_project.State.Host \
    #    /xyz/openbmc_project/state/host0 \
    #    xyz.openbmc_project.State.Host \
    #    RequestedHostTransition s \
    #    xyz.openbmc_project.State.Host.Transition.${1}

    if [ "${1}" == "Off" ];then
        ipmitool power off
    else
        ipmitool power on
    fi

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

SetGPIO() {
    #Switch for disconnecting flash from host
    if [ ! -d "/sys/class/gpio/gpio${SPI_SW_SELECT}" ];then
        echo ${SPI_SW_SELECT} > /sys/class/gpio/export
    fi

    if [ "${1}" == "1" ];then
        echo high to 115;
        echo high > /sys/class/gpio/gpio${SPI_SW_SELECT}/direction
    else
        echo echo low to 115;
        echo low > /sys/class/gpio/gpio${SPI_SW_SELECT}/direction
    fi

    #MUX for selecting CS0 signal
    if [ ! -d "/sys/class/gpio/gpio${SPI_MUX_SELECT}" ];then
        echo ${SPI_MUX_SELECT} > /sys/class/gpio/export
    fi

    if [ "${1}" == "1" ];then
        echo low to 114;
        echo low > /sys/class/gpio/gpio${SPI_MUX_SELECT}/direction
    else
        echo high to 114;
        echo high > /sys/class/gpio/gpio${SPI_MUX_SELECT}/direction
    fi
}

BIOSFlashMount() {
    SetGPIO ${1}

    if [ "${1}" == "1" ];then
        #bit 16-1:SPI3_CLK/D0/D1/CS0
        MFSEL4=$(devmem 0xF080026C)
        MFSEL4_New=$((${MFSEL4} | 0x10000))
        devmem 0xF080026C 32 ${MFSEL4_New}
    else
        #bit 16-1:SPI3_CLK/D0/D1/CS0
        MFSEL4=$(devmem 0xF080026C)
        MFSEL4_New=$((${MFSEL4} & 0xFFFEFFFF))
        devmem 0xF080026C 32 ${MFSEL4_New}
    fi

    if [ -d "${KERNEL_SYSFS_FIU}/${KERNEL_FIU_ID}" ];then
        echo "${KERNEL_FIU_ID}" > "${KERNEL_SYSFS_FIU}"/unbind
    fi

    if [ "${1}" == "1" ];then
        echo "${KERNEL_FIU_ID}" > "${KERNEL_SYSFS_FIU}"/bind
    fi
}

trap "BIOSFlashMount 0; HostPower On" EXIT SIGHUP SIGINT SIGTERM

main() {
    if [ ! -f ${IMAGE_FILE} ]; then
        echo "Invalid bios image file!" | tee -a ${FLASH_LOG}
        exit 1
    fi

    HostPower Off

    echo "Mount BIOS flash" | tee -a ${FLASH_LOG}
    BIOSFlashMount 1

    echo "Find mtd partition for bios" | tee -a ${FLASH_LOG}
    m=$(findmtd image-bios)
    if test -z "$m"
    then
        echo "Unable to find mtd partition for bios." | tee -a ${FLASH_LOG}
        exit 1
    fi

    echo "Starting programing BIOS flash" | tee -a ${FLASH_LOG}
    flashcp -v ${IMAGE_FILE} /dev/$m | tee -a ${FLASH_LOG}
    if [ $? -eq 0 ]; then
        echo "bios update successfully..." | tee -a ${FLASH_LOG}
    else
        echo "bios update failed..." | tee -a ${FLASH_LOG}
        exit 1
    fi
}

main "$@"
