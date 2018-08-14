SUMMARY = "FBGrab is a framebuffer screenshot program"
HOMEPAGE = "http://fbgrab.monells.se/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=ea5bed2f60d357618ca161ad539f7c0a"
SECTION = "console/utils"
DEPENDS = "libpng zlib"
SRC_URI = "http://fbgrab.monells.se/${BP}.tar.gz"

inherit autotools-brokensep

SRC_URI[md5sum] = "7d8c24081c681dfbba21f2934c1ac656"
SRC_URI[sha256sum] = "5fab478cbf8731fbacefaa76236a8f8b38ccff920c53b3a8253bc35509fba8ed"

do_configure_prepend() {
    sed -i 's|$(DESTDIR)/usr/man/|$(DESTDIR)${mandir}/|g' ${S}/Makefile
}
