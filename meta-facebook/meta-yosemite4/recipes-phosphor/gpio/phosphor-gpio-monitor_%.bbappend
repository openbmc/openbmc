FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd systemd

SRC_URI += "file://yosemite4-phosphor-multi-gpio-monitor.json \
            file://set-button-sled.service \
            file://probe-slot-device@.service \
            file://probe-slot-device \
            "

RDEPENDS:${PN}:append:yosemite4 = " bash"

FILES:${PN} += "${systemd_system_unitdir}/*"

SYSTEMD_SERVICE:${PN} += " \
    set-button-sled.service \
    probe-slot-device@.service \
    "

SYSTEMD_AUTO_ENABLE = "enable"

do_install:append:() {
    install -d ${D}${datadir}/phosphor-gpio-monitor
    install -m 0644 ${WORKDIR}/yosemite4-phosphor-multi-gpio-monitor.json \
                    ${D}${datadir}/phosphor-gpio-monitor/phosphor-multi-gpio-monitor.json
    install -m 0644 ${WORKDIR}/set-button-sled.service ${D}${systemd_system_unitdir}/set-button-sled.service
    install -m 0644 ${WORKDIR}/probe-slot-device@.service ${D}${systemd_system_unitdir}/probe-slot-device@.service
    install -d ${D}${libexecdir}/${PN}
    install -m 0777 ${WORKDIR}/probe-slot-device ${D}${libexecdir}/${PN}/
}
