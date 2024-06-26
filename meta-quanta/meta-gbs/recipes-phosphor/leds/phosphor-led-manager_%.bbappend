FILESEXTRAPATHS:prepend:gbs := "${THISDIR}/${PN}:"
SRC_URI:append:gbs = " file://service-override.conf"

FILES:${PN}:append:gbs = " ${systemd_system_unitdir}/xyz.openbmc_project.LED.GroupManager.service.d/service-override.conf"

do_install:append:gbs() {
    rm -rf ${D}${datadir}/${PN}/*

    install -d ${D}${systemd_system_unitdir}/xyz.openbmc_project.LED.GroupManager.service.d
    install -D -m 0644 ${UNPACKDIR}/service-override.conf \
      ${D}${systemd_system_unitdir}/xyz.openbmc_project.LED.GroupManager.service.d/
}
