SUMMARY = "Linux man-pages"
DESCRIPTION = "The Linux man-pages project documents the Linux kernel and C library interfaces that are employed by user programs"
SECTION = "console/utils"
HOMEPAGE = "http://www.kernel.org/pub/linux/docs/man-pages"
LICENSE = "GPL-2.0-or-later"

LIC_FILES_CHKSUM = "file://README;md5=92cd5ee2e0b35d782817e7e277b6ce4b"
SRC_URI = "${KERNELORG_MIRROR}/linux/docs/${BPN}/${BP}.tar.gz"

SRC_URI[sha256sum] = "ed615e9a31978833f59e7316667b9aeffbbdb0a92d1391f9c66fadc6e77d0da8"

inherit manpages

MAN_PKG = "${PN}"

PACKAGECONFIG ??= ""
PACKAGECONFIG[manpages] = ""

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
        oe_runmake install prefix=${prefix} DESTDIR=${D}
}

# Only deliveres man-pages so FILES:${PN} gets everything
FILES:${PN}-doc = ""
FILES:${PN} = "${mandir}/*"

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE:${PN} = "passwd.5 getspnam.3 crypt.3"
ALTERNATIVE_LINK_NAME[passwd.5] = "${mandir}/man5/passwd.5"
ALTERNATIVE_LINK_NAME[getspnam.3] = "${mandir}/man3/getspnam.3"
ALTERNATIVE_LINK_NAME[crypt.3] = "${mandir}/man3/crypt.3"
