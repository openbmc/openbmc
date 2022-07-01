FILESEXTRAPATHS:prepend := "${THISDIR}:"
DESCRIPTION = "CPLD/FPGA Programmer"
PR = "r1"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/Nuvoton-Israel/loadsvf.git;branch=master;protocol=https \
    	   file://cpld-update.service \
           file://cpld-update.sh \
          "

SRCREV = "f2296005cbba13b49e5163340cac80efbec9cdf4"
S = "${WORKDIR}/git"

inherit autotools pkgconfig
inherit systemd
inherit obmc-phosphor-systemd

DEPENDS += "autoconf-archive-native"
DEPENDS += "systemd"
RDEPENDS:${PN} += "bash"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "cpld-update.service"

do_install:append() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/cpld-update.sh ${D}${bindir}/
}
