FILESEXTRAPATHS:prepend:scm-npcm845  := "${THISDIR}/${PN}:"

SRC_URI:append:scm-npcm845  = " file://service-override.conf"

FILES:${PN}:append:scm-npcm845  = " ${systemd_system_unitdir}/xyz.openbmc_project.LED.GroupManager.service.d/service-override.conf"

do_install:append:scm-npcm845 () {
    rm -rf ${D}${datadir}/${PN}/*

    install -d ${D}${systemd_system_unitdir}/xyz.openbmc_project.LED.GroupManager.service.d
    install -D -m 0644 ${WORKDIR}/service-override.conf \
      ${D}${systemd_system_unitdir}/xyz.openbmc_project.LED.GroupManager.service.d/
}