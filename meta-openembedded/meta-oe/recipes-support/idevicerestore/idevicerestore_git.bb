SUMMARY = "A command-line application to restore firmware files to iOS devices"
LICENSE = "LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=e6a600fd5e1d9cbde2d983680233ad02 \
"

HOMEPAGE = "http://www.libimobiledevice.org/"

DEPENDS = "libirecovery libimobiledevice libzip curl libimobiledevice-glue openssl"

PV = "1.0.1+git${SRCPV}"

SRCREV = "7d622d916be16f2df5a72bf53a42f3a326bbfaa4"
SRC_URI = "git://github.com/libimobiledevice/idevicerestore;protocol=https;branch=master"

S = "${WORKDIR}/git"
inherit autotools pkgconfig
