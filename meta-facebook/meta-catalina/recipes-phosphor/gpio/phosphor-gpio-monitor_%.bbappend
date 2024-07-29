FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd systemd

SRC_URI += " \
    file://catalina-gpio-monitor \
    file://phosphor-multi-gpio-monitor.json \
    "

RDEPENDS:${PN}:append = " bash"

FILES:${PN} += "${systemd_system_unitdir}/*"

SYSTEMD_SERVICE:${PN}-monitor += " \
    assert-reset-button.service \
    assert-run-power-pg.service \
    deassert-reset-button.service \
    deassert-run-power-pg.service \
    "

SYSTEMD_AUTO_ENABLE = "enable"

do_install:append:() {
    install -d ${D}${datadir}/${PN}
    install -d ${D}${libexecdir}/${PN}

    install -m 0644 ${WORKDIR}/phosphor-multi-gpio-monitor.json \
                    ${D}${datadir}/phosphor-gpio-monitor/phosphor-multi-gpio-monitor.json
    install -m 0755 ${WORKDIR}/catalina-gpio-monitor ${D}${libexecdir}/${PN}/catalina-gpio-monitor
}
