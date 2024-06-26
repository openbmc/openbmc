FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

RDEPENDS:${PN}-monitor += "bash"

SRC_URI += " \
            file://phosphor-multi-gpio-monitor.json \
            file://phosphor-multi-gpio-presence.json \
           "

SYSTEMD_SERVICE:${PN}-monitor += " \
                                  ampere-host-shutdown-ack@.service \
                                  ampere-host-reboot@.service \
                                 "

FILES:${PN}-monitor += " \
                        ${datadir}/${PN}/phosphor-multi-gpio-monitor.json \
                       "
FILES:${PN}-presence += " \
                        ${datadir}/${PN}/phosphor-multi-gpio-presence.json \
                       "

do_install:append() {
    install -d ${D}${bindir}
    install -m 0644 ${UNPACKDIR}/phosphor-multi-gpio-monitor.json ${D}${datadir}/${PN}/
    install -m 0644 ${UNPACKDIR}/phosphor-multi-gpio-presence.json ${D}${datadir}/${PN}/
}
