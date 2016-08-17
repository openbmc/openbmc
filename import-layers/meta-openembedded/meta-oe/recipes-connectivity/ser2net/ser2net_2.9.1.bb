SUMMARY = "A serial to network proxy"
SECTION = "console/network"
HOMEPAGE = "http://sourceforge.net/projects/ser2net/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=bae3019b4c6dc4138c217864bd04331f"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/ser2net/ser2net/ser2net-${PV}.tar.gz"

SRC_URI[md5sum] = "80011ac0e60bbdcb65f1d7a86251e3f3"
SRC_URI[sha256sum] = "fdee1e69903cf409bdc6f32403a566cbc6006aa9e2a4d6f8f12b90dfd5ca0d0e"

inherit autotools pkgconfig

BBCLASSEXTEND += "native nativesdk"
