SUMMARY = "MBW determines the copy memory bandwidth available to userspace programs"
HOMEPAGE = "http://github.com/raas/mbw"
SECTION = "console/tests"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://mbw.spec;beginline=1;endline=10;md5=34d71e08e6337a8411d82d7dd0c54fe2"

SRC_URI = "git://github.com/raas/${BPN}.git;branch=master;protocol=https"

SRCREV = "d2cd3d36c353fee578f752c4e65a8c1efcee002c"

PV = "1.5"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/mbw ${D}${bindir}
}
