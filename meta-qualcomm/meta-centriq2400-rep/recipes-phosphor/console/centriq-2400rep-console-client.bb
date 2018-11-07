FILESEXTRAPATHS_prepend_centriq2400-rep := "${THISDIR}/${PN}:"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${QUALCOMMBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

SRC_URI += "file://centriq-2400rep-console-client.sh"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/centriq-2400rep-console-client.sh ${D}${bindir}/centriq-2400rep-console-client.sh
}

