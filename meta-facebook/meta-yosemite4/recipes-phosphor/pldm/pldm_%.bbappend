FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://boot_config_setting_attrs.json \
"

do_install:append() {
    install -d ${D}/usr/share/pldm/bios
    install -m 0644 ${WORKDIR}/boot_config_setting_attrs.json ${D}/usr/share/pldm/bios/boot_config_setting_attrs.json
}
