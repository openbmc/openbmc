SUMMARY = "Thunderbolt user-space management tool"
DESCRIPTION = "Userspace system daemon to enable security levels for Thunderbolt on GNU/Linux"
HOMEPAGE = "https://gitlab.freedesktop.org/bolt/bolt"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "udev polkit dbus"
REQUIRED_DISTRO_FEATURES = "polkit"

SRC_URI = "git://gitlab.freedesktop.org/bolt/bolt.git;protocol=https;branch=master;tag=${PV}"
SRCREV = "9d1d66380353dab94c28466f34adb2ba6c7e4393"


CVE_PRODUCT = "freedesktop:bolt"

inherit cmake pkgconfig meson features_check

FILES:${PN} += "${datadir}/dbus-1/* \
                ${datadir}/polkit-1/* \
                ${systemd_system_unitdir} \
"
