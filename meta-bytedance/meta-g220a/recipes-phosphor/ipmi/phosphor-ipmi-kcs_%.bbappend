FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

KCS_DEVICE = "ipmi_kcs3"
SMM_DEVICE = "ipmi_kcs4"
SYSTEMD_SERVICE_${PN}_append = " ${PN}@${SMM_DEVICE}.service "

SRC_URI += "file://99-ipmi-kcs.rules"

do_install_append() {
    install -d ${D}${base_libdir}/udev/rules.d
    install -m 0644 ${WORKDIR}/99-ipmi-kcs.rules ${D}${base_libdir}/udev/rules.d/
}
