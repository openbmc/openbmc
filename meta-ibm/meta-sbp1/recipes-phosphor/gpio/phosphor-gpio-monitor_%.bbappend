FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
inherit systemd
inherit obmc-phosphor-systemd

SRC_URI += " \
            file://phosphor-multi-gpio-presence.json \
            file://dependencies.conf \
           "

FILES:${PN}-presence += " ${datadir}/${PN}/phosphor-multi-gpio-presence.json \
                          ${systemd_system_unitdir}/phosphor-multi-gpio-presence.service.d/dependencies.conf \
                       "

do_install:append() {
    rm -f ${D}${datadir}/${PN}/phosphor-multi-gpio-presence.json
    install -m 0644 ${UNPACKDIR}/phosphor-multi-gpio-presence.json ${D}${datadir}/${PN}/
    install -d ${D}${systemd_system_unitdir}/phosphor-multi-gpio-presence.service.d/
    install -m 644 -D ${UNPACKDIR}/dependencies.conf ${D}${systemd_system_unitdir}/phosphor-multi-gpio-presence.service.d/dependencies.conf
}
