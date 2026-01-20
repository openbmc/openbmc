FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

OBMC_CONSOLE_HOST_TTY = "ttyS0"

SRC_URI:remove = "file://${BPN}.conf"

SRC_URI:append = "\
        file://server.ttyS0.conf \
        file://server.ttyVUART0.conf \
        file://client.2200.conf \
        file://client.2201.conf \
"

do_install:append() {
        # Remove upstream-provided configuration
        rm -rf ${D}${sysconfdir}/${BPN}

        # Install the server configuration
        install -m 0755 -d ${D}${sysconfdir}/${BPN}
        install -m 0644 ${UNPACKDIR}/*.conf ${D}${sysconfdir}/${BPN}/

        # Install the services in the multi-user.target
        install -m 0644 -d ${D}${systemd_unitdir}/system/multi-user.target.wants
        ln -s ../obmc-console-ssh@.service ${D}${systemd_unitdir}/system/multi-user.target.wants/obmc-console-ssh@2200.service
        ln -s ../obmc-console-ssh@.service ${D}${systemd_unitdir}/system/multi-user.target.wants/obmc-console-ssh@2201.service
}

EXTRA_OECONF:append = " --enable-concurrent-servers"

SYSTEMD_SERVICE_${PN}:remove = "obmc-console-ssh.socket"

SYSTEMD_SERVICE_${PN}:append = " obmc-console-ssh@2200.service \
        obmc-console-ssh@2201.service \
"

REGISTERED_SERVICES_${PN}:append = " obmc_console_host0:tcp:2200: \
        obmc_console_host1:tcp:2201: \
"

FILES_${PN}:remove = "${systemd_system_unitdir}/obmc-console-ssh@.service.d/use-socket.conf"
