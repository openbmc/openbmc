FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd systemd

SRC_URI += " \
    file://backend-nic-driver-bind \
    file://catalina-gpio-monitor \
    file://prepare-serv-json \
    file://phosphor-multi-gpio-monitor.json \
    file://phosphor-multi-gpio-presence.json \
    file://phosphor-multi-gpio-monitor-evt.json \
    file://phosphor-multi-gpio-presence-evt.json \
    file://set-uart-select-led \
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
    backend-nic-driver-bind.service \
    catalina-host-ready.target \
    "

SYSTEMD_AUTO_ENABLE = "enable"

do_install:append:() {
    install -d ${D}${datadir}/${PN}
    install -d ${D}${libexecdir}/${PN}

    install -m 0644 ${UNPACKDIR}/phosphor-multi-gpio-monitor.json \
                    ${D}${datadir}/${PN}/phosphor-multi-gpio-monitor.json
    install -m 0644 ${UNPACKDIR}/phosphor-multi-gpio-presence.json \
                    ${D}${datadir}/${PN}/phosphor-multi-gpio-presence.json
    install -m 0644 ${UNPACKDIR}/phosphor-multi-gpio-monitor.json \
                    ${D}${datadir}/${PN}/phosphor-multi-gpio-monitor-evt.json
    install -m 0644 ${UNPACKDIR}/phosphor-multi-gpio-presence.json \
                    ${D}${datadir}/${PN}/phosphor-multi-gpio-presence-evt.json
    install -m 0755 ${UNPACKDIR}/backend-nic-driver-bind ${D}${libexecdir}/${PN}/backend-nic-driver-bind
    install -m 0755 ${UNPACKDIR}/catalina-gpio-monitor ${D}${libexecdir}/${PN}/catalina-gpio-monitor
    install -m 0755 ${UNPACKDIR}/prepare-serv-json ${D}${libexecdir}/${PN}/prepare-serv-json
    install -m 0755 ${UNPACKDIR}/set-uart-select-led ${D}${libexecdir}/${PN}/set-uart-select-led
}
