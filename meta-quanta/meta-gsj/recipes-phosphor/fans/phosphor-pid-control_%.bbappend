FILESEXTRAPATHS_prepend_gsj := "${THISDIR}/${PN}:"
SRC_URI_append_gsj = " file://config-8ssd.json"
SRC_URI_append_gsj = " file://config-2ssd.json"
SRC_URI_append_gsj = " file://fan-control.sh"
SRC_URI_append_gsj = " file://fan-default-speed.sh"
SRC_URI_append_gsj = " file://phosphor-pid-control.service"
SRC_URI_append_gsj = " file://fan-reboot-control.service"
SRC_URI_append_gsj = " file://fan-boot-control.service"

FILES_${PN}_append_gsj = " ${datadir}/swampd/config-8ssd.json"
FILES_${PN}_append_gsj = " ${datadir}/swampd/config-2ssd.json"
FILES_${PN}_append_gsj = " ${bindir}/fan-control.sh"
FILES_${PN}_append_gsj = " ${bindir}/fan-default-speed.sh"

inherit systemd
RDEPENDS_${PN} += "bash"

SYSTEMD_SERVICE_${PN}_append_gsj = " phosphor-pid-control.service"
SYSTEMD_SERVICE_${PN}_append_gsj = " fan-reboot-control.service"
SYSTEMD_SERVICE_${PN}_append_gsj = " fan-boot-control.service"

do_install_append_gsj() {
    install -d ${D}/${bindir}
    install -m 0755 ${WORKDIR}/fan-control.sh ${D}/${bindir}
    install -m 0755 ${WORKDIR}/fan-default-speed.sh ${D}/${bindir}

    install -d ${D}${datadir}/swampd
    install -m 0644 -D ${WORKDIR}/config-8ssd.json \
        ${D}${datadir}/swampd/config-8ssd.json
    install -m 0644 -D ${WORKDIR}/config-2ssd.json \
        ${D}${datadir}/swampd/config-2ssd.json

    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/phosphor-pid-control.service \
        ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/fan-reboot-control.service \
        ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/fan-boot-control.service \
        ${D}${systemd_unitdir}/system
}
