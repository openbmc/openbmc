SUMMARY = "Linux system security checks"
DESCRIPTION = "The checksec script is designed to test what standard Linux OS and PaX security features are being used."
SECTION = "security"
LICENSE = "BSD"
HOMEPAGE="https://github.com/slimm609/checksec.sh"

LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=8d90285f711cf1f378e2c024457066d8"

SRCREV = "c3754e45e04f9104db93b2048afd094427102d48"
SRC_URI = "git://github.com/slimm609/checksec.sh"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/checksec ${D}${bindir}
}

RDEPENDS_${PN} = "bash openssl-bin binutils"
