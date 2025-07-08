FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append = " file://mctp-usb.rules"

do_install:append() {
    install -d ${D}${nonarch_base_libdir}/udev/rules.d
    install -m 0644 ${UNPACKDIR}/mctp-usb.rules ${D}${nonarch_base_libdir}/udev/rules.d/82-mctp-usb.rules
}
