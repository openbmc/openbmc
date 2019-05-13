SUMMARY = "Phosphor fan monitor definition default data"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit allarch
inherit phosphor-fan

S = "${WORKDIR}"

SRC_URI = "file://monitor.yaml"

do_install() {
    DEST=${D}${monitor_datadir}
    install -D monitor.yaml ${D}${monitor_datadir}/monitor.yaml
}

FILES_${PN} += "${monitor_datadir}/monitor.yaml"
