FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd systemd

SRC_URI += "file://yosemite5-phosphor-multi-gpio-monitor.json \
            file://reset_btn \
            file://reset_btn@.service \
            "

RDEPENDS:${PN}:append = " bash"

FILES:${PN} += "${systemd_system_unitdir}/*"

SYSTEMD_SERVICE:${PN} += " \
    reset_btn@.service \
    "

SYSTEMD_AUTO_ENABLE = "enable"

do_install:append:() {
    install -d ${D}${datadir}/phosphor-gpio-monitor
    install -m 0644 ${UNPACKDIR}/yosemite5-phosphor-multi-gpio-monitor.json \
                    ${D}${datadir}/phosphor-gpio-monitor/phosphor-multi-gpio-monitor.json
    install -m 0644 ${UNPACKDIR}/reset_btn@.service ${D}${systemd_system_unitdir}/
    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/reset_btn ${D}${libexecdir}/${PN}/
}
