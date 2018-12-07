SUMMARY = "HXT SOC update"
DESCRIPTION = "A sample script to help update the host(soc) firmware"
HOMEPAGE = ""
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${HXTBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

SRC_URI += "file://socupdate.sh"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/socupdate.sh ${D}${bindir}/socupdate.sh
}
