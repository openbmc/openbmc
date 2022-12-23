SUMMARY = "Phosphor DBUS Object Manager"
DESCRIPTION = "Phosphor DBUS object manager."
HOMEPAGE = "http://github.com/openbmc/phosphor-objmgr"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"
DEPENDS += "systemd"
DEPENDS += "boost"
DEPENDS += "libtinyxml2"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-logging"
DEPENDS += "cli11"
SRCREV = "fffd34daac3d6d1882c58f46a2c5ac15bbc3d0a5"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI += "git://github.com/openbmc/phosphor-objmgr;branch=master;protocol=https"

SYSTEMD_SERVICE:${PN} += " \
        mapper-wait@.service \
        mapper-subtree-remove@.service \
        "
S = "${WORKDIR}/git"

inherit meson pkgconfig
inherit obmc-phosphor-dbus-service
inherit obmc-phosphor-systemd

EXTRA_OEMESON += "-Dtests=disabled"

PROVIDES += "libmapper"
PACKAGE_BEFORE_PN += "libmapper"
FILES:libmapper = "${libdir}/lib*.so*"

DBUS_SERVICE:${PN} += "xyz.openbmc_project.ObjectMapper.service"
