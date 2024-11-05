SUMMARY = "Phosphor OpenBMC MBOX Daemon"
DESCRIPTION = "Phosphor OpenBMC MBOX Daemon"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
DEPENDS += "autoconf-archive-native"
DEPENDS += "systemd"
DEPENDS += "phosphor-logging"
PROVIDES += "mboxctl"
SRCREV = "b8d89b777e5e2c4242802b4554818f65a5fadf1a"
# Enable virtual-pnor by DISTRO_FEATURE openpower-virtual-pnor.
PACKAGECONFIG:append:df-openpower-virtual-pnor = " virtual-pnor"
PACKAGECONFIG[virtual-pnor] = "--enable-virtual-pnor,--disable-virtual-pnor"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/hiomapd.git;branch=master;protocol=https"
SRC_URI += "file://99-aspeed-lpc-ctrl.rules"

S = "${WORKDIR}/git"
SYSTEMD_SUBSTITUTIONS += "FLASH_SIZE:${MBOXD_FLASH_SIZE}:${PN}.service"
SYSTEMD_SUBSTITUTIONS += "WINDOW_NUM:${MBOXD_WINDOW_NUM}:${PN}.service"
SYSTEMD_SERVICE:${PN} += "mboxd.service"
SYSTEMD_SERVICE:${PN} += "mboxd-reload@.service"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'FMT', 'OBMC_HOST_INSTANCES')}"
SYSTEMD_LINK[vardeps] += "OBMC_HOST_INSTANCES"

inherit autotools pkgconfig
inherit obmc-phosphor-systemd

do_install:append() {
    install -d ${D}/${nonarch_base_libdir}/udev/rules.d
    install -m 0644 ${WORKDIR}/99-aspeed-lpc-ctrl.rules ${D}/${nonarch_base_libdir}/udev/rules.d
}

MBOXD_FLASH_SIZE ??= "32M"
# When 0, code will default to use the entire reserved memory region
MBOXD_WINDOW_NUM ??= "0"
TMPL = "mboxd-reload@.service"
TGTFMT = "obmc-host-startmin@{0}.target"
INSTFMT = "mboxd-reload@{0}.service"
FMT = "../${TMPL}:${TGTFMT}.wants/${INSTFMT}"
