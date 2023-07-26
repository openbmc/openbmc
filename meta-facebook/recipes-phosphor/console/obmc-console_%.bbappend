FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# Disable obmc-console ssh ports.
PACKAGECONFIG:remove = "ssh"

OBMC_CONSOLE_HOST_TTY = "ttyS2"
OBMC_CONSOLE_TTYS = "${@' '.join(['ttyS{}'.format(i) for i in range(int(d.getVar('OBMC_CONSOLE_INST_CNT', True)) + 1) if 'ttyS{}'.format(i) != d.getVar('OBMC_BMC_TTY', True)])}"
OBMC_BMC_TTY = "ttyS4"

OBMC_CONSOLE_INST = "${@d.getVar('OBMC_HOST_INSTANCES', True)}"
OBMC_CONSOLE_INST_CNT = "${@sum([1 for item in d.getVar('OBMC_CONSOLE_INST', True).split() if item.isdigit()])}"
SERVER_CONFS = "${@' '.join(['file://server.ttyS{}.conf'.format(i) for i in range(int(d.getVar('OBMC_CONSOLE_INST_CNT', True)) + 1) if 'ttyS{}'.format(i) != d.getVar('OBMC_BMC_TTY', True)])}"

SRC_URI:append:fb-compute-singlehost = " file://server.ttyS2.conf"

SRC_URI:append:fb-compute-multihost = " ${SERVER_CONFS} \
                                        file://client.2200.conf \
                                        file://client.2201.conf \
                                        file://client.2202.conf \
                                        file://client.2203.conf"

CLIENT_SERVICE_FILES_FMT = "file://${BPN}-{0}-ssh-host@.service \
                            file://${BPN}-{0}-ssh-bic@.service"

SRC_URI:append:fb-compute-multihost = " \
                        ${@compose_list(d, 'CLIENT_SERVICE_FILES_FMT', 'OBMC_CONSOLE_TTYS')}"

CLIENT_SERVICE_FMT = "${PN}-{0}-ssh-host@.service \
                      ${PN}-{0}-ssh-bic@.service"

SYSTEMD_SERVICE:${PN}:append:fb-compute-multihost = " \
                        ${@compose_list(d, 'CLIENT_SERVICE_FMT', 'OBMC_CONSOLE_TTYS')} \
                        "

SRC_URI:remove = "file://${BPN}.conf"

SYSTEMD_SERVICE:${PN}:remove:fb-compute-multihost = "obmc-console-ssh@.service"

PACKAGECONFIG:append:fb-compute-multihost = " concurrent-servers"

do_install:append() {
        # Install the server configuration
        install -m 0755 -d ${D}${sysconfdir}/${BPN}
        install -m 0644 ${WORKDIR}/*.conf ${D}${sysconfdir}/${BPN}/
        # Remove upstream-provided server configuration
        rm -f ${D}${sysconfdir}/${BPN}/server.ttyVUART0.conf
}

do_install:append:fb-compute-multihost() {
        # Implement a script called "select-uart-mux" in project layer and follow the interface below:
        # Usage: select-uart-mux <slot1|slot2|slot3|slot4> <host|bic>
        install -m 0644 ${WORKDIR}/${BPN}-*-ssh-bic@.service ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/${BPN}-*-ssh-host@.service ${D}${systemd_system_unitdir}

        rm -rf ${D}${systemd_system_unitdir}/obmc-console-ssh@.service.d/
        rm -f ${D}${systemd_system_unitdir}/${BPN}-ssh@.service
}
