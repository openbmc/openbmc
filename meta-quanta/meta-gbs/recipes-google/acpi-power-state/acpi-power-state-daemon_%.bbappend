FILESEXTRAPATHS:prepend:gbs := "${THISDIR}/${PN}:"

SRC_URI:append:gbs = " \
    file://gbs-host-s0-set-failsafe.service \
    file://gbs-host-s5-set-failsafe.service \
    file://gbs-host-set-boot-failsafe@.service \
    file://gbs-check-host-state.service \
    file://gbs-set-boot-failsafe.sh \
    file://gbs-set-failsafe.sh \
    file://gbs-check-host-state.sh \
    file://gbs-host-ready.target \
    "

RDEPENDS:${PN}:append:gbs = "bash"

CHASSIS_INSTANCE="0"

SYSTEMD_SERVICE:${PN}:append:gbs = " \
    gbs-host-s0-set-failsafe.service \
    gbs-host-s5-set-failsafe.service \
    gbs-host-set-boot-failsafe@${CHASSIS_INSTANCE}.service \
    gbs-check-host-state.service \
    gbs-host-ready.target \
    "

FILES:${PN}:append:gbs = " \
    ${systemd_system_unitdir}/gbs-host-set-boot-failsafe@.service \
    "

do_install:append:gbs() {
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
