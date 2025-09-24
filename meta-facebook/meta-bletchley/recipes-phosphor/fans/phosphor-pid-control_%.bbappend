FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append:bletchley15 = " file://config.json \
                           "

FILES:${PN}:append = " ${datadir}/swampd"
FILES:${PN}:append = " ${systemd_system_unitdir}/phosphor-pid-control.service.d/*.conf"

do_install:append() {

    override_dir="${D}${systemd_system_unitdir}/phosphor-pid-control.service.d"
    override_file="${override_dir}/10-bletchley.conf"
    mkdir -p ${D}${systemd_system_unitdir}/phosphor-pid-control.service.d
    echo "[Unit]" > ${override_file}
    echo "After=xyz.openbmc_project.hwmontempsensor.service" >> ${override_file}
    echo "After=phosphor-virtual-sensor.service" >> ${override_file}

    install -d ${D}${datadir}/swampd
}

do_install:append:bletchley15() {
    install -m 0644 -D ${UNPACKDIR}/config.json ${D}${datadir}/swampd/config.json
}
