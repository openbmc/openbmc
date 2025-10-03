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
SRCREV = "93d664fc1b21f1aad6a6759c33b913060fcf94ba"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-objmgr;branch=master;protocol=https"

PACKAGECONFIG ??= ""
PACKAGECONFIG[unit-failure-monitor] = "-Dunit-failure-monitor=enabled,-Dunit-failure-monitor=disabled,,"

SYSTEMD_SERVICE:${PN} += " \
        mapper-wait@.service \
        mapper-subtree-remove@.service \
        xyz.openbmc_project.ObjectMapper.service \
        "
S = "${WORKDIR}/git"

inherit meson pkgconfig systemd

EXTRA_OEMESON += "-Dtests=disabled"

PROVIDES += "libmapper"
PACKAGE_BEFORE_PN += "libmapper"
FILES:libmapper = "${libdir}/lib*.so*"
FILES:${PN} += "${datadir}/dbus-1"
