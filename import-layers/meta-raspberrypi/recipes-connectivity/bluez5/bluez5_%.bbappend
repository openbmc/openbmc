FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

BCM_BT_SOURCES =  " \
    file://0001-bcm43xx-Add-bcm43xx-3wire-variant.patch \
    file://0002-bcm43xx-The-UART-speed-must-be-reset-after-the-firmw.patch \
    file://0003-Increase-firmware-load-timeout-to-30s.patch \
    file://0004-Move-the-43xx-firmware-into-lib-firmware.patch \
    file://brcm43438.service \
    "

BCM_BT_SERVICE = "brcm43438.service"

enable_bcm_bluetooth() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${WORKDIR}/${BCM_BT_SERVICE} ${D}${systemd_unitdir}/system
    fi
}

BCM_BT_RDEPENDS = "\
    udev-rules-rpi \
    pi-bluetooth \
"

# for raspberrypi3
SRC_URI_append_raspberrypi3 = " ${BCM_BT_SOURCES}"

do_install_append_raspberrypi3() {
    enable_bcm_bluetooth
}

SYSTEMD_SERVICE_${PN}_append_raspberrypi3 = " ${BCM_BT_SERVICE}"

RDEPENDS_${PN}_append_raspberrypi3 = " ${BCM_BT_RDEPENDS}"

# for raspberrypi0-wifi
SRC_URI_append_raspberrypi0-wifi = " ${BCM_BT_SOURCES}"

do_install_append_raspberrypi0-wifi() {
    enable_bcm_bluetooth
}

SYSTEMD_SERVICE_${PN}_append_raspberrypi0-wifi = " ${BCM_BT_SERVICE}"

RDEPENDS_${PN}_append_raspberrypi0-wifi = " ${BCM_BT_RDEPENDS}"
