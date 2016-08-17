SUMMARY = "FBGrab is a framebuffer screenshot program"
HOMEPAGE = "http://fbgrab.monells.se/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=ea5bed2f60d357618ca161ad539f7c0a"
SECTION = "console/utils"
DEPENDS = "libpng zlib"
SRC_URI = "http://fbgrab.monells.se/${BP}.tar.gz"

inherit autotools-brokensep

S = "${WORKDIR}/${BPN}"

SRC_URI[md5sum] = "15b432735d6efd0373722bb96577b945"
SRC_URI[sha256sum] = "61e0772ad6ea744ee597ae478398ddd0ba6fceee2cf343859bebde2c15bafb91"

do_configure_prepend() {
    sed -i 's|$(DESTDIR)/usr/man/|$(DESTDIR)${mandir}/|g' ${S}/Makefile
}
