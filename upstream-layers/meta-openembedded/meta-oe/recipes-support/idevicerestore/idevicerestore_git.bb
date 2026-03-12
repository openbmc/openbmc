SUMMARY = "A command-line application to restore firmware files to iOS devices"
LICENSE = "LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=e6a600fd5e1d9cbde2d983680233ad02 \
"

HOMEPAGE = "http://www.libimobiledevice.org/"

DEPENDS = "libirecovery libimobiledevice libzip curl libimobiledevice-glue openssl libtatsu"

PV = "1.0.1+git"

SRCREV = "a0cec3b34fb112168aaae9fbb024e15302563b34"
SRC_URI = "git://github.com/libimobiledevice/idevicerestore;protocol=https;branch=master"

inherit autotools pkgconfig
