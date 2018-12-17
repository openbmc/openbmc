FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

BCM_BT_SOURCES =  " \
    file://0001-bcm43xx-Add-bcm43xx-3wire-variant.patch \
    file://0002-bcm43xx-The-UART-speed-must-be-reset-after-the-firmw.patch \
    file://0003-Increase-firmware-load-timeout-to-30s.patch \
    file://0004-Move-the-43xx-firmware-into-lib-firmware.patch \
"

BCM_BT_RDEPENDS = "pi-bluetooth"

SRC_URI_append_raspberrypi0-wifi = " ${BCM_BT_SOURCES}"
SRC_URI_append_raspberrypi3 = " ${BCM_BT_SOURCES}"

RDEPENDS_${PN}_append_raspberrypi0-wifi = " ${BCM_BT_RDEPENDS}"
RDEPENDS_${PN}_append_raspberrypi3 = " ${BCM_BT_RDEPENDS}"
