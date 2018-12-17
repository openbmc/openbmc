SUMMARY = "C/C++ Configuration File Library"
DESCRIPTION = "Library for manipulating structured configuration files"
HOMEPAGE = "https://hyperrealm.github.io/libconfig/"
BUGTRACKER = "https://github.com/hyperrealm/libconfig/issues"
SECTION = "libs"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=fad9b3332be894bab9bc501572864b29"

SRC_URI = "https://hyperrealm.github.io/libconfig/dist/libconfig-${PV}.tar.gz"

inherit autotools-brokensep pkgconfig

SRC_URI[md5sum] = "6bd98ee3a6e6b9126c82c916d7a9e690"
SRC_URI[sha256sum] = "7c3c7a9c73ff3302084386e96f903eb62ce06953bb1666235fac74363a16fad9"

FILES_${PN}_append = " \
    ${libdir}/cmake \
"
