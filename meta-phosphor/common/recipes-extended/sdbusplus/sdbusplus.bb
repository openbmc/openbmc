SUMMARY = "C++ bindings for systemd dbus APIs"
DESCRIPTION = "C++ bindings for systemd dbus APIs."
HOMEPAGE = "http://github.com/openbmc/sdbusplus"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit autotools pkgconfig
inherit obmc-phosphor-python-autotools

DEPENDS += "autoconf-archive-native"
RDEPENDS_${PN} += " \
        python-inflection \
        python-mako \
        python-pyyaml \
        "

SRC_URI += "git://github.com/openbmc/sdbusplus"
SRCREV = "abd10b0875ff5842d6fad240cfa7cee8217dcacf"

PACKAGECONFIG ??= "libsdbusplus"
PACKAGECONFIG[libsdbusplus] = "--enable-libsdbusplus,--disable-libsdbusplus,systemd,libsystemd"

S = "${WORKDIR}/git"

PACKAGECONFIG_remove_class-native = "libsdbusplus"
PACKAGECONFIG_remove_class-nativesdk = "libsdbusplus"

BBCLASSEXTEND += "native nativesdk"
