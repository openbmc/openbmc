FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://yosemite4.conf \
    file://boot_config_setting_attrs.json \
    file://nic-powercycle \
    file://nic-powercycle@.service \
    file://pldmd-exit-handler.service \
    file://pldmd-exit-handler \
"

FILES:${PN}:append = " \
    ${systemd_system_unitdir}/pldmd.service.d/*.conf \
    ${systemd_system_unitdir}/nic-powercycle \
    ${systemd_system_unitdir}/pldmd-exit-handler \
"

SYSTEMD_SERVICE:${PN} += " \
    nic-powercycle@.service \
    pldmd-exit-handler.service \
    "

do_install:append() {
    install -d ${D}${systemd_system_unitdir}/pldmd.service.d
    install -m 0644 ${UNPACKDIR}/yosemite4.conf ${D}${systemd_system_unitdir}/pldmd.service.d/

    install -d ${D}/usr/share/pldm/bios
    install -m 0644 ${UNPACKDIR}/boot_config_setting_attrs.json ${D}/usr/share/pldm/bios/boot_config_setting_attrs.json

    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/nic-powercycle ${D}${libexecdir}/${PN}
    install -m 0644 ${UNPACKDIR}/nic-powercycle@.service ${D}${systemd_system_unitdir}
    install -m 0755 ${UNPACKDIR}/pldmd-exit-handler ${D}${libexecdir}/${PN}/
    install -m 0644 ${UNPACKDIR}/pldmd-exit-handler.service ${D}${systemd_system_unitdir}
}
