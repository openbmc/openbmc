SUMMARY = "Power sequencer data definition"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${IBMBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit allarch

SRC_URI = "file://ucd90160.yaml"

FILES_${PN} += "${datadir}/power-sequencer/ucd90160.yaml"

S = "${WORKDIR}"

do_install() {
    DEST=${D}${datadir}/power-sequencer

    install -D ucd90160.yaml ${DEST}/ucd90160.yaml
}
