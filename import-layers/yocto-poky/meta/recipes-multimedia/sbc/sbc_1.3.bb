SUMMARY = "SBC Audio Codec"
DESCRIPTION = "Bluetooth low-complexity, subband codec (SBC) library."
HOMEPAGE = "https://www.bluez.org"
SECTION = "libs"
LICENSE = "GPLv2+ & LGPLv2.1+"
LICENSE_${PN} = "LGPLv2.1+"
LICENSE_${PN}-examples = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e \
                    file://COPYING.LIB;md5=fb504b67c50331fc78734fed90fb0e09 \
                    file://src/sbcenc.c;beginline=1;endline=24;md5=08e7a70b127f4100ff2cd7d629147d8d \
                    file://sbc/sbc.h;beginline=1;endline=26;md5=0f57d0df22b0d40746bdd29805a4361b"

DEPENDS = "libsndfile1"

SRC_URI = "${KERNELORG_MIRROR}/linux/bluetooth/${BP}.tar.xz"

SRC_URI[md5sum] = "2d8b7841f2c11ab287718d562f2b981c"
SRC_URI[sha256sum] = "e61022cf576f14190241e7071753fdacdce5d1dea89ffd704110fc50be689309"

inherit autotools pkgconfig

PACKAGES =+ "${PN}-examples"
FILES_${PN}-examples += "${bindir}/*"
