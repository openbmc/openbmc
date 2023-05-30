#!/bin/sh
# File which is managing GPIOS when detected. First parameter is which GPIOs that switched
if [ "$1" = "up" ]
then
    systemctl stop xyz.openbmc_project.ampere_host_error_monitor.service
    rmmod smpro_hwmon smpro_errmon smpro_misc smpro_mfd
    busctl set-property xyz.openbmc_project.Chassis.Gpios /xyz/openbmc_project/chassis/gpios xyz.openbmc_project.Chassis.Gpios PGood b true
    smproStatus=$(lsmod | grep smpro-mfd)
    if [ "$smproStatus" = "" ]
    then
        # sleep 15
        # We need to wait for the SoC to be ready to communicate - We just received the PGOOD signal
        # The status is reported through an interrupt that we can poll at 2e within the CPLD address space (0xd100_00e2)
        max_retry=5
        sleep_time=5
        success=0
        while [ "$max_retry" != "0" ]
        do
            waitForSoC=$(devmem 0xd10000e2 8)
            isAvailable=$(( waitForSoC & 0x2 ))
            if [ "r$isAvailable" = "r2" ]
            then
                max_retry=0
                success=1
            else
                max_retry=$(( max_retry - 1))
                sleep $sleep_time
            fi
        done
        if [ "$success" = "1" ]
        then
            # Activate local UBM (front NVME drive)
            devmem 0xd1000087 8 5
            i2ctransfer -y 3 w4@0x40 0x34 0xbf 0x00 0xe8
            i2ctransfer -y 3 w2@0x40 0x34 0xa7 r1

            devmem 0xd1000087 8 7
            i2ctransfer -y 3 w4@0x40 0x34 0xbf 0x00 0xe8
            i2ctransfer -y 3 w2@0x40 0x34 0xa7 r1

            devmem 0xd1000087 8 9
            i2ctransfer -y 3 w4@0x40 0x34 0xbf 0x00 0xe8
            i2ctransfer -y 3 w2@0x40 0x34 0xa7 r1

            devmem 0xd1000087 8 0xb
            i2ctransfer -y 3 w4@0x40 0x34 0xbf 0x00 0xe8
            i2ctransfer -y 3 w2@0x40 0x34 0xa7 r1

            devmem 0xd1000088 8 5
            i2ctransfer -y 4 w4@0x40 0x34 0xbf 0x00 0xe8
            i2ctransfer -y 4 w2@0x40 0x34 0xa7 r1

            modprobe smpro-misc
            modprobe smpro-errmon
            modprobe smpro-hwmon
            modprobe smpro-mfd
            systemctl restart xyz.openbmc_project.ampere_host_error_monitor.service
            systemctl restart xyz.openbmc_project.amperecpusensor.service
            systemctl restart xyz.openbmc_project.EntityManager.service
            systemctl restart phosphor-pid-control.service
        fi
        # We shall add a condition here to make an emergency stop
    fi
else
    if [ "$1" = "down" ]
    then
        busctl set-property xyz.openbmc_project.Chassis.Gpios /xyz/openbmc_project/chassis/gpios xyz.openbmc_project.Chassis.Gpios PGood b false
        # We can re-init the system
        devmem 0xd1000009 8 0x24
        # We need to check the reason why we have been shutdown
        shutdownReason=$(devmem 0x80000074 16)
        # If bit 10 is set Software initiated a shutdown we need to restart
        isSet2=$( ${shutdownReason} | 0xFBFF )
        if [ "$isSet2" = "65535" ]
        then
            echo "Restarting" >> /tmp/gpios
            obmcutil poweron
        fi
    fi
fi
echo "$1" >> /tmp/gpios.txt
