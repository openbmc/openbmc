FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd systemd

SERVICE_LIST = "present-assert@.service \
                present-deassert@.service \
                power-good-assert@.service \
                power-good-deassert@.service \
                leak-detect-assert@.service \
                leak-detect-deassert@.service \
                rpu-ready-assert.service \
                rpu-ready-deassert.service \
                cable-present-assert@.service \
                cable-present-deassert@.service \
                sfp-present-assert.service \
                sfp-present-deassert.service \
                ac-power-good-assert@.service \
                ac-power-good-deassert@.service \
                "

SERVICE_FILE_FMT = "file://{0}"

SRC_URI += "file://minerva-phosphor-multi-gpio-monitor.json \
            file://logging \
            file://sfp-present-check \
            ${@compose_list(d, 'SERVICE_FILE_FMT', 'SERVICE_LIST')} \
            "

RDEPENDS:${PN}:append: = " bash"

FILES:${PN} += "${systemd_system_unitdir}/*"

SYSTEMD_SERVICE:${PN} += "${SERVICE_LIST}"

SYSTEMD_AUTO_ENABLE = "enable"

do_install:append:() {
    install -d ${D}${datadir}/phosphor-gpio-monitor
    install -m 0644 ${WORKDIR}/minerva-phosphor-multi-gpio-monitor.json \
                    ${D}${datadir}/phosphor-gpio-monitor/phosphor-multi-gpio-monitor.json

    for s in ${SERVICE_LIST}
    do
        install -m 0644 ${WORKDIR}/${s} ${D}${systemd_system_unitdir}/${s}
    done

    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${WORKDIR}/logging ${D}${libexecdir}/${PN}/
    install -m 0755 ${WORKDIR}/sfp-present-check ${D}${libexecdir}/${PN}/
}
