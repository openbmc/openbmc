FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit systemd

SRC_URI:append = " \
    file://phosphor-multi-gpio-monitor.json \
    file://reset_btn \
    file://reset_btn@.service \
    file://assert-host-ready.service \
    file://deassert-host-ready.service \
    file://assert-power-good-drop \
    file://assert-power-good-drop.service \
    file://deassert-power-good-drop \
    file://deassert-power-good-drop.service \
    file://thermal-event-logger \
    file://thermal-assert-log@.service \
    file://thermal-deassert-log@.service \
    file://vr-fault-assert-log@.service \
    file://vr-fault-deassert-log@.service \
    file://vr-fault-event-logger \
    file://smc-assert-log@.service \
    file://smc-deassert-log@.service \
    file://smc-event-logger \
    file://power-rail-assert-log@.service \
    file://power-rail-deassert-log@.service \
    file://power-rail-event-logger \
    file://uart-select-led@.service \
    file://set-uart-select-led \
    file://rainier-gpio-handler \
    file://rainier-gpio-handler@.service \
    "

RDEPENDS:${PN}:append = " bash"

FILES:${PN} += "${systemd_system_unitdir}/*"

SYSTEMD_SERVICE:${PN} += " \
    reset_btn@.service \
    assert-host-ready.service \
    deassert-host-ready.service \
    assert-power-good-drop.service \
    deassert-power-good-drop.service \
    thermal-assert-log@.service \
    thermal-deassert-log@.service \
    vr-fault-assert-log@.service \
    vr-fault-deassert-log@.service \
    smc-assert-log@.service \
    smc-deassert-log@.service \
    power-rail-assert-log@.service \
    power-rail-deassert-log@.service \
    uart-select-led@.service \
    rainier-gpio-handler@.service \
    "

do_install:append() {
    install -d ${D}${datadir}/${PN}
    install -m 0644 ${UNPACKDIR}/phosphor-multi-gpio-monitor.json \
                    ${D}${datadir}/${PN}/phosphor-multi-gpio-monitor.json

    install -d ${D}${systemd_system_unitdir}/
    install -m 0644 ${UNPACKDIR}/*.service ${D}${systemd_system_unitdir}/

    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/reset_btn ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/assert-power-good-drop ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/deassert-power-good-drop ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/thermal-event-logger ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/vr-fault-event-logger ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/smc-event-logger ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/power-rail-event-logger ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/set-uart-select-led ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/rainier-gpio-handler ${D}${libexecdir}/${PN}/

    install -d ${D}${systemd_system_unitdir}/phosphor-multi-gpio-monitor.service.d
}
