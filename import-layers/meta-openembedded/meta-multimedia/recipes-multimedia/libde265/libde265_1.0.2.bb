DESCRIPTION = "libde265 is an open source implementation of the h.265 \
video codec. It is written from scratch and has a plain C API to enable a \
simple integration into other software."
HOMEPAGE = "http://www.libde265.org/"
SECTION = "libs/multimedia"

LICENSE = "LGPLv3"
LICENSE_FLAGS = "commercial"
LIC_FILES_CHKSUM = "file://COPYING;md5=852f345c1c52c9160f9a7c36bb997546"

SRC_URI = "https://github.com/strukturag/libde265/releases/download/v${PV}/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "93520b378df25f3a94e962f2b54872cc"
SRC_URI[sha256sum] = "eaa0348839c2935dd90647d72c6dd4a043e36361cb3c33d2b04df10fbcebd3cb"

EXTRA_OECONF = "--disable-sherlock265 --disable-dec265"

inherit autotools-brokensep pkgconfig

PACKAGES =+ "${PN}-tools"
FILES_${PN}-tools = "${bindir}/*"
