SUMMARY = "Linux system security checks"
DESCRIPTION = "The checksec script is designed to test what standard Linux OS and PaX security features are being used."
SECTION = "security"
LICENSE = "BSD"
HOMEPAGE="https://github.com/slimm609/checksec.sh"

LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=93fddcca19f6c897871f9b5f9a035f4a"

SRCREV = "3c15cb89641c700096fdec0c1904a0cf9b83c5e2"
SRC_URI = "git://github.com/slimm609/checksec.sh"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/checksec ${D}${bindir}
}

RDEPENDS_${PN} = "bash openssl-bin"
