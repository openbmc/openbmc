SUMMARY = "Thunderbolt user-space management tool"
DESCRIPTION = "Userspace system daemon to enable security levels for Thunderbolt on GNU/Linux"
HOMEPAGE = "https://gitlab.freedesktop.org/bolt/bolt"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "udev polkit dbus"
REQUIRED_DISTRO_FEATURES = "polkit"

SRC_URI = "git://gitlab.freedesktop.org/bolt/bolt.git;protocol=https;branch=master"
SRCREV = "5a8a5866a847561566499847d46a97c612b4e6dd"

S = "${WORKDIR}/git"

CVE_CHECK_SKIP_RECIPE = "${PN}"

inherit cmake pkgconfig meson features_check

FILES:${PN} += "${datadir}/dbus-1/* \
                ${datadir}/polkit-1/* \
               "
