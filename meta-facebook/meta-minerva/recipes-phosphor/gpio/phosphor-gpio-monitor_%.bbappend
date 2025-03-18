FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd systemd

SERVICE_LIST = "power-good-assert@.service \
                power-good-deassert@.service \
                leak-detect-assert@.service \
                leak-detect-deassert@.service \
                rpu-ready-assert@.service \
                rpu-ready-deassert@.service \
                ac-power-good-assert@.service \
                ac-power-good-deassert@.service \
                power-fail-assert@.service \
                power-fail-deassert@.service \
                rescan-fru.service \
                "

SERVICE_FILE_FMT = "file://{0}"

SRC_URI += "file://minerva-phosphor-multi-gpio-monitor.json \
            file://minerva-phosphor-multi-gpio-presence.json \
            file://logging \
            ${@compose_list(d, 'SERVICE_FILE_FMT', 'SERVICE_LIST')} \
            "

RDEPENDS:${PN}:append: = " bash"

FILES:${PN} += "${systemd_system_unitdir}/*"

SYSTEMD_SERVICE:${PN} += "${SERVICE_LIST}"

SYSTEMD_AUTO_ENABLE = "enable"

do_install:append:() {
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
}
