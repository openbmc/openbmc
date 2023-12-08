FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd

# Disable obmc-console ssh ports.
PACKAGECONFIG:remove:minerva = "ssh"

OBMC_CONSOLE_HOST_TTY = "ttyS5"
OBMC_CONSOLE_TTYS = "ttyS5"

SRC_URI:append:minerva = " \
    file://server.ttyS5.conf \
    file://80-minerva-obmc-console-uart.rules \
    file://select-uart-mux \
"

RDEPENDS:${PN}:append:minerva = " bash"

do_install:append:minerva() {

    # Replace upstream-provided udev rules
    install -d ${D}/${nonarch_base_libdir}/udev/rules.d
    rm -f ${D}/${nonarch_base_libdir}/udev/rules.d/80-obmc-console-uart.rules
    install -m 0644 ${WORKDIR}/80-minerva-obmc-console-uart.rules ${D}/${nonarch_base_libdir}/udev/rules.d

    # Install script for selecting uart mux
    install -m 0744 ${WORKDIR}/select-uart-mux ${D}${bindir}
}
