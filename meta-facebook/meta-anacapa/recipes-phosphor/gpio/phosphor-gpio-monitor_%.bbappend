FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-utils
inherit systemd

SERVICE_LIST = "assert-power-good-drop.service \
                bind-apml-driver.service \
                deassert-power-good-drop.service \
                multi-gpios-sys-init.service \
                power-rail-logger@.service \
                "

SERVICE_FILE_FMT = "file://{0}"

SRC_URI += " \
    file://assert-power-good-drop \
    file://bind-apml-driver \
    file://deassert-power-good-drop \
    file://multi-gpios-sys-init \
    file://plat-phosphor-multi-gpio-monitor.json \
    file://power-rail-event-logger \
    file://phosphor-multi-gpio-monitor.conf \
    ${@compose_list(d, 'SERVICE_FILE_FMT', 'SERVICE_LIST')} \
    "

RDEPENDS:${PN}:append = " bash"

FILES:${PN} += "${systemd_system_unitdir}/*"

SYSTEMD_SERVICE:${PN} += "${SERVICE_LIST}"

do_install:append() {
    install -d ${D}${datadir}/phosphor-gpio-monitor
    install -m 0644 ${UNPACKDIR}/plat-phosphor-multi-gpio-monitor.json \
                    ${D}${datadir}/phosphor-gpio-monitor/phosphor-multi-gpio-monitor.json

    install -d ${D}${systemd_system_unitdir}
    for s in ${SERVICE_LIST}
    do
        install -m 0644 ${UNPACKDIR}/${s} ${D}${systemd_system_unitdir}/${s}
    done

    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/assert-power-good-drop ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/bind-apml-driver ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/deassert-power-good-drop ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/multi-gpios-sys-init ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/power-rail-event-logger ${D}${libexecdir}/${PN}/

    install -d ${D}${systemd_system_unitdir}/phosphor-multi-gpio-monitor.service.d
    install -m 0644 ${UNPACKDIR}/phosphor-multi-gpio-monitor.conf \
        ${D}${systemd_system_unitdir}/phosphor-multi-gpio-monitor.service.d/phosphor-multi-gpio-monitor.conf
}
