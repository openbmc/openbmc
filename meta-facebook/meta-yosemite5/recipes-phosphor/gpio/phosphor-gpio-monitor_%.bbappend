FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit systemd

SRC_URI += "file://phosphor-multi-gpio-monitor.json \
            file://phosphor-multi-gpio-monitor-evt.json \
            file://reset_btn \
            file://reset_btn@.service \
            file://multi-gpios-sys-init \
            file://multi-gpios-sys-init.service \
            file://assert-host-ready.service \
            file://deassert-host-ready.service \
            file://assert-power-good-drop \
            file://assert-power-good-drop.service \
            file://deassert-power-good-drop \
            file://deassert-power-good-drop.service \
            file://gpio_bypass \
            file://gpio_bypass@.service \
            file://thermal-event-logger \
            file://thermal-assert-log@.service \
            file://thermal-deassert-log@.service \
            file://vr-fault-assert-log@.service \
            file://vr-fault-deassert-log@.service \
            file://vr-fault-event-logger \
            file://smc-assert-log@.service \
            file://smc-deassert-log@.service \
            file://smc-event-logger \
            file://phosphor-multi-gpio-monitor.conf \
            "

RDEPENDS:${PN}:append = " bash"

FILES:${PN} += "${systemd_system_unitdir}/*"

SYSTEMD_SERVICE:${PN} += " \
    reset_btn@.service \
    multi-gpios-sys-init.service \
    assert-host-ready.service \
    deassert-host-ready.service \
    assert-power-good-drop.service \
    deassert-power-good-drop.service \
    gpio_bypass@.service \
    thermal-assert-log@.service \
    thermal-deassert-log@.service \
    vr-fault-assert-log@.service \
    vr-fault-deassert-log@.service \
    smc-assert-log@.service \
    smc-deassert-log@.service \
    "

SYSTEMD_AUTO_ENABLE = "enable"

do_install:append() {
    install -d ${D}${datadir}/${PN}
    install -m 0644 ${UNPACKDIR}/phosphor-multi-gpio-monitor.json \
                    ${D}${datadir}/${PN}/phosphor-multi-gpio-monitor.json
    install -m 0644 ${UNPACKDIR}/phosphor-multi-gpio-monitor-evt.json \
                    ${D}${datadir}/${PN}/phosphor-multi-gpio-monitor-evt.json

    install -d ${D}${systemd_system_unitdir}/
    install -m 0644 ${UNPACKDIR}/*.service ${D}${systemd_system_unitdir}/

    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/reset_btn ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/gpio_bypass ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/multi-gpios-sys-init ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/thermal-event-logger ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/vr-fault-event-logger ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/smc-event-logger ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/assert-power-good-drop ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/deassert-power-good-drop ${D}${libexecdir}/${PN}/

    install -d ${D}${systemd_system_unitdir}/phosphor-multi-gpio-monitor.service.d
    install -m 0644 ${UNPACKDIR}/phosphor-multi-gpio-monitor.conf \
        ${D}${systemd_system_unitdir}/phosphor-multi-gpio-monitor.service.d/phosphor-multi-gpio-monitor.conf
}
