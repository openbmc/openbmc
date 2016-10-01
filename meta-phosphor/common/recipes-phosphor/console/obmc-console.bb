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

SRCREV = "44580de4e2170c8ee06dbf401315d3acfcf52b22"

REGISTERED_SERVICES_${PN} += "obmc_console:_obmc_console._tcp:2200"

SYSTEMD_SERVICE_${PN} = " \
        ${PN}.service \
        ${PN}-ssh.socket \
        ${PN}-ssh@.service \
        "

do_install_append() {
        install -m 0755 -d ${D}${sysconfdir}
        install -m 0644 ${WORKDIR}/${PN}.conf ${D}${sysconfdir}/${PN}.conf
}

S = "${WORKDIR}/git"
