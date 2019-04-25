SUMMARY = "A library for Microsoft compression formats"
HOMEPAGE = "http://www.cabextract.org.uk/libmspack/"
SECTION = "lib"
LICENSE = "LGPL-2.1"
DEPENDS = ""

LIC_FILES_CHKSUM = "file://COPYING.LIB;beginline=1;endline=2;md5=5b1fd1f66ef926b3c8a5bb00a72a28dd"

SRC_URI = "${DEBIAN_MIRROR}/main/libm/${BPN}/${BPN}_${PV}.orig.tar.xz"

SRC_URI[md5sum] = "d894d91eba4d2c6f76695fc9566d5387"
SRC_URI[sha256sum] = "850c57442b850bf1bc0fc4ea8880903ebf2bed063c3c80782ee4626fbcb0e67d"

inherit autotools

S = "${WORKDIR}/${BP}alpha"
