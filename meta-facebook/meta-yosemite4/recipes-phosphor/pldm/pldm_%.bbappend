FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://boot_config_setting_attrs.json \
    file://pldm_nic_power_cycle \
    file://pldm_nic_power_cycle@.service \
"

FILES:${PN}:append = " \
    ${systemd_system_unitdir}/pldm_nic_power_cycle \
"

SYSTEMD_SERVICE:${PN} += " \
    pldm_nic_power_cycle@.service \
    "

do_install:append() {
    install -d ${D}/usr/share/pldm/bios
    install -m 0644 ${UNPACKDIR}/boot_config_setting_attrs.json ${D}/usr/share/pldm/bios/boot_config_setting_attrs.json

    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/pldm_nic_power_cycle ${D}${libexecdir}/${PN}/
    install -m 0644 ${UNPACKDIR}/pldm_nic_power_cycle@.service ${D}${systemd_system_unitdir}
}
