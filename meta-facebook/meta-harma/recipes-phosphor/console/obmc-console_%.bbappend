FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
RDEPENDS:${PN}:append = " bash"

inherit obmc-phosphor-systemd

SRC_URI:append = " \
    file://setup-uart-routing \
    file://setup-uart-routing.conf \
    file://server.ttyUSB1.conf \
    file://plat-80-obmc-console-uart.rules \
    "

do_install:append() {
    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${WORKDIR}/setup-uart-routing ${D}${libexecdir}/${PN}
}

OBMC_CONSOLE_TTYS:append = " ttyUSB1"

SYSTEMD_OVERRIDE:${PN}:append = " setup-uart-routing.conf:obmc-console@ttyS2.service.d/setup-uart-routing.conf"

do_install:append() {
        install -d ${D}${base_libdir}/udev/rules.d/
        install -m 0644 ${WORKDIR}/plat-80-obmc-console-uart.rules ${D}${base_libdir}/udev/rules.d/80-obmc-console-uart.rules
}
