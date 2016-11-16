SUMMARY = "Interface definitions for all obmc dbus interfaces"
DESCRIPTION = "Interface definitions for all obmc dbus interfaces"
HOMEPAGE = "http://github.com/openbmc/phosphor-dbus-interfaces"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit autotools pkgconfig
inherit obmc-phosphor-python-autotools

DEPENDS += "autoconf-archive-native"
DEPENDS += "phosphor-logging"
RDEPENDS_${PN} += " \
        python-inflection \
        python-mako \
        python-pyyaml \
        "

SRC_URI += "git://github.com/openbmc/phosphor-dbus-interfaces"
SRCREV = "719ac45c838c31542df13822c8287fda2ea1ce02"

S = "${WORKDIR}/git"

BBCLASSEXTEND += "native nativesdk"
