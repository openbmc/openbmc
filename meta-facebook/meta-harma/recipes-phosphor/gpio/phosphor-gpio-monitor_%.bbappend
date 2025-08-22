FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd systemd

SRC_URI += "file://assert-post-end \
            file://assert-post-end.service \
            file://assert-power-good \
            file://assert-power-good.service \
            file://assert-reset-button \
            file://assert-reset-button.service \
            file://auto-power \
            file://auto-poweroff \
            file://auto-poweroff@.service \
            file://auto-poweron \
            file://auto-poweron@.service \
            file://deassert-post-end \
            file://deassert-post-end.service \
            file://deassert-power-good \
            file://deassert-power-good.service \
            file://deassert-reset-button \
            file://deassert-reset-button.service \
            file://deassert-uart-switch-button \
            file://deassert-uart-switch-button.service \
            file://device-reinitial \
            file://device-reinitial@.service \
            file://device-util \
            file://fan-reload \
            file://fan-reload.service \
            file://gpios-assert-log@.service \
            file://gpios-deassert-log@.service \
            file://gpios-event-logger \
            file://initial-poweron-device \
            file://initial-poweron-device.service \
            file://logging-util \
            file://mmc-recovery.service \
            file://multi-gpios-sys-init \
            file://multi-gpios-sys-init.service \
            file://plat-phosphor-multi-gpio-monitor.json \
            file://plat-phosphor-multi-gpio-presence.json \
            file://power-rail-assert-log@.service \
            file://power-rail-deassert-log@.service \
            file://power-rail-event-logger \
            file://prochot-assert-log.service \
            file://prochot-deassert-log.service \
            file://smc-assert-log@.service \
            file://smc-assert-log@.timer \
            file://smc-deassert-log@.service \
            file://smc-event-logger \
            file://thermtrip-assert-log.service \
            file://thermtrip-deassert-log.service \
            file://thermal-event-logger \
            file://vr-fault-assert-log@.service \
            file://vr-fault-deassert-log@.service \
            file://vr-fault-event-logger \
            "

RDEPENDS:${PN}:append = " bash"

FILES:${PN} += "${systemd_system_unitdir}/*"

SYSTEMD_SERVICE:${PN} += " \
    assert-power-good.service \
    assert-post-end.service \
    assert-reset-button.service \
    deassert-power-good.service \
    deassert-post-end.service \
    deassert-reset-button.service \
    deassert-uart-switch-button.service \
    device-reinitial@.service \
    fan-reload.service \
    initial-poweron-device.service \
    mmc-recovery.service \
    multi-gpios-sys-init.service \
    prochot-assert-log.service \
    prochot-deassert-log.service  \
    smc-assert-log@.service \
    smc-assert-log@.timer \
    smc-deassert-log@.service \
    thermtrip-assert-log.service \
    thermtrip-deassert-log.service  \
    "

do_install:append() {
    install -d ${D}${datadir}/phosphor-gpio-monitor
    install -d ${D}${systemd_system_unitdir}/
    install -d ${D}${libexecdir}/${PN}

    install -m 0644 ${UNPACKDIR}/plat-phosphor-multi-gpio-monitor.json \
                    ${D}${datadir}/phosphor-gpio-monitor/phosphor-multi-gpio-monitor.json
    install -m 0644 ${UNPACKDIR}/plat-phosphor-multi-gpio-presence.json \
                    ${D}${datadir}/phosphor-gpio-monitor/phosphor-multi-gpio-presence.json

    install -d ${D}${systemd_system_unitdir}/
    install -m 0644 ${UNPACKDIR}/*.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${UNPACKDIR}/*.timer ${D}${systemd_system_unitdir}

    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/gpios-event-logger ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/power-rail-event-logger ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/smc-event-logger ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/thermal-event-logger ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/vr-fault-event-logger ${D}${libexecdir}/${PN}/

    install -m 0755 ${UNPACKDIR}/logging-util ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/multi-gpios-sys-init ${D}${libexecdir}/${PN}/

    install -m 0755 ${UNPACKDIR}/assert-reset-button ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/deassert-reset-button ${D}${libexecdir}/${PN}/

    install -m 0755 ${UNPACKDIR}/assert-post-end ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/deassert-post-end ${D}${libexecdir}/${PN}/

    install -m 0755 ${UNPACKDIR}/assert-power-good ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/deassert-power-good ${D}${libexecdir}/${PN}/

    install -m 0755 ${UNPACKDIR}/device-reinitial ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/device-util ${D}${libexecdir}/${PN}/

    install -m 0755 ${UNPACKDIR}/deassert-uart-switch-button ${D}${libexecdir}/${PN}/

    install -m 0755 ${UNPACKDIR}/auto-power ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/auto-poweroff ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/auto-poweron ${D}${libexecdir}/${PN}/

    install -m 0755 ${UNPACKDIR}/fan-reload ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/initial-poweron-device ${D}${libexecdir}/${PN}/
}

SYSTEMD_OVERRIDE:${PN}-monitor += "phosphor-multi-gpio-monitor.conf:phosphor-multi-gpio-monitor.service.d/phosphor-multi-gpio-monitor.conf"
