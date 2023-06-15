SUMMARY = "Linux man-pages"
DESCRIPTION = "The Linux man-pages project documents the Linux kernel and C library interfaces that are employed by user programs"
SECTION = "console/utils"
HOMEPAGE = "http://www.kernel.org/pub/linux/docs/man-pages"
LICENSE = "GPL-2.0-or-later & GPL-2.0-only & GPL-1.0-or-later & BSD-2-Clause & BSD-3-Clause & BSD-4-Clause & MIT"

LIC_FILES_CHKSUM = "file://README;md5=5b7d7488344f5af8841dc13aaec49cdf \
                    file://LICENSES/BSD-2-Clause.txt;md5=d0f280d1058e77e66264a9b9e10e6c89 \
                    file://LICENSES/BSD-3-Clause.txt;md5=71f739ef75581cae312e8c711bcdab16 \
                    file://LICENSES/BSD-4-Clause-UC.txt;md5=1da3cf8ad50cd8d5d1de3cfc53196d01 \
                    file://LICENSES/GPL-1.0-or-later.txt;md5=e5b7c80002ef72ab868b43ce47b65125 \
                    file://LICENSES/GPL-2.0-only.txt;md5=3d26203303a722dedc6bf909d95ba815 \
                    file://LICENSES/GPL-2.0-or-later.txt;md5=3d26203303a722dedc6bf909d95ba815 \
                    file://LICENSES/Linux-man-pages-copyleft.txt;md5=173b960c686ff2d26f043ddaeb63f6ce \
                    file://LICENSES/MIT.txt;md5=7dda4e90ded66ab88b86f76169f28663 \
                    "
SRC_URI = "${KERNELORG_MIRROR}/linux/docs/${BPN}/${BP}.tar.gz"

SRC_URI[sha256sum] = "590623b99bf1f8ee958483c35cc0aaef2363e42998c4d927d1f705890d15d51e"

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
