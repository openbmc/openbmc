SUMMARY = "Checksec tool"
DESCRIPTION = "The checksec.sh script is designed to test what standard Linux OS and PaX security features are being used."
SECTION = "security"
LICENSE = "BSD-3-Clause"
HOMEPAGE="http://www.trapkit.de/tools/checksec.html"

LIC_FILES_CHKSUM = "file://checksec-${PV}.sh;beginline=3;endline=34;md5=6dab14470bfdf12634b866dbdd7a04b0"

SRC_URI = "http://www.trapkit.de/tools/checksec.sh;downloadfilename=checksec-${PV}.sh"

SRC_URI[md5sum] = "57cc3fbbbe48e8ebd4672c569954374d"
SRC_URI[sha256sum] = "05822cd8668589038d20650faa0e56f740911d8ad06f7005b3d12a5c76591b90"


S = "${WORKDIR}"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/checksec-${PV}.sh    ${D}${bindir}/checksec.sh
    sed -i 's/\r//' ${D}${bindir}/checksec.sh
}

RDEPENDS_${PN} = "bash binutils"

BBCLASSEXTEND = "native"
