FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://boot_config_setting_attrs.json \
    file://nic-powercycle \
    file://nic-powercycle@.service \
"

FILES:${PN}:append = " \
    ${systemd_system_unitdir}/nic-powercycle \
"

SYSTEMD_SERVICE:${PN} += " \
    nic-powercycle@.service \
    "

do_install:append() {
    install -d ${D}/usr/share/pldm/bios
    install -m 0644 ${UNPACKDIR}/boot_config_setting_attrs.json ${D}/usr/share/pldm/bios/boot_config_setting_attrs.json

    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/nic-powercycle ${D}${libexecdir}/${PN}
    install -m 0644 ${UNPACKDIR}/nic-powercycle@.service ${D}${systemd_system_unitdir}
}
