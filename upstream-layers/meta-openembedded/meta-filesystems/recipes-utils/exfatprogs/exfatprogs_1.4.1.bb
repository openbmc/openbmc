SUMMARY = "exFAT filesystem userspace utilities"
DESCRIPTION = "\
As new exfat filesystem is merged into linux-5.7 kernel, exfatprogs is \
created as an official userspace utilities that contain all of the standard \
utilities for creating and fixing and debugging exfat filesystem in linux \
system. The goal of exfatprogs is to provide high performance and quality \
at the level of exfat utilities in windows. And this software is licensed \
under the GNU General Public License Version 2."
HOMEPAGE = "https://github.com/${BPN}/${BPN}"
SECTION = "universe/otherosfs"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/${PV}/${BP}.tar.xz \
           file://run-ptest"
SRC_URI[sha256sum] = "85c133e8802cbc1191bff2477a67b376192dfb9f94bb254c05dbae79fd958f2e"

UPSTREAM_CHECK_URI = "https://github.com/${BPN}/${BPN}/releases"
UPSTREAM_CHECK_REGEX = "${BPN}-(?P<pver>\d+(\.\d+)+)"

inherit autotools ptest pkgconfig

DEPENDS += "util-linux"

RPROVIDES:${PN} = "exfat-utils"
RCONFLICTS:${PN} = "exfat-utils"
RREPLACES:${PN} = "exfat-utils"
RDEPENDS:${PN}-ptest += "bash xz"

do_install_ptest(){
    cp -r ${S}/tests ${D}${PTEST_PATH}
    sed -i "s,Passed,PASS:," ${D}${PTEST_PATH}/tests/test_fsck.sh
    sed -i "s,Failed,FAIL:," ${D}${PTEST_PATH}/tests/test_fsck.sh
}
