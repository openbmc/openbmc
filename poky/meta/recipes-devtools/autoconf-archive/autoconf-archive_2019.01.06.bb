SUMMARY = "a collection of freely re-usable Autoconf macros"
HOMEPAGE = "http://www.gnu.org/software/autoconf-archive/"
SECTION = "devel"
LICENSE = "GPL-3.0-with-autoconf-exception"
LIC_FILES_CHKSUM = "file://COPYING;md5=11cc2d3ee574f9d6b7ee797bdce4d423 \
    file://COPYING.EXCEPTION;md5=fdef168ebff3bc2f13664c365a5fb515"

SRC_URI = "${GNU_MIRROR}/${BPN}/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "d46413c8b00a125b1529bae385bbec55"
SRC_URI[sha256sum] = "17195c833098da79de5778ee90948f4c5d90ed1a0cf8391b4ab348e2ec511e3f"

inherit autotools allarch

PACKAGES = "${PN} ${PN}-doc"

FILES_${PN} += "${datadir}/aclocal"

BBCLASSEXTEND = "native nativesdk"
