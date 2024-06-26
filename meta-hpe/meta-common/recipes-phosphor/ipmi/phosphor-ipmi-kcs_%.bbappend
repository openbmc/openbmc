KCS_DEVICE = "ipmi_kcs1"
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://99-ipmi-kcs.rules"

do_install:append() {
    install -d ${D}${base_libdir}/udev/rules.d
    install -m 0644 ${UNPACKDIR}/99-ipmi-kcs.rules ${D}${base_libdir}/udev/rules.d/
}
