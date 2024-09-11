FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd

OBMC_CONSOLE_TTYS:fb-nohost:append = " ttyS5"

SRC_URI:append = " \
    file://80-minerva-obmc-console-uart.rules \
"

RDEPENDS:${PN}:append = " bash"

do_install:append() {

    # Replace upstream-provided udev rules
    install -d ${D}/${nonarch_base_libdir}/udev/rules.d
    rm -f ${D}/${nonarch_base_libdir}/udev/rules.d/80-obmc-console-uart.rules
    install -m 0644 ${WORKDIR}/80-minerva-obmc-console-uart.rules ${D}/${nonarch_base_libdir}/udev/rules.d
}
