FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://yosemite4-reboot.conf \
"

do_install:append() {
    install -d ${D}${systemd_system_unitdir}/xyz.openbmc_project.ObjectMapper.service.d
    install -m 0644 ${UNPACKDIR}/yosemite4-reboot.conf ${D}${systemd_system_unitdir}/xyz.openbmc_project.ObjectMapper.service.d/
}

FILES:${PN} += " \
    ${systemd_system_unitdir}/*.service \
    ${systemd_system_unitdir}/*.service.d \
    ${systemd_system_unitdir}/*.service.d/*.conf \
"
