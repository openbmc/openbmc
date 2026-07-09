FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

FACEBOOK_REMOVED_DBUS_SENSORS:remove = " \
    external \
"

PACKAGECONFIG:append = " \
    nvmesensor \
    cablemonitor \
"

SRC_URI:append = " \
    file://cable-config.json \
    file://wait-host0-state-ready.conf \
    "

FILES:${PN} += "${systemd_system_unitdir}/xyz.openbmc_project.psusensor.service.d/wait-host0-state-ready.conf"
FILES:${PN} += "${systemd_system_unitdir}/xyz.openbmc_project.hwmontempsensor.service.d/wait-host0-state-ready.conf"

do_install:append() {
    install -d ${D}${systemd_system_unitdir}/xyz.openbmc_project.psusensor.service.d
    install -m 0644 ${UNPACKDIR}/wait-host0-state-ready.conf \
        ${D}${systemd_system_unitdir}/xyz.openbmc_project.psusensor.service.d/wait-host0-state-ready.conf

    install -d ${D}${systemd_system_unitdir}/xyz.openbmc_project.hwmontempsensor.service.d
    install -m 0644 ${UNPACKDIR}/wait-host0-state-ready.conf \
        ${D}${systemd_system_unitdir}/xyz.openbmc_project.hwmontempsensor.service.d/wait-host0-state-ready.conf

    if ${@bb.utils.contains('PACKAGECONFIG', 'cablemonitor', 'true', 'false', d)}; then
        install -d ${D}/var/lib/cablemonitor
        install -m 0755 ${UNPACKDIR}/cable-config.json ${D}/var/lib/cablemonitor/cable-config.json
    fi
}
