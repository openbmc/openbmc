SUMMARY = "SBC Audio Codec"
DESCRIPTION = "Bluetooth low-complexity, subband codec (SBC) library."
HOMEPAGE = "https://www.bluez.org"
SECTION = "libs"
LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later"
LICENSE:${PN} = "LGPL-2.1-or-later"
LICENSE:${PN}-examples = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e \
                    file://COPYING.LIB;md5=fb504b67c50331fc78734fed90fb0e09 \
                    file://src/sbcenc.c;beginline=1;endline=24;md5=08e7a70b127f4100ff2cd7d629147d8d \
                    file://sbc/sbc.h;beginline=1;endline=26;md5=0f57d0df22b0d40746bdd29805a4361b"

DEPENDS = "libsndfile1"

SRC_URI = "${KERNELORG_MIRROR}/linux/bluetooth/${BP}.tar.xz \
           file://0001-sbc_primitives-Fix-build-on-non-x86.patch \
           "

SRC_URI[sha256sum] = "0cbad69823a99e8421fe0700e8cf9eeb8fa0c1ad28e8dbc2182b3353507931d2"

inherit autotools pkgconfig

PACKAGES =+ "${PN}-examples"
FILES:${PN}-examples += "${bindir}/*"
