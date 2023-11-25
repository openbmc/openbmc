DESCRIPTION = "libde265 is an open source implementation of the h.265 \
video codec. It is written from scratch and has a plain C API to enable a \
simple integration into other software."
HOMEPAGE = "http://www.libde265.org/"
SECTION = "libs/multimedia"

LICENSE = "LGPL-3.0-only & MIT"
LICENSE_FLAGS = "commercial"
LIC_FILES_CHKSUM = "file://COPYING;md5=695b556799abb2435c97a113cdca512f"

SRC_URI = "git://github.com/strukturag/libde265.git;branch=master;protocol=https"
SRCREV = "a267c84707ab264928fa9b86de2ee749c48c318c"

S = "${WORKDIR}/git"

EXTRA_OECONF = "--disable-sherlock265 --disable-dec265"

inherit autotools-brokensep pkgconfig

PACKAGES =+ "${PN}-tools"
FILES:${PN}-tools = "${bindir}/*"
