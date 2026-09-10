SUMMARY = "C++ bindings for Cairo graphics library"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4bf661c1e3793e55c8d1051bc5e0ae21"

inherit gnomebase

DEPENDS += "boost cairo libsigc++-3"

SRC_URI = "https://www.cairographics.org/releases/cairomm-${PV}.tar.xz"
SRC_URI[sha256sum] = "06881a0167d84962c74db318427c5be663d30b8ea5c60740a9dc36a9c1961b54"

S = "${UNPACKDIR}/cairomm-${PV}"

FILES:${PN}-doc += "${datadir}/devhelp"
FILES:${PN}-dev += "${libdir}/cairomm-*/"

