FILESEXTRAPATHS_prepend_stardragon4800-rep2 := "${THISDIR}/${PN}:"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${HXTBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

SRC_URI += "file://stardragon4800-rep2-console-client.sh"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/stardragon4800-rep2-console-client.sh ${D}${bindir}/stardragon4800-rep2-console-client.sh
}
