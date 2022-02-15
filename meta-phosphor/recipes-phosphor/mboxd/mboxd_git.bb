SUMMARY = "Phosphor OpenBMC MBOX Daemon"
DESCRIPTION = "Phosphor OpenBMC MBOX Daemon"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit autotools pkgconfig
inherit obmc-phosphor-systemd

DEPENDS += "autoconf-archive-native"
DEPENDS += "systemd"
DEPENDS += "phosphor-logging"

S = "${WORKDIR}/git"

SRC_URI += "git://github.com/openbmc/mboxbridge.git;branch=master;protocol=https"

SRC_URI += "file://99-aspeed-lpc-ctrl.rules"

SRCREV="5c9264daedf1aff690e1957c4578d8395b549cff"

PROVIDES += "mboxctl"

MBOXD_FLASH_SIZE ??= "32M"
SYSTEMD_SUBSTITUTIONS += "FLASH_SIZE:${MBOXD_FLASH_SIZE}:${PN}.service"

do_install:append() {
    install -d ${D}/${nonarch_base_libdir}/udev/rules.d
    install -m 0644 ${WORKDIR}/99-aspeed-lpc-ctrl.rules ${D}/${nonarch_base_libdir}/udev/rules.d
}

TMPL = "mboxd-reload@.service"
TGTFMT = "obmc-host-startmin@{0}.target"
INSTFMT = "mboxd-reload@{0}.service"
FMT = "../${TMPL}:${TGTFMT}.wants/${INSTFMT}"

SYSTEMD_SERVICE:${PN} += "mboxd.service"
SYSTEMD_SERVICE:${PN} += "mboxd-reload@.service"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'FMT', 'OBMC_HOST_INSTANCES')}"

# Enable virtual-pnor by DISTRO_FEATURE openpower-virtual-pnor.
PACKAGECONFIG:append:df-openpower-virtual-pnor = " virtual-pnor"
PACKAGECONFIG[virtual-pnor] = "--enable-virtual-pnor,--disable-virtual-pnor"
