SUMMARY = "Phosphor Fan Presence"
DESCRIPTION = "Phosphor fan presence provides a set of fan presence \
daemons to monitor fan presence changes by different methods of \
presence detection."
HOMEPAGE = "https://github.com/openbmc/phosphor-fan-presence"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit autotools pkgconfig pythonnative
inherit obmc-phosphor-systemd

DEPENDS += "autoconf-archive-native"
DEPENDS += "python-pyyaml-native"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-logging"
RDEPENDS_${PN} += "sdbusplus"

FAN_PRESENCE_PACKAGES = " \
    ${PN}-tach \
"
PACKAGES_remove = "${PN}"
PACKAGES += "${FAN_PRESENCE_PACKAGES}"
SYSTEMD_PACKAGES = "${FAN_PRESENCE_PACKAGES}"
RDEPENDS_${PN}-dev = "${FAN_PRESENCE_PACKAGES}"

FILES_${PN}-tach = "${sbindir}/phosphor-fan-presence-tach"
SYSTEMD_SERVICE_${PN}-tach += "phosphor-fan-presence-tach.service"

SRC_URI += "git:///home/msbarth/openbmc/phosphor-fan-presence"
#SRC_URI += "git://github.com/openbmc/phosphor-fan-presence"
SRCREV = "f25b4c99bb029a9673ca5554c826dc056783e6b0"

S = "${WORKDIR}/git"
