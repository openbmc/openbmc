FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"
RDEPENDS:${PN} += "bash"

# Declare port spcific config files
OBMC_CONSOLE_TTYS = "ttyS0 ttyS1 ttyS2 ttyS3"
CONSOLE_CLIENT = "2200 2201 2202 2203"

SRC_URI += " \
             ${@compose_list(d, 'CONSOLE_SERVER_CONF_FMT', 'OBMC_CONSOLE_TTYS')} \
             ${@compose_list(d, 'CONSOLE_CLIENT_CONF_FMT', 'CONSOLE_CLIENT')} \
             file://ampere_uart_console_setup.sh \
           "

SYSTEMD_SERVICE:${PN}:append = " \
                                  ${@compose_list(d, 'CONSOLE_CLIENT_SERVICE_FMT', 'CONSOLE_CLIENT')} \
                                "

do_install:append() {
    # Script to set host's uart muxes to BMC
    install -m 0755 ${WORKDIR}/ampere_uart_console_setup.sh ${D}${sbindir}

    # Install the console client configurations
    install -m 0644 ${WORKDIR}/client.*.conf ${D}${sysconfdir}/${BPN}
}
