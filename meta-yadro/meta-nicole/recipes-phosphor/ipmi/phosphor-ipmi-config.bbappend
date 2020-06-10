FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

inherit systemd

RDEPENDS_${PN} = "${VIRTUAL-RUNTIME_base-utils}"
SYSTEMD_SERVICE_${PN} = "${BPN}.service"
SRC_URI_append = " \
    file://${BPN}-set-device-id.sh \
    file://${BPN}.service \
"

S = "${WORKDIR}"
do_install_append() {
    install -d ${D}${bindir} ${D}${systemd_system_unitdir}
    install ${BPN}-set-device-id.sh ${D}${bindir}/
    install -m 0644 ${BPN}.service ${D}${systemd_system_unitdir}/
}

FILES_${PN}_append = " \
    ${bindir}/${BPN}-set-device-id.sh \
"
