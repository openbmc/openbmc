FILESEXTRAPATHS_prepend_gbs := "${THISDIR}/${PN}:"

SRC_URI_append_gbs = " \
    file://gbs-host-s0-set-failsafe.service \
    file://gbs-host-s5-set-failsafe.service \
    file://gbs-host-set-boot-failsafe@.service \
    file://gbs-check-host-state.service \
    file://gbs-set-boot-failsafe.sh \
    file://gbs-set-failsafe.sh \
    file://gbs-check-host-state.sh \
    file://gbs-host-ready.target \
    "

RDEPENDS_${PN}_append_gbs = "bash"

CHASSIS_INSTANCE="0"

SYSTEMD_SERVICE_${PN}_append_gbs = " \
    gbs-host-s0-set-failsafe.service \
    gbs-host-s5-set-failsafe.service \
    gbs-host-set-boot-failsafe@${CHASSIS_INSTANCE}.service \
    gbs-check-host-state.service \
    gbs-host-ready.target \
    "

FILES_${PN}_append_gbs = " \
    ${systemd_system_unitdir}/gbs-host-set-boot-failsafe@.service \
    "

do_install_append_gbs() {
    install -d ${D}${bindir}

    install -m 0755 ${WORKDIR}/gbs-set-failsafe.sh ${D}${bindir}/.
    install -m 0755 ${WORKDIR}/gbs-set-boot-failsafe.sh ${D}${bindir}/.
    install -m 0755 ${WORKDIR}/gbs-check-host-state.sh ${D}${bindir}/.

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/gbs-host-s0-set-failsafe.service ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/gbs-host-s5-set-failsafe.service ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/gbs-host-set-boot-failsafe@.service ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/gbs-check-host-state.service ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/gbs-host-ready.target ${D}${systemd_system_unitdir}
}
