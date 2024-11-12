FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

SRC_URI += " \
             file://LED-GroupManager-override.conf \
           "

FILES:${PN} += " \
                 ${systemd_system_unitdir}/xyz.openbmc_project.LED.GroupManager.service.d \
               "

PACKAGECONFIG:append = " monitor-operational-status"

do_install:append() {
    install -d ${D}${systemd_system_unitdir}/xyz.openbmc_project.LED.GroupManager.service.d
    install -m 644 ${UNPACKDIR}/LED-GroupManager-override.conf \
        ${D}${systemd_system_unitdir}/xyz.openbmc_project.LED.GroupManager.service.d
}
