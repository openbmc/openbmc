SUMMARY = "SNMP Manager Configuration"
DESCRIPTION = "SNMP Manager Configuration."
HOMEPAGE = "http://github.com/openbmc/phosphor-snmp"
PR = "r1"
PV = "0.1+git${SRCPV}"

inherit meson pkgconfig
inherit python3native
inherit obmc-phosphor-dbus-service

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI += "git://github.com/openbmc/phosphor-snmp;branch=master;protocol=https"
SRCREV = "dec302696ab3d2f89b64d401e0da9d9bf240c7e5"

DBUS_SERVICE:${PN} += "xyz.openbmc_project.Network.SNMP.service"

DEPENDS += "systemd"
DEPENDS += "sdbusplus ${PYTHON_PN}-sdbus++-native"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "phosphor-logging"
DEPENDS += "net-snmp"

S = "${WORKDIR}/git"
