FILESEXTRAPATHS:prepend:bletchley := "${THISDIR}/${PN}:"
SRC_URI:append:bletchley = " \
                            file://config.json \
                           "

FILES:${PN}:append:bletchley = " ${datadir}/swampd/config.json"

do_install:append:bletchley() {
    # Insert service dependencies
    sed -i '/^\[Unit\]/a After=xyz.openbmc_project.hwmontempsensor.service' \
        ${D}${systemd_system_unitdir}/phosphor-pid-control.service
    sed -i '/^\[Unit\]/a After=phosphor-virtual-sensor.service' \
        ${D}${systemd_system_unitdir}/phosphor-pid-control.service

    install -d ${D}${datadir}/swampd
    install -m 0644 -D ${WORKDIR}/config.json ${D}${datadir}/swampd/
}
