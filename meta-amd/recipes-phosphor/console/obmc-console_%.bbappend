FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}/${MACHINE}:"
OBMC_CONSOLE_HOST_TTY:ethanolx = "ttyS0"

SRC_URI:remove = "file://${BPN}.conf"
SRC_URI:ethanolx += "file://server.ttyS0.conf"

do_install:append() {
        # Remove upstream-provided configuration
        rm -rf ${D}${sysconfdir}/${BPN}

        # Install the server configuration
        install -m 0755 -d ${D}${sysconfdir}/${BPN}
        install -m 0644 ${WORKDIR}/*.conf ${D}${sysconfdir}/${BPN}/

}
