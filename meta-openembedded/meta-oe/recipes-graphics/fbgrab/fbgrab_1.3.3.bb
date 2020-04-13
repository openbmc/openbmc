SUMMARY = "FBGrab is a framebuffer screenshot program"
HOMEPAGE = "https://github.com/GunnarMonell/fbgrab"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=ea5bed2f60d357618ca161ad539f7c0a"
SECTION = "console/utils"
DEPENDS = "libpng zlib"
SRC_URI = "git://github.com/GunnarMonell/fbgrab.git;protocol=https"

SRCREV = "b179e2a42b8a5d72516b9c8d91713c9025cf6044"
S = "${WORKDIR}/git"

inherit autotools-brokensep

do_configure_prepend() {
    sed -i 's|$(DESTDIR)/usr/man/|$(DESTDIR)${mandir}/|g' ${S}/Makefile
}
