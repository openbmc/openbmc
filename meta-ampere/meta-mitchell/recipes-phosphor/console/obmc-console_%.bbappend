FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"
RDEPENDS:${PN} += "bash"

# Declare port specific config files
OBMC_CONSOLE_TTYS = "ttyS0 ttyS1 ttyS2 ttyS3 ttyS7 ttyS8"
CONSOLE_CLIENT = "2200 2201 2202 2203 2204 2205"

SRC_URI += " \
             ${@compose_list(d, 'CONSOLE_SERVER_CONF_FMT', 'OBMC_CONSOLE_TTYS')} \
             ${@compose_list(d, 'CONSOLE_CLIENT_CONF_FMT', 'CONSOLE_CLIENT')} \
           "

SYSTEMD_SERVICE:${PN}:append = " \
                                  ${@compose_list(d, 'CONSOLE_CLIENT_SERVICE_FMT', 'CONSOLE_CLIENT')} \
                                "

do_install:append() {
    # Install the console client configurations
    install -m 0644 ${UNPACKDIR}/client.*.conf ${D}${sysconfdir}/${BPN}
}
