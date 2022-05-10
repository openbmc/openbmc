FILESEXTRAPATHS:prepend := "${THISDIR}:"
DESCRIPTION = "MCU F/W Programmer"
PR = "r1"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit systemd
inherit obmc-phosphor-systemd
inherit autotools pkgconfig

DEPENDS += "systemd"
DEPENDS += "autoconf-archive-native"
RDEPENDS:${PN} += "bash"

SRC_URI = "git://github.com/Nuvoton-Israel/loadmcu.git;branch=master;protocol=https \
           file://mcu-update.service \
           file://mcu-version.sh \
           file://mcu-version@.service \
          "
SRCREV = "12fed94b53f6fa0fe1c96bee264c7e363f2ed7d8"
S = "${WORKDIR}/git"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "mcu-update.service mcu-version@.service"
SYSTEMD_SERVICE:${PN} += "mcu-version@13.service"

do_install:append() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/mcu-version.sh ${D}${bindir}/
}
