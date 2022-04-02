SUMMARY = "A command-line application to restore firmware files to iOS devices"
LICENSE = "LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=e6a600fd5e1d9cbde2d983680233ad02 \
"

HOMEPAGE = "http://www.libimobiledevice.org/"

DEPENDS = "libirecovery libimobiledevice libzip curl"

PV = "1.0.1+git${SRCPV}"

SRCREV = "280575bb95977241e240ed081a2602d68746443e"
SRC_URI = "git://github.com/libimobiledevice/idevicerestore;protocol=https;branch=master"

S = "${WORKDIR}/git"
inherit autotools pkgconfig
