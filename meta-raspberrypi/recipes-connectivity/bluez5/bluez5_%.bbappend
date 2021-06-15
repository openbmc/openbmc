FILESEXTRAPATHS_prepend_rpi := "${THISDIR}/${PN}:"

SRC_URI_append_rpi = "\
    file://0001-bcm43xx-Add-bcm43xx-3wire-variant.patch \
    file://0002-bcm43xx-The-UART-speed-must-be-reset-after-the-firmw.patch \
    file://0003-Increase-firmware-load-timeout-to-30s.patch \
    file://0004-Move-the-43xx-firmware-into-lib-firmware.patch \
"

RDEPENDS_${PN}_append_rpi = " pi-bluetooth"
