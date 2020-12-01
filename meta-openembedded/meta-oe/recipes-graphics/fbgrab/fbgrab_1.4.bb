SUMMARY = "FBGrab is a framebuffer screenshot program"
HOMEPAGE = "https://github.com/GunnarMonell/fbgrab"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=ea5bed2f60d357618ca161ad539f7c0a"
SECTION = "console/utils"
DEPENDS = "libpng zlib"
SRC_URI = "git://github.com/GunnarMonell/fbgrab.git;protocol=https"

SRCREV = "74373aafc0b496e67642562d86eac6b858a31f24"
S = "${WORKDIR}/git"

inherit autotools-brokensep

do_configure_prepend() {
    sed -i 's|$(DESTDIR)/usr/man/|$(DESTDIR)${mandir}/|g' ${S}/Makefile
}
