SUMMARY = "Bluez Tools"
DESCRIPTION = "\
    Additional tools for bluez5 to list, manage, and show inforations about \
    adapters, agents, devices, network connections, and obex. \
"
HOMEPAGE = "https://github.com/khvzak/bluez-tools"
BUGTRACKER = "https://github.com/khvzak/bluez-tools/issues"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e"

DEPENDS = "dbus-glib glib-2.0 readline"

SRC_URI = "\
    git://github.com/khvzak/bluez-tools.git;protocol=https;branch=master \
    file://fix-memory-leaks.patch \
    file://obex-file-fix-null-check.patch \
"
SRCREV = "f65321736475429316f07ee94ec0deac8e46ec4a"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

RDEPENDS:${PN} = "bluez5"
