FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:remove:df-openpower = "file://${BPN}.conf"
SRC_URI:append:df-openpower = " file://server.ttyVUART0.conf"

install_concurrent_console_config() {
        # Install configuration for the servers and clients. Keep commandline
        # compatibility with previous configurations by defaulting to not
        # specifying a console-id for VUART0/2200
        install -m 0755 -d ${D}${sysconfdir}/${BPN}

        # Remove the default client configuration as we don't to define a
        # console-id for the 2200 console
        rm -f ${D}${sysconfdir}/${BPN}/client.2200.conf

        # However, now link to /dev/null as a way of not specifying a
        # console-id while having a configuration file present. We need to
        # provide a configuration path to meet the requirements of the packaged
        # unit file.
        ln -sr ${D}/dev/null ${D}${sysconfdir}/${BPN}/client.2200.conf

        # We need to populate console-id for remaining consoles
        install -m 0644 ${UNPACKDIR}/client.2201.conf ${D}${sysconfdir}/${BPN}/

        # Install configuration for remaining servers - the base recipe
        # installs the configuration for the first.
        install -m 0644 ${UNPACKDIR}/server.ttyVUART1.conf ${D}${sysconfdir}/${BPN}/
}

SRC_URI:append:p10bmc = " file://client.2201.conf"
SRC_URI:append:p10bmc = " file://server.ttyVUART1.conf"

REGISTERED_SERVICES:${PN}:append:p10bmc = " obmc_console_hypervisor:tcp:2201:"

SYSTEMD_SERVICE:${PN}:append:p10bmc = " obmc-console-ssh@2200.service \
		obmc-console-ssh@2201.service \
                "
SYSTEMD_SERVICE:${PN}:remove:p10bmc = "obmc-console-ssh.socket"

FILES:${PN}:remove:p10bmc = "${systemd_system_unitdir}/obmc-console-ssh@.service.d/use-socket.conf"

PACKAGECONFIG:append:p10bmc = " concurrent-servers"

do_install:append:p10bmc() {
        install_concurrent_console_config
}

SRC_URI:append:witherspoon-tacoma = " file://client.2201.conf"
SRC_URI:append:witherspoon-tacoma = " file://server.ttyVUART1.conf"

REGISTERED_SERVICES:${PN}:append:witherspoon-tacoma = " obmc_console_hypervisor:tcp:2201:"

SYSTEMD_SERVICE:${PN}:append:witherspoon-tacoma = " obmc-console-ssh@2200.service \
		obmc-console-ssh@2201.service \
                "
SYSTEMD_SERVICE:${PN}:remove:witherspoon-tacoma = "obmc-console-ssh.socket"

FILES:${PN}:remove:witherspoon-tacoma = "${systemd_system_unitdir}/obmc-console-ssh@.service.d/use-socket.conf"

EXTRA_OECONF:append:witherspoon-tacoma = " --enable-concurrent-servers"

do_install:append:witherspoon-tacoma() {
        install_concurrent_console_config
}

SRC_URI:append:sbp1 = " file://server.ttyVUART0.conf"
SRC_URI:append:system1 = " file://server.ttyVUART0.conf"
