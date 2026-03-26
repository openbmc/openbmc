FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit systemd

SRC_URI:append = " \
    file://phosphor-multi-gpio-monitor.json \
    file://phosphor-multi-gpio-presence.json \
    file://platform-gpio-monitor \
    file://set-uart-select-led \
    file://power-rail-assert-log@.service \
    file://power-rail-deassert-log@.service \
    file://power-rail-event-logger \
    file://vr-fault-assert-log@.service \
    file://vr-fault-deassert-log@.service \
    file://vr-fault-event-logger \
    file://assert-cpu-boot-done.service \
    file://assert-gpio-log@.service \
    file://assert-reset-button.service \
    file://assert-run-power-pg.service \
    file://assert-uart-select-led.service \
    file://deassert-cpu-boot-done.service \
    file://deassert-gpio-log@.service \
    file://deassert-reset-button.service \
    file://deassert-run-power-pg.service \
    file://deassert-uart-select-led.service \
    file://platform-host-ready.target \
    "

SRC_URI:append:catalina = " \
    file://phosphor-multi-gpio-monitor-evt.json \
    file://phosphor-multi-gpio-presence-evt.json \
    file://prepare-serv-json \
    "

RDEPENDS:${PN}:append = " bash"

FILES:${PN} += "${systemd_system_unitdir}/*"

SYSTEMD_SERVICE:${PN}-monitor += " \
    assert-cpu-boot-done.service \
    assert-gpio-log@.service \
    assert-reset-button.service \
    assert-run-power-pg.service \
    assert-uart-select-led.service \
    deassert-cpu-boot-done.service \
    deassert-gpio-log@.service \
    deassert-reset-button.service \
    deassert-run-power-pg.service \
    deassert-uart-select-led.service \
    power-rail-assert-log@.service \
    power-rail-deassert-log@.service \
    vr-fault-assert-log@.service \
    vr-fault-deassert-log@.service \
    platform-host-ready.target \
    "

do_install:append() {

    install -d ${D}${datadir}/${PN}

    install -m 0644 ${UNPACKDIR}/phosphor-multi-gpio-monitor.json \
                    ${D}${datadir}/${PN}/phosphor-multi-gpio-monitor.json
    install -m 0644 ${UNPACKDIR}/phosphor-multi-gpio-presence.json \
                    ${D}${datadir}/${PN}/phosphor-multi-gpio-presence.json


    install -d ${D}${libexecdir}/${PN}

    install -m 0755 ${UNPACKDIR}/platform-gpio-monitor \
                    ${D}${libexecdir}/${PN}/platform-gpio-monitor
    install -m 0755 ${UNPACKDIR}/set-uart-select-led \
                    ${D}${libexecdir}/${PN}/set-uart-select-led
    install -m 0755 ${UNPACKDIR}/power-rail-event-logger \
                    ${D}${libexecdir}/${PN}/power-rail-event-logger
    install -m 0755 ${UNPACKDIR}/vr-fault-event-logger \
                    ${D}${libexecdir}/${PN}/vr-fault-event-logger

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/assert-cpu-boot-done.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/assert-gpio-log@.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/assert-reset-button.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/assert-run-power-pg.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/assert-uart-select-led.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/deassert-cpu-boot-done.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/deassert-gpio-log@.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/deassert-reset-button.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/deassert-run-power-pg.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/deassert-uart-select-led.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/power-rail-assert-log@.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/power-rail-deassert-log@.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/vr-fault-assert-log@.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/vr-fault-deassert-log@.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/platform-host-ready.target ${D}${systemd_system_unitdir}
}

do_install:append:catalina() {

    install -m 0644 ${UNPACKDIR}/phosphor-multi-gpio-monitor-evt.json \
                    ${D}${datadir}/${PN}/phosphor-multi-gpio-monitor-evt.json
    install -m 0644 ${UNPACKDIR}/phosphor-multi-gpio-presence-evt.json \
                    ${D}${datadir}/${PN}/phosphor-multi-gpio-presence-evt.json

    install -m 0755 ${UNPACKDIR}/prepare-serv-json \
                    ${D}${libexecdir}/${PN}/prepare-serv-json
}
