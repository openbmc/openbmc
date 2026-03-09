FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://plat-80-obmc-console-uart.rules \
"

# For BIOS 2A03/older compatibility
OBMC_SOL_ROUTING = "uart2:uart3 uart3:uart2"

do_install:append() {
    install -d ${D}${base_libdir}/udev/rules.d/
    install -m 0644 ${UNPACKDIR}/plat-80-obmc-console-uart.rules ${D}${base_libdir}/udev/rules.d/80-obmc-console-uart.rules
}
