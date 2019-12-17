SUMMARY = "Phosphor OpenBMC KCS to DBUS"
DESCRIPTION = "Phosphor OpenBMC KCS to DBUS."
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b1beb00e508e89da1ed2a541934f28c0"

inherit autotools pkgconfig
inherit systemd

PV = "1.0+git${SRCPV}"

KCS_DEVICE ?= "ipmi-kcs3"

SYSTEMD_SERVICE_${PN} = " ${PN}@${KCS_DEVICE}.service "
FILES_${PN} += " ${systemd_system_unitdir}/${PN}@.service "

PROVIDES += "virtual/obmc-host-ipmi-hw"
RPROVIDES_${PN} += "virtual-obmc-host-ipmi-hw"
RRECOMMENDS_${PN} += "phosphor-ipmi-host"

DEPENDS += " \
        autoconf-archive-native \
        systemd \
        sdbusplus \
        boost \
        phosphor-logging \
        cli11 \
        "

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/openbmc/kcsbridge.git"
SRCREV = "46525ae48db23333493ac927c12ed13a0e663de5"
