FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd systemd

SRC_URI:append = " \
    file://phosphor-multi-gpio-monitor.json \
    file://cpu-boot-done \
    file://run-power-good \
    "

RDEPENDS:${PN}:append = " bash"

FILES:${PN} += "${systemd_system_unitdir}/*"

SYSTEMD_SERVICE:${PN}-monitor += " \
    cpu-boot-done-assert.service \
    cpu-boot-done-deassert.service \
    run-power-good-assert.service \
    run-power-good-deassert.service \
    "

do_install:append() {

    install -d ${D}${datadir}/${PN}

    install -m 0644 ${UNPACKDIR}/phosphor-multi-gpio-monitor.json \
                    ${D}${datadir}/${PN}/phosphor-multi-gpio-monitor.json

    install -d ${D}${libexecdir}/${PN}

    install -m 0755 ${UNPACKDIR}/cpu-boot-done \
                    ${D}${libexecdir}/${PN}/cpu-boot-done
    install -m 0755 ${UNPACKDIR}/run-power-good \
                    ${D}${libexecdir}/${PN}/run-power-good
}
