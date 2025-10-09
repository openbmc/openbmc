FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd systemd

SERVICE_LIST = "power-rail-assert-log@.service \
                power-rail-deassert-log@.service \
                smc-assert-log@.service \
                smc-deassert-log@.service \
                led-blue-assert@.service \
                led-blue-deassert@.service \
                led-blue-init-assert@.service \
                led-blue-init-deassert@.service \
                tray-identify@.service \
                "

SERVICE_FILE_FMT = "file://{0}"

SRC_URI += "file://ventura-phosphor-multi-gpio-monitor.json \
            file://power-rail-event-logger \
            file://smc-event-logger \
            file://logging \
            file://frontled \
            file://tray-identify \
            ${@compose_list(d, 'SERVICE_FILE_FMT', 'SERVICE_LIST')} \
            "

RDEPENDS:${PN}:append = " bash"

FILES:${PN} += "${systemd_system_unitdir}/*"

SYSTEMD_SERVICE:${PN} += "${SERVICE_LIST}"

do_install:append() {
    install -d ${D}${datadir}/phosphor-gpio-monitor
    install -m 0644 ${UNPACKDIR}/ventura-phosphor-multi-gpio-monitor.json \
                    ${D}${datadir}/phosphor-gpio-monitor/phosphor-multi-gpio-monitor.json

    for s in ${SERVICE_LIST}
    do
        install -m 0644 ${UNPACKDIR}/${s} ${D}${systemd_system_unitdir}/${s}
    done

    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/logging ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/frontled ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/power-rail-event-logger ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/smc-event-logger ${D}${libexecdir}/${PN}/
    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/tray-identify ${D}${bindir}/
}