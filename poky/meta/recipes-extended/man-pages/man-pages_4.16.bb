SUMMARY = "Linux man-pages"
DESCRIPTION = "The Linux man-pages project documents the Linux kernel and C library interfaces that are employed by user programs"
SECTION = "console/utils"
HOMEPAGE = "http://www.kernel.org/pub/linux/docs/man-pages"
LICENSE = "GPLv2+"

LIC_FILES_CHKSUM = "file://README;md5=794f701617cc03fe50c53257660d8ec4"
SRC_URI = "${KERNELORG_MIRROR}/linux/docs/${BPN}/Archive/${BP}.tar.gz"

SRC_URI[md5sum] = "d1fb8ba312a1c15e0bfda911a98c5544"
SRC_URI[sha256sum] = "d38b0460bf3f35c95faf7f8cf52dac1216d86a47866f5e5f2fda88c61da04960"

inherit manpages

MAN_PKG = "${PN}"

PACKAGECONFIG ??= ""
PACKAGECONFIG[manpages] = ""

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
        oe_runmake install DESTDIR=${D}
}

# Only deliveres man-pages so FILES_${PN} gets everything
FILES_${PN}-doc = ""
FILES_${PN} = "${mandir}/*"

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_${PN} = "passwd.5 getspnam.3"
ALTERNATIVE_LINK_NAME[passwd.5] = "${mandir}/man5/passwd.5"
ALTERNATIVE_LINK_NAME[getspnam.3] = "${mandir}/man3/getspnam.3"
