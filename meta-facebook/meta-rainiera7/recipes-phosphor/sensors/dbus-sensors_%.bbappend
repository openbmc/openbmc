FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd

PACKAGECONFIG:append = " \
    nvmesensor \
"

WAIT_POWER_SYNC ??= "true"

SRC_URI:append = " \
    ${@bb.utils.contains('WAIT_POWER_SYNC', 'true', 'file://wait-host0-state-ready file://wait-host0-state-ready.conf', '', d)} \
"

SYSTEMD_OVERRIDE:${PN}:append = " \
    ${@bb.utils.contains('WAIT_POWER_SYNC', 'true', 'wait-host0-state-ready.conf:xyz.openbmc_project.psusensor.service.d/wait-host0-state-ready.conf', '', d)} \
"

RDEPENDS:${PN}:append = " \
    ${@bb.utils.contains('WAIT_POWER_SYNC', 'true', 'bash jq', '', d)} \
"

do_install:append() {
    if [ -f ${UNPACKDIR}/wait-host0-state-ready ]; then
        install -d ${D}${libexecdir}/dbus-sensors
        install -m 0755 ${UNPACKDIR}/wait-host0-state-ready ${D}${libexecdir}/dbus-sensors/
    fi
}