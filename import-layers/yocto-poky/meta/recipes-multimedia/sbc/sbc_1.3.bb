SUMMARY = "SBC Audio Codec"
DESCRIPTION = "Bluetooth low-complexity, subband codec (SBC) library."
HOMEPAGE = "https://www.bluez.org"
SECTION = "libs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e \
                    file://sbc/sbc.h;beginline=1;endline=26;md5=0f57d0df22b0d40746bdd29805a4361b"

DEPENDS = "libsndfile1"

SRC_URI = "${KERNELORG_MIRROR}/linux/bluetooth/${BP}.tar.xz"

SRC_URI[md5sum] = "2d8b7841f2c11ab287718d562f2b981c"
SRC_URI[sha256sum] = "e61022cf576f14190241e7071753fdacdce5d1dea89ffd704110fc50be689309"

inherit autotools pkgconfig
