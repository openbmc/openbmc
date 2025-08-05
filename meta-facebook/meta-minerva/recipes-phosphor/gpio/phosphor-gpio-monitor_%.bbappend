FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd systemd

SERVICE_LIST = "rpu-ready-assert@.service \
                rpu-ready-deassert@.service \
                power-rail-assert-log@.service \
                power-rail-deassert-log@.service \
                vr-fault-assert-log@.service \
                vr-fault-deassert-log@.service \
                rescan-fru.service \
                fan-reload.service \
                cr-toggle-boot-enabled.service \
                cr-toggle-boot-disabled.service \
                "

SERVICE_FILE_FMT = "file://{0}"

SRC_URI += "file://minerva-phosphor-multi-gpio-monitor.json \
            file://minerva-phosphor-multi-gpio-presence.json \
            file://logging \
            file://fan-reload \
            file://cr-toggle-boot-logger \
            file://power-rail-event-logger \
            file://vr-fault-event-logger \
            ${@compose_list(d, 'SERVICE_FILE_FMT', 'SERVICE_LIST')} \
            "

RDEPENDS:${PN}:append = " bash"

FILES:${PN} += "${systemd_system_unitdir}/*"

SYSTEMD_SERVICE:${PN} += "${SERVICE_LIST}"

do_install:append() {
    install -d ${D}${datadir}/phosphor-gpio-monitor
    install -m 0644 ${UNPACKDIR}/minerva-phosphor-multi-gpio-monitor.json \
                    ${D}${datadir}/phosphor-gpio-monitor/phosphor-multi-gpio-monitor.json
    install -m 0644 ${UNPACKDIR}/minerva-phosphor-multi-gpio-presence.json \
                    ${D}${datadir}/phosphor-gpio-monitor/phosphor-multi-gpio-presence.json

    for s in ${SERVICE_LIST}
    do
        install -m 0644 ${UNPACKDIR}/${s} ${D}${systemd_system_unitdir}/${s}
    done

    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/logging ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/fan-reload ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/cr-toggle-boot-logger ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/power-rail-event-logger ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/vr-fault-event-logger ${D}${libexecdir}/${PN}/
}
