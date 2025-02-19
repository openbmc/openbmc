FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd systemd

SERVICE_LIST = "power-good-assert@.service \
                power-good-deassert@.service \
                critical-leak-detect-assert@.service \
                leak-detect-assert@.service \
                leak-detect-deassert@.service \
                rpu-ready-assert@.service \
                rpu-ready-deassert@.service \
                rack-level-leak@.service \
                led-blue-assert@.service \
                led-blue-deassert@.service \
                led-amber-assert@.service \
                led-amber-deassert@.service \
                "

SERVICE_FILE_FMT = "file://{0}"

SRC_URI += "file://ventura-phosphor-multi-gpio-monitor.json \
            file://ventura-phosphor-multi-gpio-presence.json \
            file://logging \
            file://rack-level-leak \
            file://frontled \
            ${@compose_list(d, 'SERVICE_FILE_FMT', 'SERVICE_LIST')} \
            "

RDEPENDS:${PN}:append: = " bash"

FILES:${PN} += "${systemd_system_unitdir}/*"

SYSTEMD_SERVICE:${PN} += "${SERVICE_LIST}"

SYSTEMD_AUTO_ENABLE = "enable"

do_install:append:() {
    install -d ${D}${datadir}/phosphor-gpio-monitor
    install -m 0644 ${UNPACKDIR}/ventura-phosphor-multi-gpio-monitor.json \
                    ${D}${datadir}/phosphor-gpio-monitor/phosphor-multi-gpio-monitor.json
    install -m 0644 ${UNPACKDIR}/ventura-phosphor-multi-gpio-presence.json \
                    ${D}${datadir}/phosphor-gpio-monitor/phosphor-multi-gpio-presence.json

    for s in ${SERVICE_LIST}
    do
        install -m 0644 ${UNPACKDIR}/${s} ${D}${systemd_system_unitdir}/${s}
    done

    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/logging ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/rack-level-leak ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/frontled ${D}${libexecdir}/${PN}/
}
