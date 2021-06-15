FILESEXTRAPATHS_append := "${THISDIR}/${PN}:"
RDEPENDS_${PN} += "bash"

# Remove what installed by common recipe
OBMC_CONSOLE_HOST_TTY = ""
SYSTEMD_SUBSTITUTIONS_remove += "OBMC_CONSOLE_HOST_TTY:${OBMC_CONSOLE_HOST_TTY}:${PN}-ssh@.service"
SYSTEMD_SUBSTITUTIONS_remove += "OBMC_CONSOLE_HOST_TTY:${OBMC_CONSOLE_HOST_TTY}:${PN}-ssh.socket"
SYSTEMD_SERVICE_${PN}_remove = " \
                          ${PN}-ssh.socket \
                          ${PN}-ssh@.service \
                        "

# Declare port spcific conf and service files
HOST_CONSOLE_TTY = "ttyS0 ttyS1 ttyS2 ttyS3"

CONSOLE_CONF_FMT = "file://server.{0}.conf"
SRC_URI += "${@compose_list(d, 'CONSOLE_CONF_FMT', 'HOST_CONSOLE_TTY')}"
SRC_URI += "file://${BPN}-server-setup.sh"
SRC_URI += "file://${BPN}@.service"
SRC_URI += "file://ampere_uartmux_ctrl.sh"

CONSOLE_SSH_SOCKET_FILE_FMT = "file://${PN}-{0}-ssh.socket"
CONSOLE_SSH_SERVICE_FILE_FMT = "file://${PN}-{0}-ssh@.service"
SRC_URI += "${@compose_list(d, 'CONSOLE_SSH_SOCKET_FILE_FMT', 'HOST_CONSOLE_TTY')}"
SRC_URI += "${@compose_list(d, 'CONSOLE_SSH_SERVICE_FILE_FMT', 'HOST_CONSOLE_TTY')}"

CONSOLE_SSH_SOCKET_FMT = "${PN}-{0}-ssh.socket"
CONSOLE_SSH_SERVICE_FMT = "${PN}-{0}-ssh@.service"

SYSTEMD_SERVICE_${PN} = " \
                          ${PN}@.service \
                          ${@compose_list(d, 'CONSOLE_SSH_SOCKET_FMT', 'HOST_CONSOLE_TTY')} \
                          ${@compose_list(d, 'CONSOLE_SSH_SERVICE_FMT', 'HOST_CONSOLE_TTY')} \
                        "
do_install_append() {
    for i in ${HOST_CONSOLE_TTY}
    do
        install -m 0644 ${WORKDIR}/server.${i}.conf ${D}${sysconfdir}/${BPN}/server.${i}.conf
        install -m 0644 ${WORKDIR}/${BPN}-${i}-ssh.socket ${D}${systemd_unitdir}/system/${BPN}-${i}-ssh.socket
        install -m 0644 ${WORKDIR}/${BPN}-${i}-ssh@.service ${D}${systemd_unitdir}/system/${BPN}-${i}-ssh@.service
    done
    install -m 0755 ${WORKDIR}/${BPN}-server-setup.sh ${D}${sbindir}/${BPN}-server-setup.sh

    # Deal with files installed by the base package's .bb install function
    rm -f ${D}${sysconfdir}/${BPN}.conf
    rm -f ${D}${sysconfdir}/${BPN}/server.ttyVUART0.conf
    rm -rf ${D}${systemd_unitdir}/system/${BPN}-ssh@.service.d/
    rm -f ${D}${systemd_unitdir}/system/${BPN}-ssh@.service
    rm -f ${D}${systemd_unitdir}/system/${BPN}-ssh.socket
    # Overwrite base package's obmc-console@.service with our own
    install -m 0644 ${WORKDIR}/${BPN}@.service ${D}${systemd_unitdir}/system/${BPN}@.service
    install -d ${D}/usr/sbin
    install -m 0755 ${WORKDIR}/ampere_uartmux_ctrl.sh ${D}/${sbindir}/ampere_uartmux_ctrl.sh
}
