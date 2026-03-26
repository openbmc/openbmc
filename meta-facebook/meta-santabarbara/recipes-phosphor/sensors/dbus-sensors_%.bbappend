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
    file://wait-cable-inventory \
    file://wait-inventory.conf \
    "

inherit systemd

SRC_URI:append = " \
    file://cable-config.json \
    file://wait-cable-inventory \
    file://wait-inventory.conf \
    "

RDEPENDS:${PN}:append = " bash jq"

SYSTEMD_SERVICE:${PN}:append = " xyz.openbmc_project.cablemonitor.service"

FILES:${PN} += "${systemd_system_unitdir}/xyz.openbmc_project.cablemonitor.service.d/wait-inventory.conf"

do_install:append() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'cablemonitor', 'true', 'false', d)}; then
        install -d ${D}/var/lib/cablemonitor
        install -m 0755 ${UNPACKDIR}/cable-config.json ${D}/var/lib/cablemonitor/cable-config.json

        install -d ${D}${libexecdir}/cablemonitor
        install -m 0755 ${UNPACKDIR}/wait-cable-inventory ${D}${libexecdir}/cablemonitor/

        install -d ${D}${systemd_system_unitdir}/xyz.openbmc_project.cablemonitor.service.d
        install -m 0644 ${UNPACKDIR}/wait-inventory.conf \
            ${D}${systemd_system_unitdir}/xyz.openbmc_project.cablemonitor.service.d/wait-inventory.conf
    fi
}
