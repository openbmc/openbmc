SUMMARY = "Program radominization"
DESCRIPTION = "The checksec.sh script is designed to test what standard Linux OS and PaX security features are being used."
SECTION = "security"
LICENSE = "BSD"
HOMEPAGE="http://www.trapkit.de/tools/checksec.html"

LIC_FILES_CHKSUM = "file://checksec.sh;md5=075996be339ab16ad7b94d6de3ee07bd"

SRC_URI = "file://checksec.sh"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/checksec.sh    ${D}${bindir}
}

RDEPENDS_${PN} = "bash"
