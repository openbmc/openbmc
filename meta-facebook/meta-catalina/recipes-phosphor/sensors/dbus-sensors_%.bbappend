FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-dbus-service

PACKAGECONFIG:append = " \
    nvmesensor \
"

SYSTEMD_OVERRIDE:${PN}:append = "\
    wait-host0-state-ready.conf:xyz.openbmc_project.hwmontempsensor.service.d/wait-host0-state-ready.conf \
    wait-host0-state-ready.conf:xyz.openbmc_project.psusensor.service.d/wait-host0-state-ready.conf \
    "
