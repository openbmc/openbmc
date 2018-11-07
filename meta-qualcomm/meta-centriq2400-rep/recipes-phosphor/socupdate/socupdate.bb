SUMMARY = "Qualcomm SOC update"
DESCRIPTION = ""
HOMEPAGE = ""
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${QUALCOMMBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"


SRC_URI += "file://socupdate.sh"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/socupdate.sh ${D}${bindir}/socupdate.sh
}
