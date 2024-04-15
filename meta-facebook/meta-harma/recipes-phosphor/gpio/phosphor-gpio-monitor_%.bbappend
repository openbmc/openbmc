FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd systemd

SRC_URI += "file://plat-phosphor-multi-gpio-monitor.json \
            file://assert-reset-button.service \
            file://assert-power-good.service \
            file://assert-post-end.service \
            file://deassert-reset-button.service \
            file://deassert-power-good.service \
            file://deassert-post-end.service \
            file://multi-gpios-sys-init.service \
            file://multi-gpios-sys-init \
            file://assert-power-good \
            file://deassert-power-good \
            file://assert-post-end \
            file://deassert-post-end \
            file://assert-reset-button \
            file://deassert-reset-button \
            file://assert-gpio-log@.service \
            file://deassert-gpio-log@.service \
            file://logging \
            "

RDEPENDS:${PN}:append = " bash"

FILES:${PN} += "${systemd_system_unitdir}/*"

SYSTEMD_SERVICE:${PN} += " \
    assert-reset-button.service \
    assert-power-good.service \
    assert-post-end.service \
    deassert-reset-button.service \
    deassert-power-good.service \
    deassert-post-end.service \
    multi-gpios-sys-init.service \
    "

SYSTEMD_AUTO_ENABLE = "enable"

do_install:append:() {
    install -d ${D}${datadir}/phosphor-gpio-monitor
    install -m 0644 ${WORKDIR}/plat-phosphor-multi-gpio-monitor.json \
                    ${D}${datadir}/phosphor-gpio-monitor/phosphor-multi-gpio-monitor.json

    install -d ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/*.service ${D}${systemd_system_unitdir}/

    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${WORKDIR}/logging ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/multi-gpios-sys-init ${D}${libexecdir}/${PN}/

    install -m 0755 ${WORKDIR}/assert-reset-button ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/deassert-reset-button ${D}${libexecdir}/${PN}/

    install -m 0755 ${WORKDIR}/assert-post-end ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/deassert-post-end ${D}${libexecdir}/${PN}/

    install -m 0755 ${WORKDIR}/assert-power-good ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/deassert-power-good ${D}${libexecdir}/${PN}/
}

SYSTEMD_OVERRIDE:${PN}-monitor += "phosphor-multi-gpio-monitor.conf:phosphor-multi-gpio-monitor.service.d/phosphor-multi-gpio-monitor.conf"
