# Copyright (c) 2021-24 Axiado Corporation (or its affiliates). All rights reserved.

SUMMARY = "USB Bridging Support"
DESCRIPTION = "USB dev rules and USB configfs script"
LICENSE = "CLOSED"
PV = "1.0"

LIC_FILES_CHKSUM ?= "file://${COREBASE}/meta-axiado/COPYING.axiado;md5=3da9cfbcb788c80a0384361b4de20420"

inherit systemd
inherit obmc-phosphor-systemd
DEPENDS = "systemd"
RDEPENDS:${PN} = "bash"

SRC_URI += "file://ax-usb.rules \
            file://create-usb-rndis.service \
            file://create-usb-rndis.sh \
            file://power_monitor_host_server_usb.sh \
            file://usbgadget.sh \
            file://usb-connect-on-power-up-host.service \
            "

SYSTEMD_SERVICE:${PN} = "usb-connect-on-power-up-host.service create-usb-rndis.service"

do_install() {
        install -d ${D}/etc/usb/
        install -m 0777 ${UNPACKDIR}/usbgadget.sh ${D}/etc/usb/
        install -m 0777 ${UNPACKDIR}/power_monitor_host_server_usb.sh ${D}/etc/usb/power_monitor_usb.sh

        install -d ${D}/etc/udev/rules.d/
        install -m 0644 ${UNPACKDIR}/ax-usb.rules ${D}/etc/udev/rules.d/

        install -d ${D}${bindir}
        install -m 0777 ${UNPACKDIR}/create-usb-rndis.sh ${D}${bindir}/
}
