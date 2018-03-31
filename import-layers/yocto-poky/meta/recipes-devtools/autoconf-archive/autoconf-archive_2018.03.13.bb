SUMMARY = "a collection of freely re-usable Autoconf macros"
HOMEPAGE = "http://www.gnu.org/software/autoconf-archive/"
SECTION = "devel"
LICENSE = "GPL-3.0-with-autoconf-exception"
LIC_FILES_CHKSUM = "file://COPYING;md5=11cc2d3ee574f9d6b7ee797bdce4d423 \
    file://COPYING.EXCEPTION;md5=fdef168ebff3bc2f13664c365a5fb515"

SRC_URI = "${GNU_MIRROR}/${BPN}/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "46b13a5936372297b6d49980327a3c35"
SRC_URI[sha256sum] = "6175f90d9fa64c4d939bdbb3e8511ae0ee2134863a2c7bf8d9733819efa6e159"

inherit autotools allarch

PACKAGES = "${PN} ${PN}-doc"

FILES_${PN} += "${datadir}/aclocal"

BBCLASSEXTEND = "native nativesdk"
