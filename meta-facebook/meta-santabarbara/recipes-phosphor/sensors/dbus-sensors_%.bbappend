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

inherit obmc-phosphor-systemd
RDEPENDS:${PN}:append = " bash jq"

SYSTEMD_SERVICE:${PN}:append = " xyz.openbmc_project.cablemonitor.service"
SYSTEMD_OVERRIDE:${PN}:append = " \
    wait-inventory.conf:xyz.openbmc_project.cablemonitor.service.d/wait-inventory.conf \
    "

do_install:append:() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'cablemonitor', 'true', 'false', d)}; then
        install -d ${D}/var/lib/cablemonitor
        install -m 0755 ${UNPACKDIR}/cable-config.json ${D}/var/lib/cablemonitor/cable-config.json

        install -d ${D}${libexecdir}/cablemonitor
        install -m 0755 ${UNPACKDIR}/wait-cable-inventory ${D}${libexecdir}/cablemonitor/
    fi
}
