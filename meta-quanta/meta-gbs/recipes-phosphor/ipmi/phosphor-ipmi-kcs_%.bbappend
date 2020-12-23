KCS_DEVICE_gbs = "ipmi_kcs1"

FILESEXTRAPATHS_prepend_gbs := "${THISDIR}/${PN}:"
SRC_URI_append_gbs = " file://99-ipmi-kcs.rules"

do_install_append_gbs() {
        install -d ${D}/lib/udev/rules.d
        install -m 0644 ${WORKDIR}/99-ipmi-kcs.rules ${D}/lib/udev/rules.d
}
