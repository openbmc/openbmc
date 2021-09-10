SUMMARY = "C/C++ Configuration File Library"
DESCRIPTION = "Library for manipulating structured configuration files"
HOMEPAGE = "https://hyperrealm.github.io/libconfig/"
BUGTRACKER = "https://github.com/hyperrealm/libconfig/issues"
SECTION = "libs"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=fad9b3332be894bab9bc501572864b29"

SRC_URI = "https://hyperrealm.github.io/libconfig/dist/libconfig-${PV}.tar.gz"

UPSTREAM_CHECK_URI = "https://github.com/hyperrealm/libconfig/releases"
UPSTREAM_CHECK_REGEX = "Version (?P<pver>\d+(\.\d+)+)"

inherit autotools-brokensep pkgconfig

SRC_URI[md5sum] = "15ec701205f91f21b1187f8b61e0d64f"
SRC_URI[sha256sum] = "545166d6cac037744381d1e9cc5a5405094e7bfad16a411699bcff40bbb31ee7"

PACKAGE_BEFORE_PN = "${PN}++"
FILES:${PN}++ = "${libdir}/${BPN}++*${SOLIBS}"
