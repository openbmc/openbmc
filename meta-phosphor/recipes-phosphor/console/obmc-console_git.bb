SUMMARY = "OpenBMC console daemon"
DESCRIPTION = "Daemon to handle UART console connections"
HOMEPAGE = "http://github.com/openbmc/obmc-console"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"
DEPENDS += "autoconf-archive-native \
            systemd \
           "
SRCREV = "ed04991236db13e25bc50cd613c348cd94fe0a83"
PACKAGECONFIG ??= "udev ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[udev] = "--with-udevdir=`pkg-config --variable=udevdir udev`,\
                       --without-udevdir,udev"
PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_system_unitdir}, \
                          --without-systemdsystemunitdir"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI += "git://github.com/openbmc/obmc-console;branch=master;protocol=https"
SRC_URI += "file://${BPN}.conf"

S = "${WORKDIR}/git"
SYSTEMD_SERVICE:${PN} += "obmc-console-ssh@.service \
                obmc-console-ssh.socket \
                obmc-console@.service \
                "

inherit autotools pkgconfig
inherit obmc-phosphor-discovery-service
inherit systemd

do_install:append() {
        # Install the server configuration
        install -m 0755 -d ${D}${sysconfdir}/${BPN}
        # If the OBMC_CONSOLE_TTYS variable is used without the default OBMC_CONSOLE_HOST_TTY
        # the port specific config file should be provided. If it is just OBMC_CONSOLE_HOST_TTY,
        # use the old style which supports both port specific or obmc-console.conf method.
        if [ "${OBMC_CONSOLE_TTYS}" !=  "${OBMC_CONSOLE_HOST_TTY}" ]; then
                rm -f ${D}${sysconfdir}/${BPN}/server.ttyVUART0.conf
                for CONSOLE in ${OBMC_CONSOLE_TTYS}
                do
                        if test -f "${WORKDIR}/server.${CONSOLE}.conf" ; then
                                install -m 0644 ${WORKDIR}/server.${CONSOLE}.conf ${D}${sysconfdir}/${BPN}/
                        else
                                bberror "Must provide port specific config files when using OBMC_CONSOLE_TTYS" \
                                        "Missing server.${CONSOLE}.conf"
                        fi
                done
        else
                # Port specific config file is prioritized over generic conf file.
                # If port specific config file is not present and generic "obmc-console.conf"
                # exists, it will be used.
                if test -f "${WORKDIR}/server.${OBMC_CONSOLE_TTYS}.conf" ; then
                        # Remove the upstream-provided server configuration
                        rm -f ${D}${sysconfdir}/${BPN}/server.ttyVUART0.conf
                        # Install the package-provided new-style configuration
                        install -m 0644 ${WORKDIR}/server.${OBMC_CONSOLE_TTYS}.conf ${D}${sysconfdir}/${BPN}/
                elif test -f "${WORKDIR}/${BPN}.conf"; then
                        # Remove the upstream-provided server configuration
                        rm -f ${D}${sysconfdir}/${BPN}/server.ttyVUART0.conf
                        # Install the old-style server configuration
                        install -m 0644 ${WORKDIR}/${BPN}.conf ${D}${sysconfdir}/
                        # Link the custom configuration to the required location
                        ln -sr ${D}${sysconfdir}/${BPN}.conf ${D}${sysconfdir}/${BPN}/server.${OBMC_CONSOLE_TTYS}.conf
                else
                        # Otherwise, remove socket-id from the shipped configuration to
                        # align with the lack of a client configuration file
                        sed -ri '/^socket-id =/d' ${D}${sysconfdir}/${BPN}/server.${OBMC_CONSOLE_TTYS}.conf
                fi
        fi
}

FILES:${PN} += "${systemd_system_unitdir}/obmc-console-ssh@.service.d/use-socket.conf"

TARGET_CFLAGS += "-fpic -O2"

REGISTERED_SERVICES:${PN} += "obmc_console:tcp:2200:"
OBMC_CONSOLE_HOST_TTY ?= "ttyVUART0"
# Support multiple TTY ports using space separated list.
# Ex. OBMC_CONSOLE_TTYS = "ttyS1 ttyS2"
OBMC_CONSOLE_TTYS ?= "${OBMC_CONSOLE_HOST_TTY}"
