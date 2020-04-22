FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_remove = "file://${BPN}.conf"
SRC_URI += "file://server.ttyVUART0.conf"

install_concurrent_console_config() {
        # Install configuration for the servers and clients. Keep commandline
        # compatibility with previous configurations by defaulting to not
        # specifying a socket-id for VUART0/2200
        install -m 0755 -d ${D}${sysconfdir}/${BPN}

        # Remove the default client configuration as we don't to define a
        # socket-id for the 2200 console
        rm -f ${D}${sysconfdir}/${BPN}/client.2200.conf

        # However, now link to /dev/null as a way of not specifying a
        # socket-id while having a configuration file present. We need to
        # provide a configuration path to meet the requirements of the packaged
        # unit file.
        ln -sr ${D}/dev/null ${D}${sysconfdir}/${BPN}/client.2200.conf

        # We need to populate socket-id for remaining consoles
        install -m 0644 ${WORKDIR}/client.2201.conf ${D}${sysconfdir}/${BPN}/

        # Install configuration for remaining servers - the base recipe
        # installs the configuration for the first.
        install -m 0644 ${WORKDIR}/server.ttyVUART1.conf ${D}${sysconfdir}/${BPN}/
}

SRC_URI_append_rainier = " file://client.2201.conf"
SRC_URI_append_rainier = " file://server.ttyVUART1.conf"

REGISTERED_SERVICES_${PN}_append_rainier = " obmc_console_guests:tcp:2201:"

SYSTEMD_SERVICE_${PN}_append_rainier = " obmc-console-ssh@2200.service \
		obmc-console-ssh@2201.service \
                "
SYSTEMD_SERVICE_${PN}_remove_rainier = "obmc-console-ssh.socket"

FILES_${PN}_remove_rainier = "/lib/systemd/system/obmc-console-ssh@.service.d/use-socket.conf"

EXTRA_OECONF_append_rainier = " --enable-concurrent-servers"

do_install_append_rainier() {
        install_concurrent_console_config
}

SRC_URI_append_witherspoon-tacoma = " file://client.2201.conf"
SRC_URI_append_witherspoon-tacoma = " file://server.ttyVUART1.conf"

REGISTERED_SERVICES_${PN}_append_witherspoon-tacoma = " obmc_console_guests:tcp:2201:"

SYSTEMD_SERVICE_${PN}_append_witherspoon-tacoma = " obmc-console-ssh@2200.service \
		obmc-console-ssh@2201.service \
                "
SYSTEMD_SERVICE_${PN}_remove_witherspoon-tacoma = "obmc-console-ssh.socket"

FILES_${PN}_remove_witherspoon-tacoma = "/lib/systemd/system/obmc-console-ssh@.service.d/use-socket.conf"

EXTRA_OECONF_append_witherspoon-tacoma = " --enable-concurrent-servers"

do_install_append_witherspoon-tacoma() {
        install_concurrent_console_config
}
