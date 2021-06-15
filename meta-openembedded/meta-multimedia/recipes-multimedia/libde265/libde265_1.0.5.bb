DESCRIPTION = "libde265 is an open source implementation of the h.265 \
video codec. It is written from scratch and has a plain C API to enable a \
simple integration into other software."
HOMEPAGE = "http://www.libde265.org/"
SECTION = "libs/multimedia"

LICENSE = "LGPLv3 & MIT"
LICENSE_FLAGS = "commercial"
LIC_FILES_CHKSUM = "file://COPYING;md5=695b556799abb2435c97a113cdca512f"

SRC_URI = "https://github.com/strukturag/libde265/releases/download/v${PV}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "e3f277d8903408615a5cc34718b391b83c97c646faea4f41da93bac5ee08a87f"

EXTRA_OECONF = "--disable-sherlock265 --disable-dec265"

inherit autotools-brokensep pkgconfig

PACKAGES =+ "${PN}-tools"
FILES_${PN}-tools = "${bindir}/*"
