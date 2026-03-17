FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit systemd

SRC_URI:append = " \
    file://phosphor-multi-gpio-monitor.json \
    file://phosphor-multi-gpio-presence.json \
    file://cpu-boot-done \
    file://cpu-shdn-ok \
    file://hmc-ready \
    file://run-power-good \
    file://thermal-event-logger \
    "

SRC_URI:append = " \
    file://cpu-boot-done-assert.service \
    file://cpu-boot-done-deassert.service \
    file://cpu-shdn-ok.service \
    file://prochot-assert-log@.service \
    file://prochot-deassert-log@.service \
    file://run-power-good-assert.service \
    file://run-power-good-deassert.service \
    file://thermtrip-assert-log@.service \
    file://thermtrip-deassert-log@.service \
    "

RDEPENDS:${PN}:append = " bash"

FILES:${PN} += "${systemd_system_unitdir}/*"

SYSTEMD_SERVICE:${PN}-monitor += " \
    cpu-boot-done-assert.service \
    cpu-boot-done-deassert.service \
    cpu-shdn-ok.service \
    prochot-assert-log@.service \
    prochot-deassert-log@.service \
    hmc-ready-assert.service \
    run-power-good-assert.service \
    run-power-good-deassert.service \
    thermtrip-assert-log@.service \
    thermtrip-deassert-log@.service \
    "

do_install:append() {

    install -d ${D}${datadir}/${PN}

    install -m 0644 ${UNPACKDIR}/phosphor-multi-gpio-monitor.json \
                    ${D}${datadir}/${PN}/phosphor-multi-gpio-monitor.json

    install -m 0644 ${UNPACKDIR}/phosphor-multi-gpio-presence.json \
                    ${D}${datadir}/${PN}/phosphor-multi-gpio-presence.json

    install -d ${D}${libexecdir}/${PN}

    install -m 0755 ${UNPACKDIR}/cpu-boot-done \
                    ${D}${libexecdir}/${PN}/cpu-boot-done
    install -m 0755 ${UNPACKDIR}/cpu-shdn-ok \
                    ${D}${libexecdir}/${PN}/cpu-shdn-ok
    install -m 0755 ${UNPACKDIR}/hmc-ready \
                    ${D}${libexecdir}/${PN}/hmc-ready
    install -m 0755 ${UNPACKDIR}/run-power-good \
                    ${D}${libexecdir}/${PN}/run-power-good
    install -m 0755 ${UNPACKDIR}/thermal-event-logger \
                    ${D}${libexecdir}/${PN}/thermal-event-logger

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/*.service ${D}${systemd_system_unitdir}/
}
