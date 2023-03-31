SUMMARY = "a collection of freely re-usable Autoconf macros"
HOMEPAGE = "http://www.gnu.org/software/autoconf-archive/"
SECTION = "devel"
LICENSE = "GPL-3.0-with-autoconf-exception"
LIC_FILES_CHKSUM = "file://COPYING;md5=11cc2d3ee574f9d6b7ee797bdce4d423 \
    file://COPYING.EXCEPTION;md5=fdef168ebff3bc2f13664c365a5fb515"

SRC_URI = "${GNU_MIRROR}/${BPN}/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "71d4048479ae28f1f5794619c3d72df9c01df49b1c628ef85fde37596dc31a33"

inherit autotools allarch texinfo

PACKAGES = "${PN} ${PN}-doc"

FILES:${PN} += "${datadir}/aclocal"

BBCLASSEXTEND = "native nativesdk"
