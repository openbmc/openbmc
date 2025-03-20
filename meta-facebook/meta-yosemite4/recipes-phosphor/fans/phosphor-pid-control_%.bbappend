FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd systemd

SRC_URI += "file://monitor-pldm-sensor \
            file://monitor-pldm-sensor.service \
"

SYSTEMD_SERVICE:${PN} += " \
    monitor-pldm-sensor.service \
"

SYSTEMD_AUTO_ENABLE = "enable"

RDEPENDS:${PN}:append = " bash"
FILES:${PN} += "${systemd_system_unitdir}/*"
FILES:${PN} += "${libexecdir}/*"
FILES:${PN}:append = " ${datadir}/swampd"
FILES:${PN}:append = " ${systemd_system_unitdir}/phosphor-pid-control.service.d/*.conf"

do_install:append() {
    override_dir="${D}${systemd_system_unitdir}/phosphor-pid-control.service.d"
    override_file="${override_dir}/yosemite4.conf"
    mkdir -p ${D}${systemd_system_unitdir}/phosphor-pid-control.service.d
    echo "[Unit]" > ${override_file}
    echo "After=monitor-pldm-sensor.service" >> ${override_file}
    echo "After=multi-user.target" >> ${override_file}
    install -m 0644 ${UNPACKDIR}/monitor-pldm-sensor.service ${D}${systemd_system_unitdir}
    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/monitor-pldm-sensor ${D}${libexecdir}/${PN}/
}
