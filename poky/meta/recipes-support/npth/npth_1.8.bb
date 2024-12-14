SUMMARY = "New GNU Portable Threads library"
DESCRIPTION = "nPth is a library to provide the GNU Pth API and thus a non-preemptive threads implementation. "
HOMEPAGE = "https://gnupg.org/software/npth/"
SECTION = "libs"
LICENSE = "LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=2caced0b25dfefd4c601d92bd15116de"
UPSTREAM_CHECK_URI = "https://gnupg.org/download/index.html"

SRC_URI = "${GNUPG_MIRROR}/npth/npth-${PV}.tar.bz2"

SRC_URI[sha256sum] = "8bd24b4f23a3065d6e5b26e98aba9ce783ea4fd781069c1b35d149694e90ca3e"

inherit autotools multilib_header

do_install:append() {
    oe_multilib_header npth.h
}

BBCLASSEXTEND = "native nativesdk"
