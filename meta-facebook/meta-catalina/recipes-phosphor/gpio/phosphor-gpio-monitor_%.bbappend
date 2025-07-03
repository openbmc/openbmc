FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd systemd

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
}

do_install:append:catalina() {

    install -m 0644 ${UNPACKDIR}/phosphor-multi-gpio-monitor-evt.json \
                    ${D}${datadir}/${PN}/phosphor-multi-gpio-monitor-evt.json
    install -m 0644 ${UNPACKDIR}/phosphor-multi-gpio-presence-evt.json \
                    ${D}${datadir}/${PN}/phosphor-multi-gpio-presence-evt.json

    install -m 0755 ${UNPACKDIR}/prepare-serv-json \
                    ${D}${libexecdir}/${PN}/prepare-serv-json
}
