SUMMARY = "Phosphor fan monitor definition default data"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit phosphor-fan

SRC_URI += "file://monitor.yaml"

S = "${WORKDIR}"

do_install() {
    DEST=${D}${monitor_datadir}
    install -D monitor.yaml ${DEST}/monitor.yaml
}
