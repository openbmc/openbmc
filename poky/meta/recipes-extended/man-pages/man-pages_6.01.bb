SUMMARY = "Linux man-pages"
DESCRIPTION = "The Linux man-pages project documents the Linux kernel and C library interfaces that are employed by user programs"
SECTION = "console/utils"
HOMEPAGE = "http://www.kernel.org/pub/linux/docs/man-pages"
LICENSE = "GPL-2.0-or-later"

LIC_FILES_CHKSUM = "file://README;md5=bf1faa9b0245e39a7c0c9690ffdf6e54"
SRC_URI = "${KERNELORG_MIRROR}/linux/docs/${BPN}/${BP}.tar.gz"

SRC_URI[sha256sum] = "c1e8bea88589f1a80b67dafd82fb98b018ddd7f7bfa594f8f79686258d26e784"

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
ALTERNATIVE:${PN} = "crypt.3 crypt_r.3 getspnam.3 passwd.5"
ALTERNATIVE_LINK_NAME[crypt.3] = "${mandir}/man3/crypt.3"
ALTERNATIVE_LINK_NAME[crypt_r.3] = "${mandir}/man3/crypt_r.3"
ALTERNATIVE_LINK_NAME[getspnam.3] = "${mandir}/man3/getspnam.3"
ALTERNATIVE_LINK_NAME[passwd.5] = "${mandir}/man5/passwd.5"
