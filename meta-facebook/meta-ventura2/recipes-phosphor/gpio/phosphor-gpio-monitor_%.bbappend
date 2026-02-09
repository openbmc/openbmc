FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd systemd

SERVICE_LIST = "gpio-set-high@.service \
                gpio-set-low@.service \
                power-rail-assert-log@.service \
                power-rail-deassert-log@.service \
                smc-assert-log@.service \
                smc-deassert-log@.service \
                "

SERVICE_FILE_FMT = "file://{0}"

SRC_URI += "file://plat-phosphor-multi-gpio-monitor.json \
            file://gpio-setter \
            file://power-rail-event-logger \
            file://smc-event-logger \
            ${@compose_list(d, 'SERVICE_FILE_FMT', 'SERVICE_LIST')} \
            "

RDEPENDS:${PN}:append = " bash"

FILES:${PN} += "${systemd_system_unitdir}/*"

SYSTEMD_SERVICE:${PN} += "${SERVICE_LIST}"

do_install:append() {
    install -d ${D}${localstatedir}/lib/${PN}
    install -d ${D}${datadir}/${PN}
    install -m 0644 ${UNPACKDIR}/plat-phosphor-multi-gpio-monitor.json \
                    ${D}${datadir}/${PN}/phosphor-multi-gpio-monitor.json

    for s in ${SERVICE_LIST}
    do
        install -m 0644 ${UNPACKDIR}/${s} ${D}${systemd_system_unitdir}/${s}
    done

    LIBEXECDIR_PN="${D}${libexecdir}/${PN}"
    install -d ${LIBEXECDIR_PN}
    install -m 0755 ${UNPACKDIR}/gpio-setter ${LIBEXECDIR_PN}
    install -m 0755 ${UNPACKDIR}/power-rail-event-logger ${LIBEXECDIR_PN}
    install -m 0755 ${UNPACKDIR}/smc-event-logger ${LIBEXECDIR_PN}
}
