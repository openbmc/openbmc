SUMMARY = "Phosphor Network IPMI Daemon"
DESCRIPTION = "Daemon to support IPMI protocol over network"
HOMEPAGE = "https://github.com/openbmc/phosphor-net-ipmid"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit autotools pkgconfig
inherit obmc-phosphor-systemd

DEPENDS += "autoconf-archive-native"
DEPENDS += "phosphor-mapper"
DEPENDS += "systemd"
DEPENDS += "phosphor-ipmi-host"
RDEPENDS_${PN} += "libmapper"
RDEPENDS_${PN} += "libsystemd"
RDEPENDS_${PN} += "iptables"

SRC_URI += "git://github.com/openbmc/phosphor-net-ipmid"
SRC_URI += "file://ipmi-net-firewall.sh"
SRCREV = "744b3c8b840a13ed4a6c07836ead4a0c88911437"

S = "${WORKDIR}/git"

do_install_append() {
        install -m 0755 ${WORKDIR}/ipmi-net-firewall.sh \
        ${D}${sbindir}/ipmi-net-firewall.sh
}

SYSTEMD_SERVICE_${PN} = " \
        ${PN}.service \
        ${PN}.socket \
        "
