SUMMARY = "OpenBMC console daemon"
DESCRIPTION = "Daemon to handle UART console connections"
HOMEPAGE = "http://github.com/openbmc/obmc-console"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

inherit autotools pkgconfig
inherit obmc-phosphor-discovery-service
inherit systemd

S = "${WORKDIR}/git"

TARGET_CFLAGS += "-fpic -O2"

PACKAGECONFIG ??= "udev ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[udev] = "--with-udevdir=`pkg-config --variable=udevdir udev`,\
                       --without-udevdir,udev"
PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_system_unitdir}, \
                          --without-systemdsystemunitdir"

DEPENDS += "autoconf-archive-native \
            systemd \
           "

SRC_URI += "git://github.com/openbmc/obmc-console"
SRC_URI += "file://${BPN}.conf"

SRCREV = "d802b11942abc8a4641976e70d567758ef0bbd58"
PV = "1.0+git${SRCPV}"

REGISTERED_SERVICES_${PN} += "obmc_console:tcp:2200:"

SYSTEMD_SERVICE_${PN} += "obmc-console-ssh@.service \
                obmc-console-ssh.socket \
                obmc-console@.service \
                "

FILES_${PN} += "/lib/systemd/system/obmc-console-ssh@.service.d/use-socket.conf"

do_install_append() {
        # Install the server configuration
        install -m 0755 -d ${D}${sysconfdir}/${BPN}
        if test -f "${WORKDIR}/${BPN}.conf"; then
                # Remove the upstream-provided server configuration
                rm -f ${D}${sysconfdir}/${BPN}/server.ttyVUART0.conf
                # Install the old-style server configuration
                install -m 0644 ${WORKDIR}/${BPN}.conf ${D}${sysconfdir}/
                # Link the custom configuration to the required location
                ln -sr ${D}${sysconfdir}/${BPN}.conf ${D}${sysconfdir}/${BPN}/server.ttyVUART0.conf
        elif test -f "${WORKDIR}/server.ttyVUART0.conf" ; then
                # Remove the upstream-provided server configuration
                rm -f ${D}${sysconfdir}/${BPN}/server.ttyVUART0.conf
                # Install the package-provided new-style configuration
                install -m 0644 ${WORKDIR}/server.ttyVUART0.conf ${D}${sysconfdir}/${BPN}/
        else
                # Otherwise, remove socket-id from the shipped configuration to
                # align with the lack of a client configuration file
                sed -ri '/^socket-id =/d' ${D}${sysconfdir}/${BPN}/server.ttyVUART0.conf
        fi
}
