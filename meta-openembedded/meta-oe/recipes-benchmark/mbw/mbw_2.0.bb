SUMMARY = "MBW determines the copy memory bandwidth available to userspace programs"
HOMEPAGE = "http://github.com/raas/mbw"
SECTION = "console/tests"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://mbw.spec;beginline=1;endline=10;md5=9ead6bd5826678fcfe229a3cf7257c56"

SRC_URI = "git://github.com/raas/${BPN}.git;branch=master;protocol=https"

SRCREV = "c3155b544a5065e8235508059c6512af6c46bd4d"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/mbw ${D}${bindir}
}
