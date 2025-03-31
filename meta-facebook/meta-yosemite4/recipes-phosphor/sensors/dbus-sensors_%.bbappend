FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# Temporary hack: Adjusting the startup sequence of xyz.openbmc_project.fansensor.service
# to address issues during BMC startup due to a busy D-Bus causing GetSubtree to fail.
# This change moves the service to start after multi-user.target and before phosphor-pid-control.
# This approach is not intended for long-term use.
FILES:${PN}:append = " \
    ${systemd_system_unitdir}/xyz.openbmc_project.fansensor.service.d/*.conf \
"

SRC_URI += " \
    file://yosemite4-fansensor.conf \
"

do_install:append() {
    install -d ${D}${systemd_system_unitdir}/xyz.openbmc_project.fansensor.service.d
    install -m 0644 ${UNPACKDIR}/yosemite4-fansensor.conf ${D}${systemd_system_unitdir}/xyz.openbmc_project.fansensor.service.d/
}
