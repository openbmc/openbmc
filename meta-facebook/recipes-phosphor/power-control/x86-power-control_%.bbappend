FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}/${MACHINE}:"

SRC_URI_append = " file://*.json"

SYSTEMD_SERVICE_${PN} += "xyz.openbmc_project.Chassis.Control.Power@1.service \
                          xyz.openbmc_project.Chassis.Control.Power@2.service \
                          xyz.openbmc_project.Chassis.Control.Power@3.service \
                          xyz.openbmc_project.Chassis.Control.Power@4.service \
"

do_install_append() {
    install -m 0755 -d ${D}/usr/share/power-control
    install -m 0644 -D ${WORKDIR}/*.json \
                   ${D}/usr/share/power-control
}
FILES_${PN} += "/usr/share/power-control/power-config1.json"
FILES_${PN} += "/usr/share/power-control/power-config2.json"
FILES_${PN} += "/usr/share/power-control/power-config3.json"
FILES_${PN} += "/usr/share/power-control/power-config4.json"

