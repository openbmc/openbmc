#!/bin/sh

devmem 0xd1000008 8 128
devmem 0xd1000009 8 36
devmem 0xd1000041 8 255
devmem 0xd100004b 8 8
# Configure UART
# 4d is allocating UEFI SOC ROM to GXP
# 5d is allocating UEFI VAR ROM access to GXP
# devmem 0xd1000119 8 0x4d
devmem 0xc00000af 8 9
# enable UART on rl300
systemctl start obmc-console@ttyS1.service
devmem 0xd100011a 8 0x00
# setup PCIe ID
devmem 0x802f002e 16 0x03d8

# Check the current power status

currentstate=$(busctl get-property xyz.openbmc_project.State.Chassis0 /xyz/openbmc_project/state/chassis0 xyz.openbmc_project.State.Chassis CurrentPowerState | awk '{ print $2 }')

if [ "$currentstate" = "\"xyz.openbmc_project.State.Chassis.PowerState.On\"" ]
then
    systemctl stop phosphor-virtual-sensor.service
    modprobe smpro-misc
    modprobe smpro-errmon
    modprobe smpro-hwmon
    modprobe smpro-mfd
    systemctl restart xyz.openbmc_project.ampere_host_error_monitor.service
    systemctl restart xyz.openbmc_project.amperecpusensor.service
    systemctl restart xyz.openbmc_project.EntityManager.service
    systemctl restart phosphor-pid-control.service
    systemctl start phosphor-virtual-sensor.service
else
    # We can start the ROM Version check services
    rmmod gxp_spifi_ctrl1
    devmem 0xd1000119 8 0x4d
    modprobe gxp_spifi_ctrl1
    systemctl start com.hpe.hpe-uefi-version.service
fi

while true
do
    sleep 1
done
