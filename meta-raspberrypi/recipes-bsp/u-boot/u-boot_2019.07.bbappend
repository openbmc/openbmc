FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"
UBOOT_RPI4_SUPPORT_PATCHES = " \
    file://0001-configs-rpi4-Add-defconfigs-for-rpi4-32-64.patch \
    file://0002-ARM-bcm283x-Add-BCM283x_BASE-define.patch \
    file://0003-arm-mach-bcm283x-Define-configs-for-RaspberryPi-4.patch \
    file://0004-rpi-Add-entry-for-Raspberry-Pi-4-model-B.patch \
    file://0005-arm-bcm283x-Include-definition-for-additional-emmc-c.patch \
    file://0006-mmc-bcm2835_sdhci-Add-support-for-bcm2711-device.patch \
    file://0007-rpi-Add-memory-map-for-bcm2838.patch \
"

SRC_URI_append_raspberrypi4 = "${UBOOT_RPI4_SUPPORT_PATCHES}"
