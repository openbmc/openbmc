SUMMARY = "SBC Audio Codec"
DESCRIPTION = "Bluetooth low-complexity, subband codec (SBC) library."
HOMEPAGE = "https://www.bluez.org"
SECTION = "libs"
LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later"
LICENSE:${PN} = "LGPL-2.1-or-later"
LICENSE:${PN}-examples = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e \
                    file://COPYING.LIB;md5=fb504b67c50331fc78734fed90fb0e09 \
                    file://src/sbcenc.c;beginline=1;endline=8;md5=cfac6012e2dea914de8b3b7693591622 \
                    file://sbc/sbc.h;beginline=1;endline=10;md5=bbf642bd99160e4f2a5bbd5d174a8320 \
                    "

DEPENDS = "libsndfile1"

SRC_URI = "${KERNELORG_MIRROR}/linux/bluetooth/${BP}.tar.xz"

SRC_URI[sha256sum] = "a1ada76ef35e5af9c2fbd063754dc9e37a8d989417c6eb1ecebb089b1383ae9e"

inherit autotools pkgconfig

CFLAGS += "-std=gnu17"

PACKAGES =+ "${PN}-examples"
FILES:${PN}-examples += "${bindir}/*"
