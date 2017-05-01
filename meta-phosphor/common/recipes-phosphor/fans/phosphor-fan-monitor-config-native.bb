SUMMARY = "Phosphor fan monitor definition default data"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-fan

FILES_${PN} += "phosphor-fan-monitor-config"

SRC_URI += "file://monitor.yaml"

S = "${WORKDIR}"

do_install() {
    DEST=${D}${monitor_datadir}
    install -D monitor.yaml ${DEST}/monitor.yaml
}
