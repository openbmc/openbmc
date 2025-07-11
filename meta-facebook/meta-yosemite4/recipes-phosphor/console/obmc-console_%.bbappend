FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://plat-80-obmc-console-uart.rules \
    file://99-terminus-usb-serial.rules \
"

do_install:append() {
        install -d ${D}${base_libdir}/udev/rules.d/
        install -m 0644 ${UNPACKDIR}/plat-80-obmc-console-uart.rules ${D}${base_libdir}/udev/rules.d/80-obmc-console-uart.rules

        install -d ${D}${sysconfdir}/udev/rules.d/
        install -m 0644 ${UNPACKDIR}/99-terminus-usb-serial.rules ${D}${sysconfdir}/udev/rules.d/99-terminus-usb-serial.rules
}
