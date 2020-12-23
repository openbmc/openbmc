FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}/${MACHINE}:"
OBMC_CONSOLE_HOST_TTY = "ttyS2"

SRC_URI_append_tiogapass = " file://server.ttyS2.conf"
SRC_URI_append_yosemitev2 = " file://server.ttyS0.conf \
                              file://server.ttyS1.conf \
                              file://server.ttyS2.conf \
                              file://server.ttyS3.conf \
                              file://client.2200.conf \
                              file://client.2201.conf \
                              file://client.2202.conf \
                              file://client.2203.conf"

SRC_URI_remove = "file://${BPN}.conf"

SYSTEMD_SERVICE_${PN}_remove_yosemitev2 = "obmc-console-ssh.socket"
EXTRA_OECONF_append_yosemitev2 = " --enable-concurrent-servers"

do_install_append() {
        # Install the server configuration
        install -m 0755 -d ${D}${sysconfdir}/${BPN}
        install -m 0644 ${WORKDIR}/*.conf ${D}${sysconfdir}/${BPN}/
        # Remove upstream-provided server configuration
        rm -f ${D}${sysconfdir}/${BPN}/server.ttyVUART0.conf
}
