SUMMARY = "a collection of freely re-usable Autoconf macros"
HOMEPAGE = "http://www.gnu.org/software/autoconf-archive/"
SECTION = "devel"
LICENSE = "GPL-3.0-with-autoconf-exception"
LIC_FILES_CHKSUM = "file://COPYING;md5=11cc2d3ee574f9d6b7ee797bdce4d423 \
    file://COPYING.EXCEPTION;md5=fdef168ebff3bc2f13664c365a5fb515"

SRC_URI = "${GNU_MIRROR}/${BPN}/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "e07454f00d8cae7907bed42d0747798927809947684d94c37207a4d63a32f423"

inherit autotools allarch texinfo

PACKAGES = "${PN} ${PN}-doc"

FILES:${PN} += "${datadir}/aclocal"

BBCLASSEXTEND = "native nativesdk"
