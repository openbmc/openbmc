SUMMARY = "Linux system security checks"
DESCRIPTION = "The checksec script is designed to test what standard Linux OS and PaX security features are being used."
SECTION = "security"
LICENSE = "BSD-3-Clause"
HOMEPAGE="https://github.com/slimm609/checksec.sh"

LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=879b2147c754bc040c29e9c3b84da836"

SRCREV = "2753ebb89fcdc96433ae8a4c4e5a49214a845be2"
SRC_URI = "git://github.com/slimm609/checksec.sh;branch=main;protocol=https"

S = "${UNPACKDIR}/git"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/checksec ${D}${bindir}
}

RDEPENDS:${PN} = "bash openssl-bin binutils findutils file procps"

BBCLASSEXTEND = "native"
