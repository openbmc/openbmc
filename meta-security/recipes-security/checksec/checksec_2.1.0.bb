SUMMARY = "Linux system security checks"
DESCRIPTION = "The checksec script is designed to test what standard Linux OS and PaX security features are being used."
SECTION = "security"
LICENSE = "BSD"
HOMEPAGE="https://github.com/slimm609/checksec.sh"

LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=93fddcca19f6c897871f9b5f9a035f4a"

SRCREV = "04582bad41589ad479ca8b1f0170ed317475b5a5"
SRC_URI = "git://github.com/slimm609/checksec.sh"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/checksec ${D}${bindir}
}

RDEPENDS_${PN} = "bash openssl-bin binutils"
