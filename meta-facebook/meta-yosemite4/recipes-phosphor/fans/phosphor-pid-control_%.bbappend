FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit systemd

SRC_URI += "file://monitor-pldm-sensor \
            file://monitor-pldm-sensor.service \
            file://monitor-fan-sensor \
            file://monitor-fan-sensor.service \
"

SYSTEMD_SERVICE:${PN} += " \
    monitor-pldm-sensor.service \
    monitor-fan-sensor.service \
"

RDEPENDS:${PN}:append = " bash"
FILES:${PN} += "${systemd_system_unitdir}/*"
FILES:${PN} += "${libexecdir}/*"
FILES:${PN}:append = " ${datadir}/swampd"
FILES:${PN}:append = " ${systemd_system_unitdir}/phosphor-pid-control.service.d/*.conf"

do_install:append() {
    install -d ${D}${systemd_system_unitdir}/phosphor-pid-control.service.d
    override_file="${D}${systemd_system_unitdir}/phosphor-pid-control.service.d/yosemite4.conf"
    echo "[Unit]" > ${override_file}
    echo "After=monitor-pldm-sensor.service" >> ${override_file}
    echo "After=multi-user.target" >> ${override_file}

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/monitor-pldm-sensor.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/monitor-fan-sensor.service ${D}${systemd_system_unitdir}
    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/monitor-pldm-sensor ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/monitor-fan-sensor ${D}${libexecdir}/${PN}/
}
