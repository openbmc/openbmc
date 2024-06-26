FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
RDEPENDS:${PN}:append = " bash"

inherit obmc-phosphor-systemd

SRC_URI:append = " \
    file://server.ttyUSB1.conf \
    file://server.ttyUSB6.conf \
    file://plat-80-obmc-console-uart.rules \
    "

OBMC_CONSOLE_TTYS:append = " ttyUSB1 ttyUSB6"
OBMC_SOL_ROUTING = "uart1:uart4 uart4:uart1 io1:uart2 uart2:io1"

do_install:append() {
        install -d ${D}${base_libdir}/udev/rules.d/
        install -m 0644 ${UNPACKDIR}/plat-80-obmc-console-uart.rules ${D}${base_libdir}/udev/rules.d/80-obmc-console-uart.rules
}
