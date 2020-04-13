FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}/${MACHINE}:"
OBMC_CONSOLE_HOST_TTY = "ttyS2"

SRC_URI += "file://*.conf"
SRC_URI_remove = "file://${BPN}.conf"

do_install_append() {
        # Install the server configuration
        install -m 0755 -d ${D}${sysconfdir}/${BPN}
        install -m 0644 ${WORKDIR}/*.conf ${D}${sysconfdir}/${BPN}/
        # Remove upstream-provided server configuration
        rm -f ${D}${sysconfdir}/${BPN}/server.ttyVUART0.conf
}
