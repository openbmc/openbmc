SUMMARY = "OpenBMC console daemon"
DESCRIPTION = "Daemon to handle UART console connections"
HOMEPAGE = "http://github.com/openbmc/obmc-console"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-systemd
inherit autotools
inherit obmc-phosphor-discovery-service

TARGET_CFLAGS   += "-fpic -O2"

DEPENDS += "autoconf-archive-native"

SRC_URI += "git://github.com/openbmc/obmc-console"
SRC_URI += "file://${PN}.conf"

SRCREV = "67eab046047e7442c0b2c241afefc3c7c0d0189b"

REGISTERED_SERVICES_${PN} += "obmc_console:tcp:2200"
OBMC_CONSOLE_HOST_TTY ?= "ttyVUART0"
SYSTEMD_SUBSTITUTIONS += "OBMC_CONSOLE_HOST_TTY:${OBMC_CONSOLE_HOST_TTY}:${PN}-ssh@.service"
SYSTEMD_SUBSTITUTIONS += "OBMC_CONSOLE_HOST_TTY:${OBMC_CONSOLE_HOST_TTY}:${PN}-ssh.socket"

SYSTEMD_SERVICE_${PN} = " \
        ${PN}@.service \
        ${PN}@${OBMC_CONSOLE_HOST_TTY}.service \
        ${PN}-ssh.socket \
        ${PN}-ssh@.service \
        "
do_install_append() {
        install -m 0755 -d ${D}${sysconfdir}
        install -m 0644 ${WORKDIR}/${PN}.conf ${D}${sysconfdir}/${PN}.conf
}

S = "${WORKDIR}/git"
