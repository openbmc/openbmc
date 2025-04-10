FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"
OBMC_CONSOLE_HOST_TTY = "ttyS2"

SRC_URI:append = " \
    file://server.obmc-console.conf \
    file://client.obmc-console.conf \
"

do_install:append() {
    # Install the console client configurations
    install -m 0644 ${UNPACKDIR}/client.*.conf ${D}${sysconfdir}/${BPN}
}
