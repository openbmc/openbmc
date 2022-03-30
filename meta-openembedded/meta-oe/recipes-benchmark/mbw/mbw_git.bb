SUMMARY = "MBW determines the copy memory bandwidth available to userspace programs"
HOMEPAGE = "http://github.com/raas/mbw"
SECTION = "console/tests"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://mbw.spec;beginline=1;endline=10;md5=bbb77813272134a5c461f71abe945bef"

SRC_URI = "git://github.com/raas/${BPN}.git;branch=master;protocol=https"

SRCREV = "2a15026ff65160127204881263464b1740a57198"

PV = "1.4+git${SRCPV}"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/mbw ${D}${bindir}
}
