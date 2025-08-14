FILESEXTRAPATHS:prepend := "${THISDIR}/obmc-console:"

inherit obmc-phosphor-systemd

SRC_URI += " \
    file://81-plat-obmc-console-uart.rules \
"

do_install:append() {
    install -d ${D}${base_libdir}/udev/rules.d/
    install -m 0644 ${UNPACKDIR}/81-plat-obmc-console-uart.rules ${D}${base_libdir}/udev/rules.d/81-plat-obmc-console-uart.rules
}
