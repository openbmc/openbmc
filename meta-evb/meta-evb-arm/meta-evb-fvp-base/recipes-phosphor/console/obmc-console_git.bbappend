FILESEXTRAPATHS:append := ":${THISDIR}/${PN}"

OBMC_CONSOLE_HOST_TTY = "ttyAMA3"

SRC_URI += " \
    file://server.ttyAMA3.conf \
    file://80-obmc-console-ttyAMA3-uart.rules \
"

do_install:append() {
    install -d ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${UNPACKDIR}/80-obmc-console-ttyAMA3-uart.rules ${D}${sysconfdir}/udev/rules.d/
}

FILES_${PN} += "${sysconfdir}/udev/rules.d/80-obmc-console-ttyAMA3-uart.rules"
