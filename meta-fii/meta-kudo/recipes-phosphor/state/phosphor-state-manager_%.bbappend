FILESEXTRAPATHS:prepend:kudo := "${THISDIR}/${PN}:"

CHASSIS_ACTION_TARGETS:append:kudo = " powercycle"

SRC_URI:append:kudo = " \
    file://xyz.openbmc_project.State.Chassis.service \
    "

do_install:append:kudo() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/xyz.openbmc_project.State.Chassis.service ${D}${systemd_system_unitdir}/xyz.openbmc_project.State.Chassis.service
}

