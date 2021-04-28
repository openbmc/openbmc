FILESEXTRAPATHS_prepend_gbs := "${THISDIR}/${PN}:"
SRC_URI_append_gbs = " file://service-override.conf"

FILES_${PN}_append_gbs = " ${systemd_system_unitdir}/xyz.openbmc_project.LED.GroupManager.service.d/service-override.conf"

do_compile_prepend_gbs() {
    install -m 0644 ${STAGING_DATADIR_NATIVE}/${PN}/led.yaml ${S}
}

do_install_append_gbs() {
    rm -rf ${D}${datadir}/${PN}/*

    install -d ${D}${systemd_system_unitdir}/xyz.openbmc_project.LED.GroupManager.service.d
    install -D -m 0644 ${WORKDIR}/service-override.conf \
      ${D}${systemd_system_unitdir}/xyz.openbmc_project.LED.GroupManager.service.d/
}
