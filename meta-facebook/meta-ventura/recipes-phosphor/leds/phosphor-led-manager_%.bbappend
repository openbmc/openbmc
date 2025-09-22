FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " file://led-group-config.json \
                   file://xyz.openbmc_project.LED.GroupManager.service.d/override.conf\
                   "

FILES:${PN} += "${systemd_system_unitdir}/xyz.openbmc_project.LED.GroupManager.service.d"

do_install:append() {
        install -d ${D}${systemd_system_unitdir}/xyz.openbmc_project.LED.GroupManager.service.d
        install -m 0644 ${UNPACKDIR}/xyz.openbmc_project.LED.GroupManager.service.d/override.conf \
                ${D}${systemd_system_unitdir}/xyz.openbmc_project.LED.GroupManager.service.d/
        install -m 0644 ${UNPACKDIR}/led-group-config.json ${D}${datadir}/phosphor-led-manager/
}
