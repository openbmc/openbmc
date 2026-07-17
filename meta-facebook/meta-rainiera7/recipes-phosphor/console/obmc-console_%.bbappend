FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
RDEPENDS:${PN}:append = " bash"

SRC_URI:append = " \
    file://server.ttyS3.conf \
    "

OBMC_CONSOLE_TTYS:append = " ttyS3"

SRC_URI:append:rainiera7 = " \
    file://plat-80-obmc-console-uart.rules \
"

do_install:append:rainiera7() {
    install -d ${D}${base_libdir}/udev/rules.d/
    install -m 0644 ${UNPACKDIR}/plat-80-obmc-console-uart.rules ${D}${base_libdir}/udev/rules.d/80-obmc-console-uart.rules
}
