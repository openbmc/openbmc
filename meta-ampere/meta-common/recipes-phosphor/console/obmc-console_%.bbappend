FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"
RDEPENDS:${PN} += "bash"

CONSOLE_CLIENT_SERVICE_FMT = "obmc-console-ssh@{0}.service"
CONSOLE_SERVER_CONF_FMT = "file://server.{0}.conf"
CONSOLE_CLIENT_CONF_FMT = "file://client.{0}.conf"

SRC_URI += " \
             file://ampere_uartmux_ctrl.sh \
             file://obmc-console@.service \
           "

SYSTEMD_SERVICE:${PN}:remove = "obmc-console-ssh.socket"

FILES:${PN}:remove = "${systemd_system_unitdir}/obmc-console-ssh@.service.d/use-socket.conf"

PACKAGECONFIG:append = " concurrent-servers"

do_install:append() {
    # Script to switch host's uart muxes by GPIOs
    install -d ${D}${sbindir}
    install -m 0755 ${WORKDIR}/ampere_uartmux_ctrl.sh ${D}/${sbindir}

    # Overriding service to call ampere_uart_console_setup.sh at ExecStartPre
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/obmc-console@.service ${D}${systemd_system_unitdir}
}
