FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit systemd

SRC_URI:append = " \
    file://control-fio-led \
    file://fan-rotor-fail-mechanism \
    file://fan-rotor-fail-mechanism.service \
    file://fan-rotor-missing-mechanism \
    file://fan-rotor-missing-mechanism.service \
    file://enable-fio-led@.service \
    file://disable-fio-led@.service \
"

SYSTEMD_SERVICE:${PN}:append = " \
    disable-fio-led@.service \
    enable-fio-led@.service \
"

RDEPENDS:${PN}:append = " bash"

do_install:append() {
    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/control-fio-led ${D}${bindir}/control-fio-led

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/*.service ${D}${systemd_system_unitdir}/

    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/fan-rotor-fail-mechanism ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/fan-rotor-missing-mechanism ${D}${libexecdir}/${PN}/
}

FILES:${PN} += "${systemd_system_unitdir}/*.service"