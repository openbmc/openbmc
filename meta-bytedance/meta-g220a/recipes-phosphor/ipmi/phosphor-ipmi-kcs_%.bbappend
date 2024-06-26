FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

KCS_DEVICE = "ipmi_kcs3"
SMM_DEVICE = "ipmi_kcs4"
SYSTEMD_SERVICE:${PN}:append = " ${PN}@${SMM_DEVICE}.service "

SRC_URI += "file://99-ipmi-kcs.rules"

do_install:append() {
    install -d ${D}${nonarch_base_libdir}/udev/rules.d
    install -m 0644 ${UNPACKDIR}/99-ipmi-kcs.rules ${D}${nonarch_base_libdir}/udev/rules.d/
}
