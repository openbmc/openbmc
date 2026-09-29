FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append = " \
    file://mctp-usb.rules \
    file://mctp-i2c.rules \
"

do_install:append() {
    install -d ${D}${nonarch_base_libdir}/udev/rules.d
    install -m 0644 ${UNPACKDIR}/mctp-usb.rules ${D}${nonarch_base_libdir}/udev/rules.d/82-mctp-usb.rules
    install -m 0644 ${UNPACKDIR}/mctp-i2c.rules ${D}${nonarch_base_libdir}/udev/rules.d/82-mctp-i2c.rules
}
